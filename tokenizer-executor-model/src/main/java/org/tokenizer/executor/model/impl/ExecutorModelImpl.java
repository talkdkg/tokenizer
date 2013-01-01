/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.executor.model.impl;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeChildrenChanged;
import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.util.Logs;
import org.tokenizer.util.zookeeper.ZkLock;
import org.tokenizer.util.zookeeper.ZkLockException;
import org.tokenizer.util.zookeeper.ZkUtil;
import org.tokenizer.util.zookeeper.ZooKeeperItf;
import org.tokenizer.util.zookeeper.ZooKeeperOperation;

public class ExecutorModelImpl implements WritableExecutorModel {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(ExecutorModelImpl.class);
    private final ZooKeeperItf zk;
    /**
     * Cache of the tasks as they are stored in ZK. Updated based on ZK watcher
     * events. Updates to this cache should synchronize on {@link #model_lock}.
     */
    private final Map<UUID, TaskInfoBean> tasksMap = new ConcurrentHashMap<UUID, TaskInfoBean>(
            16, 0.75f, 1);
    private final Object model_lock = new Object();
    private final Set<ExecutorModelListener> listeners = Collections
            .newSetFromMap(new IdentityHashMap<ExecutorModelListener, Boolean>());
    private final Watcher watcher = new ModelChangeWatcher();
    private final Watcher connectStateWatcher = new ConnectStateWatcher();
    private final ModelCacheRefresher modelCacheRefresher = new ModelCacheRefresher();
    private boolean stopped = false;
    private static final String EXECUTOR_COLLECTION_PATH = "/org/tokenizer/executor/tasks";
    private static final String EXECUTOR_TRASH_PATH = "/org/tokenizer/executor/tasks-trash";

    public ExecutorModelImpl(final ZooKeeperItf zk)
            throws InterruptedException, KeeperException {
        this.zk = zk;
        ZkUtil.createPath(zk, EXECUTOR_COLLECTION_PATH);
        ZkUtil.createPath(zk, EXECUTOR_TRASH_PATH);
        zk.addDefaultWatcher(connectStateWatcher);
        modelCacheRefresher.start();
        modelCacheRefresher.waitUntilStarted();
    }

    @PreDestroy
    public void stop() throws InterruptedException {
        stopped = true;
        zk.removeDefaultWatcher(connectStateWatcher);
        modelCacheRefresher.shutdown();
    }

    @Override
    public void addTask(final TaskInfoBean task) throws TaskExistsException,
            TaskModelException, TaskValidityException {
        assertValid(task);
        final String taskPath = EXECUTOR_COLLECTION_PATH + "/" + task.getUuid();
        final byte[] data = TaskInfoBeanConverter.toJsonBytes(task);
        try {
            zk.retryOperation(new ZooKeeperOperation<String>() {

                @Override
                public String execute() throws KeeperException,
                        InterruptedException {
                    return zk.create(taskPath, data,
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            });
        } catch (KeeperException.NodeExistsException e) {
            throw new TaskExistsException(task.getUuid().toString());
        } catch (Exception e) {
            throw new TaskModelException("Error creating Task.", e);
        }
    }

    private void assertValid(final TaskInfoBean task)
            throws TaskValidityException {
        if (task.getTaskConfiguration() == null)
            throw new TaskValidityException("Configuration should not be null.");
        if (task.getTaskConfiguration().getGeneralState() == null)
            throw new TaskValidityException("General state should not be null.");
    }

    @Override
    public void updateTaskInternal(final TaskInfoBean task)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            TaskValidityException {
        assertValid(task);
        final byte[] newData = TaskInfoBeanConverter.toJsonBytes(task);
        try {
            zk.retryOperation(new ZooKeeperOperation<Stat>() {

                @Override
                public Stat execute() throws KeeperException,
                        InterruptedException {
                    return zk.setData(
                            EXECUTOR_COLLECTION_PATH + "/" + task.getUuid(),
                            newData, task.getZkDataVersion());
                }
            });
        } catch (KeeperException.NoNodeException e) {
            throw new TaskNotFoundException(task.getUuid());
        } catch (KeeperException.BadVersionException e) {
            throw new TaskConcurrentModificationException(task.getUuid()
                    .toString());
        }
    }

    @Override
    public void updateTask(final TaskInfoBean task, final String lock)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            ZkLockException, TaskUpdateException, TaskValidityException {
        if (!ZkLock.ownsLock(zk, lock))
            throw new TaskUpdateException(
                    "You are not owner of the Task lock, your lock path is: "
                            + lock);
        assertValid(task);
        TaskInfoBean currentTask = getMutableTask(task.getUuid());
        if (currentTask.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED)
            throw new TaskUpdateException("Task in the state "
                    + task.getTaskConfiguration().getGeneralState()
                    + " cannot be modified.");
        updateTaskInternal(task);
    }

    @Override
    public void deleteTask(final UUID uuid) throws TaskModelException {
        final String taskPath = EXECUTOR_COLLECTION_PATH + "/" + uuid;
        final String taskLockPath = taskPath + "/lock";
        try {
            // Make a copy of the Task data in the executor trash
            zk.retryOperation(new ZooKeeperOperation<Object>() {

                @Override
                public Object execute() throws KeeperException,
                        InterruptedException {
                    byte[] data = zk.getData(taskPath, false, null);
                    String trashPath = EXECUTOR_TRASH_PATH + "/" + uuid;
                    // Task with the same name might have existed
                    // before and
                    // hence already exist in the executor trash, handle this by
                    // appending
                    // a sequence number until a unique name is found.
                    String baseTrashpath = trashPath;
                    int count = 0;
                    while (zk.exists(trashPath, false) != null) {
                        count++;
                        trashPath = baseTrashpath + "." + count;
                    }
                    zk.create(trashPath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                    return null;
                }
            });
            // The loop below is normally not necessary, since we disallow
            // taking new
            // locks on tasks which are being deleted.
            int tryCount = 0;
            while (true) {
                boolean success = zk
                        .retryOperation(new ZooKeeperOperation<Boolean>() {

                            @Override
                            public Boolean execute() throws KeeperException,
                                    InterruptedException {
                                try {
                                    // Delete the Task lock if it
                                    // exists
                                    if (zk.exists(taskLockPath, false) != null) {
                                        List<String> children = Collections
                                                .emptyList();
                                        try {
                                            children = zk.getChildren(
                                                    taskLockPath, false);
                                        } catch (KeeperException.NoNodeException e) {
                                            // ok
                                        }
                                        for (String child : children) {
                                            try {
                                                zk.delete(taskLockPath + "/"
                                                        + child, -1);
                                            } catch (KeeperException.NoNodeException e) {
                                                // ignore, node was already
                                                // removed
                                            }
                                        }
                                        try {
                                            zk.delete(taskLockPath, -1);
                                        } catch (KeeperException.NoNodeException e) {
                                            // ignore
                                        }
                                    }
                                    zk.delete(taskPath, -1);
                                    return true;
                                } catch (KeeperException.NotEmptyException e) {
                                    // Someone again took a lock on the
                                    // Task, retry
                                }
                                return false;
                            }
                        });
                if (success) {
                    break;
                }
                tryCount++;
                if (tryCount > 10)
                    throw new TaskModelException(
                            "Failed to delete the Task because it still has child data: {}"
                                    + uuid);
            }
        } catch (Throwable t) {
            if (t instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new TaskModelException("Failed to delete Task " + uuid, t);
        }
    }

    @Override
    public String lockTaskInternal(final UUID uuid, final boolean checkDeleted)
            throws ZkLockException, TaskNotFoundException,
            InterruptedException, KeeperException, TaskModelException {
        TaskInfoBean task = getTask(uuid);
        if (checkDeleted) {
            if (task.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED)
                throw new TaskModelException("A task in state "
                        + task.getTaskConfiguration().getGeneralState()
                        + " cannot be locked.");
        }
        final String lockPath = EXECUTOR_COLLECTION_PATH + "/" + uuid + "/lock";
        //
        // Create the lock path if necessary
        //
        Stat stat = zk.retryOperation(new ZooKeeperOperation<Stat>() {

            @Override
            public Stat execute() throws KeeperException, InterruptedException {
                return zk.exists(lockPath, null);
            }
        });
        if (stat == null) {
            // We do not make use of ZkUtil.createPath (= recursive path
            // creation) on
            // purpose, because if the parent path does not exist, this means
            // the
            // Task does not exist, and we do not want to create a
            // task
            // path (with null data) like that.
            try {
                zk.retryOperation(new ZooKeeperOperation<String>() {

                    @Override
                    public String execute() throws KeeperException,
                            InterruptedException {
                        return zk.create(lockPath, null,
                                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.PERSISTENT);
                    }
                });
            } catch (KeeperException.NodeExistsException e) {
                // ok, someone created it since we checked
            } catch (KeeperException.NoNodeException e) {
                throw new TaskNotFoundException(uuid);
            }
        }
        return ZkLock.lock(zk, lockPath);
    }

    @Override
    public String lockTask(final UUID uuid) throws ZkLockException,
            TaskNotFoundException, InterruptedException, KeeperException,
            TaskModelException {
        return lockTaskInternal(uuid, true);
    }

    @Override
    public void unlockTask(final String lock) throws ZkLockException {
        ZkLock.unlock(zk, lock);
    }

    @Override
    public void unlockTask(final String lock, final boolean ignoreMissing)
            throws ZkLockException {
        ZkLock.unlock(zk, lock, ignoreMissing);
    }

    @Override
    public TaskInfoBean getTask(final UUID uuid) throws TaskNotFoundException {
        TaskInfoBean task = tasksMap.get(uuid);
        if (task == null)
            throw new TaskNotFoundException(uuid);
        return task;
    }

    @Override
    public boolean hasTask(final UUID uuid) {
        return tasksMap.containsKey(uuid);
    }

    @Override
    public TaskInfoBean getMutableTask(final UUID uuid)
            throws InterruptedException, KeeperException, TaskNotFoundException {
        return loadTask(uuid, false);
    }

    @Override
    public Collection<TaskInfoBean> getTasks() {
        return new ArrayList<TaskInfoBean>(tasksMap.values());
    }

    @Override
    public Collection<TaskInfoBean> getTasks(
            final ExecutorModelListener listener) {
        synchronized (model_lock) {
            registerListener(listener);
            return new ArrayList<TaskInfoBean>(tasksMap.values());
        }
    }

    private TaskInfoBean loadTask(final UUID uuid, final boolean forCache)
            throws InterruptedException, KeeperException, TaskNotFoundException {
        final String childPath = EXECUTOR_COLLECTION_PATH + "/" + uuid;
        final Stat stat = new Stat();
        byte[] data;
        try {
            if (forCache) {
                // do not retry, install watcher
                data = zk.getData(childPath, watcher, stat);
            } else {
                // do retry, do not install watcher
                data = zk.retryOperation(new ZooKeeperOperation<byte[]>() {

                    @Override
                    public byte[] execute() throws KeeperException,
                            InterruptedException {
                        return zk.getData(childPath, false, stat);
                    }
                });
            }
        } catch (KeeperException.NoNodeException e) {
            throw new TaskNotFoundException(uuid);
        }
        TaskInfoBean task = TaskInfoBeanConverter.fromJsonBytes(data);
        task.setZkDataVersion(stat.getVersion());
        return task;
    }

    // @see com.vaadin.event.EventRouter for best implementation
    private void notifyListeners(final List<ExecutorModelEvent> events) {
        if (events != null) {
            // To avoid ConcurrentModificationException:
            final Object[] eventArray = events.toArray();
            for (int i = 0; i < eventArray.length; i++) {
                fireEvent((ExecutorModelEvent) eventArray[i]);
            }
        }
    }

    /**
     * Sends an event to all registered listeners. The listeners will decide if
     * the activation method should be called or not.
     * 
     * @see com.vaadin.event.EventRouter for best implementation
     * 
     * @param event
     *            the Event to be sent to all listeners.
     */
    public void fireEvent(final ExecutorModelEvent event) {
        // It is not necessary to send any events if there are no listeners
        if (listeners != null) {
            // To avoid ConcurrentModificationException:
            final Object[] listenerArray = listeners.toArray();
            for (int i = 0; i < listenerArray.length; i++) {
                ((ExecutorModelListener) listenerArray[i]).process(event);
            }
        }
    }

    @Override
    public void registerListener(final ExecutorModelListener listener) {
        this.listeners.add(listener);
    }

    // TODO: enforce "unregister" to prevent OOM...
    @Override
    public void unregisterListener(final ExecutorModelListener listener) {
        this.listeners.remove(listener);
    }

    private class ModelChangeWatcher implements Watcher {

        @Override
        public void process(final WatchedEvent event) {
            if (stopped)
                return;
            try {
                if (NodeChildrenChanged.equals(event.getType())
                        && event.getPath().equals(EXECUTOR_COLLECTION_PATH)) {
                    modelCacheRefresher.triggerRefreshAll();
                } else if (NodeDataChanged.equals(event.getType())
                        && event.getPath().startsWith(
                                EXECUTOR_COLLECTION_PATH + "/")) {
                    String uuidName = event.getPath().substring(
                            EXECUTOR_COLLECTION_PATH.length() + 1);
                    modelCacheRefresher.triggerTaskToRefresh(UUID
                            .fromString(uuidName));
                }
            } catch (Throwable t) {
                LOG.error("Event: " + event, t);
            }
        }
    }

    public class ConnectStateWatcher implements Watcher {

        @Override
        public void process(final WatchedEvent event) {
            if (stopped)
                return;
            if (event.getType() == Event.EventType.None
                    && event.getState() == Event.KeeperState.SyncConnected) {
                // Each time the connection is established, we trigger
                // refreshing, since
                // the previous refresh
                // might have failed with a ConnectionLoss exception
                modelCacheRefresher.triggerRefreshAll();
            }
        }
    }

    /**
     * Responsible for updating our internal cache of Tasks. Should be triggered
     * upon each related change on ZK, as well as on ZK connection established,
     * since this refresher simply fails on ZK connection loss exceptions,
     * rather than retrying.
     */
    private class ModelCacheRefresher implements Runnable {

        private volatile Set<UUID> tasksToRefresh = new HashSet<UUID>();
        private volatile boolean refreshAll;
        private final Object refreshLock = new Object();
        private Thread thread;
        private final Object startedLock = new Object();
        private volatile boolean started = false;

        public synchronized void shutdown() throws InterruptedException {
            if (thread == null || !thread.isAlive())
                return;
            thread.interrupt();
            Logs.logThreadJoin(thread);
            thread.join();
            thread = null;
        }

        public synchronized void start() {
            // Upon startup, be sure to run a refresh of all tasks
            this.refreshAll = true;
            thread = new Thread(this, "ExecutorModelImpl.ModelCacheRefresher");
            // Set as daemon thread: ExecutorModel can be used in tools like the
            // executor admin CLI tools,
            // where we should not require explicit shutdown.
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * Waits until the initial cache fill up happened.
         */
        public void waitUntilStarted() throws InterruptedException {
            synchronized (startedLock) {
                while (!started) {
                    startedLock.wait();
                }
            }
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    List<ExecutorModelEvent> events = new ArrayList<ExecutorModelEvent>();
                    try {
                        Set<UUID> tasksToRefresh = null;
                        boolean refreshAll = false;
                        synchronized (refreshLock) {
                            if (this.refreshAll
                                    || this.tasksToRefresh.isEmpty()) {
                                refreshAll = true;
                            } else {
                                tasksToRefresh = new HashSet<UUID>(
                                        this.tasksToRefresh);
                            }
                            this.refreshAll = false;
                            this.tasksToRefresh.clear();
                        }
                        if (refreshAll) {
                            synchronized (model_lock) {
                                refreshTasks(events);
                            }
                        } else {
                            synchronized (model_lock) {
                                for (UUID uuid : tasksToRefresh) {
                                    refreshTask(uuid, events);
                                }
                            }
                        }
                        if (!started) {
                            started = true;
                            synchronized (startedLock) {
                                startedLock.notifyAll();
                            }
                        }
                    } catch (KeeperException e) {
                        LOG.error("", e);
                    } finally {
                        // We notify the listeners here because we want to be
                        // sure events
                        // for every
                        // change are delivered, even if halfway through the
                        // refreshing we
                        // would have
                        // failed due to some error like a ZooKeeper connection
                        // loss
                        if (!events.isEmpty() && !stopped
                                && !Thread.currentThread().isInterrupted()) {
                            notifyListeners(events);
                        }
                    }
                    synchronized (refreshLock) {
                        if (!this.refreshAll && this.tasksToRefresh.isEmpty()) {
                            refreshLock.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void triggerTaskToRefresh(final UUID uuid) {
            synchronized (refreshLock) {
                tasksToRefresh.add(uuid);
                refreshLock.notifyAll();
            }
        }

        public void triggerRefreshAll() {
            synchronized (refreshLock) {
                refreshAll = true;
                refreshLock.notifyAll();
            }
        }

        private void refreshTasks(final List<ExecutorModelEvent> events)
                throws InterruptedException, KeeperException {
            // this will be set because of UUIDs:
            List<String> uuidNamesList = zk.getChildren(
                    EXECUTOR_COLLECTION_PATH, watcher);
            // Remove tasks which no longer exist in ZK
            Iterator<UUID> currentTasksIterator = tasksMap.keySet().iterator();
            while (currentTasksIterator.hasNext()) {
                UUID uuid = currentTasksIterator.next();
                if (!uuidNamesList.contains(uuid.toString())) {
                    currentTasksIterator.remove();
                    events.add(new ExecutorModelEvent(
                            ExecutorModelEventType.TASK_REMOVED, uuid));
                }
            }
            // Add/update the other Task
            for (String uuidName : uuidNamesList) {
                refreshTask(UUID.fromString(uuidName), events);
            }
        }

        /**
         * Adds or updates the given Task to the internal cache.
         */
        private void refreshTask(final UUID uuid,
                final List<ExecutorModelEvent> events)
                throws InterruptedException, KeeperException {
            try {
                TaskInfoBean task = loadTask(uuid, true);
                task.makeImmutable();
                TaskInfoBean oldTask = tasksMap.get(uuid);
                if (oldTask != null
                        && oldTask.getZkDataVersion() == task
                                .getZkDataVersion()) {
                    // nothing changed
                } else {
                    final boolean isNew = oldTask == null;
                    tasksMap.put(uuid, task);
                    events.add(new ExecutorModelEvent(
                            isNew ? ExecutorModelEventType.TASK_ADDED
                                    : ExecutorModelEventType.TASK_UPDATED, uuid));
                }
            } catch (TaskNotFoundException e) {
                Object oldTask = tasksMap.remove(uuid);
                if (oldTask != null) {
                    events.add(new ExecutorModelEvent(
                            ExecutorModelEventType.TASK_REMOVED, uuid));
                }
            }
        }
    }
}
