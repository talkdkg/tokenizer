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
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.ZkLock;
import org.lilyproject.util.zookeeper.ZkLockException;
import org.lilyproject.util.zookeeper.ZkUtil;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.lilyproject.util.zookeeper.ZooKeeperOperation;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskBatchBuildState;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.api.WritableExecutorModel;

public class ExecutorModelImpl implements WritableExecutorModel {

    private final static Log LOG = LogFactory.getLog(ExecutorModelImpl.class);

    private ZooKeeperItf zk;

    /**
     * Cache of the tasks as they are stored in ZK. Updated based on ZK watcher
     * events. Updates to this cache should synchronize on {@link #model_lock}.
     */
    private Map<String, TaskInfoBean> tasksMap = new ConcurrentHashMap<String, TaskInfoBean>(
            16, 0.75f, 1);

    private final Object model_lock = new Object();

    private Set<ExecutorModelListener> listeners = Collections
            .newSetFromMap(new IdentityHashMap<ExecutorModelListener, Boolean>());

    private Watcher watcher = new ModelChangeWatcher();

    private Watcher connectStateWatcher = new ConnectStateWatcher();

    private ModelCacheRefresher modelCacheRefresher = new ModelCacheRefresher();

    private boolean stopped = false;

    private static final String EXECUTOR_COLLECTION_PATH = "/org/tokenizer/executor/tasks";

    private static final String EXECUTOR_TRASH_PATH = "/org/tokenizer/executor/tasks-trash";

    public ExecutorModelImpl(ZooKeeperItf zk) throws InterruptedException,
            KeeperException {
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
    public void addTask(TaskInfoBean task) throws TaskExistsException,
            TaskModelException, TaskValidityException {
        assertValid(task);

        final String taskPath = EXECUTOR_COLLECTION_PATH + "/" + task.getName();

        final byte[] data = TaskInfoBeanConverter.toJsonBytes(task);

        try {
            zk.retryOperation(new ZooKeeperOperation<String>() {
                public String execute() throws KeeperException,
                        InterruptedException {
                    return zk.create(taskPath, data,
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            });
        } catch (KeeperException.NodeExistsException e) {
            throw new TaskExistsException(task.getName());
        } catch (Exception e) {
            throw new TaskModelException("Error creating Task.", e);
        }
    }

    private void assertValid(TaskInfoBean task) throws TaskValidityException {
        if (task.getName() == null || task.getName().length() == 0)
            throw new TaskValidityException(
                    "Name should not be null or zero-length");

        if (task.getTaskConfiguration() == null)
            throw new TaskValidityException("Configuration should not be null.");

        if (task.getGeneralState() == null)
            throw new TaskValidityException("General state should not be null.");

        if (task.getBatchBuildState() == null)
            throw new TaskValidityException("Build state should not be null.");
    }

    public void updateTaskInternal(final TaskInfoBean task)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            TaskValidityException {

        assertValid(task);

        final byte[] newData = TaskInfoBeanConverter.toJsonBytes(task);

        try {
            zk.retryOperation(new ZooKeeperOperation<Stat>() {
                public Stat execute() throws KeeperException,
                        InterruptedException {
                    return zk.setData(
                            EXECUTOR_COLLECTION_PATH + "/" + task.getName(),
                            newData, task.getZkDataVersion());
                }
            });
        } catch (KeeperException.NoNodeException e) {
            throw new TaskNotFoundException(task.getName());
        } catch (KeeperException.BadVersionException e) {
            throw new TaskConcurrentModificationException(task.getName());
        }
    }

    public void updateTask(final TaskInfoBean task, String lock)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            ZkLockException, TaskUpdateException, TaskValidityException {

        if (!ZkLock.ownsLock(zk, lock)) {
            throw new TaskUpdateException(
                    "You are not owner of the Task lock, your lock path is: "
                            + lock);
        }

        assertValid(task);

        TaskInfoBean currentTask = getMutableTask(task.getName());

        // if (currentTask.getGeneralState() ==
        // TaskGeneralState.DELETE_REQUESTED
        // || currentTask.getGeneralState() == TaskGeneralState.DELETING) {
        // throw new TaskUpdateException("Task in the state "
        // + task.getGeneralState() + " cannot be modified.");
        // }

        if (task.getBatchBuildState() == TaskBatchBuildState.BUILD_REQUESTED
                && currentTask.getBatchBuildState() != TaskBatchBuildState.INACTIVE) {
            throw new TaskUpdateException("Cannot move Task build state from "
                    + currentTask.getBatchBuildState() + " to "
                    + task.getBatchBuildState());
        }

        // if (currentTask.getGeneralState() ==
        // TaskGeneralState.DELETE_REQUESTED) {
        // throw new TaskUpdateException("Task in the state "
        // + TaskGeneralState.DELETE_REQUESTED + " cannot be updated.");
        // }

        updateTaskInternal(task);

    }

    public void deleteTask(final String name) throws TaskModelException {
        final String taskPath = EXECUTOR_COLLECTION_PATH + "/" + name;
        final String taskLockPath = taskPath + "/lock";

        try {
            // Make a copy of the Task data in the executor trash
            zk.retryOperation(new ZooKeeperOperation<Object>() {
                public Object execute() throws KeeperException,
                        InterruptedException {
                    byte[] data = zk.getData(taskPath, false, null);

                    String trashPath = EXECUTOR_TRASH_PATH + "/" + name;

                    // Taskwith the same name might have existed
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

                if (success)
                    break;

                tryCount++;
                if (tryCount > 10) {
                    throw new TaskModelException(
                            "Failed to delete the Task because it still has child data. Task name: "
                                    + name);
                }
            }
        } catch (Throwable t) {
            if (t instanceof InterruptedException)
                Thread.currentThread().interrupt();
            throw new TaskModelException("Failed to delete Task " + name, t);
        }
    }

    @Override
    public String lockTaskInternal(String name, boolean checkDeleted)
            throws ZkLockException, TaskNotFoundException,
            InterruptedException, KeeperException, TaskModelException {

        TaskInfoBean task = getTask(name);

        if (checkDeleted) {
            if (task.getGeneralState() == TaskGeneralState.DELETE_REQUESTED) {
                // TODO: uncomment
                // throw new TaskModelException("A task in state "
                // + task.getGeneralState() + " cannot be locked.");
            }
        }

        final String lockPath = EXECUTOR_COLLECTION_PATH + "/" + name + "/lock";

        //
        // Create the lock path if necessary
        //
        Stat stat = zk.retryOperation(new ZooKeeperOperation<Stat>() {
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
                throw new TaskNotFoundException(name);
            }
        }

        //
        // Take the actual lock
        //
        return ZkLock.lock(zk, lockPath);

    }

    @Override
    public String lockTask(String name) throws ZkLockException,
            TaskNotFoundException, InterruptedException, KeeperException,
            TaskModelException {
        return lockTaskInternal(name, true);
    }

    public void unlockTask(String lock) throws ZkLockException {
        ZkLock.unlock(zk, lock);
    }

    public void unlockTask(String lock, boolean ignoreMissing)
            throws ZkLockException {
        ZkLock.unlock(zk, lock, ignoreMissing);
    }

    public TaskInfoBean getTask(String name) throws TaskNotFoundException {
        TaskInfoBean task = tasksMap.get(name);
        if (task == null) {
            throw new TaskNotFoundException(name);
        }
        return task;
    }

    public boolean hasTask(String name) {
        return tasksMap.containsKey(name);
    }

    public TaskInfoBean getMutableTask(String name)
            throws InterruptedException, KeeperException, TaskNotFoundException {
        return loadTask(name, false);
    }

    public Collection<TaskInfoBean> getTasks() {
        return new ArrayList<TaskInfoBean>(tasksMap.values());
    }

    public Collection<TaskInfoBean> getTasks(ExecutorModelListener listener) {
        synchronized (model_lock) {
            registerListener(listener);
            return new ArrayList<TaskInfoBean>(tasksMap.values());
        }
    }

    private TaskInfoBean loadTask(String taskName, boolean forCache)
            throws InterruptedException, KeeperException, TaskNotFoundException {
        final String childPath = EXECUTOR_COLLECTION_PATH + "/" + taskName;
        final Stat stat = new Stat();

        byte[] data;
        try {
            if (forCache) {
                // do not retry, install watcher
                data = zk.getData(childPath, watcher, stat);
            } else {
                // do retry, do not install watcher
                data = zk.retryOperation(new ZooKeeperOperation<byte[]>() {
                    public byte[] execute() throws KeeperException,
                            InterruptedException {
                        return zk.getData(childPath, false, stat);
                    }
                });
            }
        } catch (KeeperException.NoNodeException e) {
            throw new TaskNotFoundException(taskName);
        }

        TaskInfoBean task = new TaskInfoBean();
        task.setZkDataVersion(stat.getVersion());
        TaskInfoBeanConverter.fromJsonBytes(data, task);

        return task;
    }

    private void notifyListeners(List<ExecutorModelEvent> events) {
        for (ExecutorModelEvent event : events) {
            for (ExecutorModelListener listener : listeners) {
                listener.process(event);
            }
        }
    }

    public void registerListener(ExecutorModelListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(ExecutorModelListener listener) {
        this.listeners.remove(listener);
    }

    private class ModelChangeWatcher implements Watcher {
        public void process(WatchedEvent event) {
            if (stopped) {
                return;
            }

            try {
                if (NodeChildrenChanged.equals(event.getType())
                        && event.getPath().equals(EXECUTOR_COLLECTION_PATH)) {
                    modelCacheRefresher.triggerRefreshAll();
                } else if (NodeDataChanged.equals(event.getType())
                        && event.getPath().startsWith(
                                EXECUTOR_COLLECTION_PATH + "/")) {
                    String name = event.getPath().substring(
                            EXECUTOR_COLLECTION_PATH.length() + 1);
                    modelCacheRefresher.triggerTaskToRefresh(name);
                }
            } catch (Throwable t) {
                LOG.error("Event: " + event, t);
            }
        }
    }

    public class ConnectStateWatcher implements Watcher {
        public void process(WatchedEvent event) {
            if (stopped) {
                return;
            }

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
        private volatile Set<String> tasksToRefresh = new HashSet<String>();
        private volatile boolean refreshAll;
        private final Object refreshLock = new Object();
        private Thread thread;
        private final Object startedLock = new Object();
        private volatile boolean started = false;

        public synchronized void shutdown() throws InterruptedException {
            if (thread == null || !thread.isAlive()) {
                return;
            }

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

        public void run() {
            while (!Thread.interrupted()) {
                try {
                    List<ExecutorModelEvent> events = new ArrayList<ExecutorModelEvent>();
                    try {
                        Set<String> tasksToRefresh = null;
                        boolean refreshAll = false;

                        synchronized (refreshLock) {
                            if (this.refreshAll
                                    || this.tasksToRefresh.isEmpty()) {
                                refreshAll = true;
                            } else {
                                tasksToRefresh = new HashSet<String>(
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
                                for (String name : tasksToRefresh) {
                                    refreshTask(name, events);
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

        public void triggerTaskToRefresh(String name) {
            synchronized (refreshLock) {
                tasksToRefresh.add(name);
                refreshLock.notifyAll();
            }
        }

        public void triggerRefreshAll() {
            synchronized (refreshLock) {
                refreshAll = true;
                refreshLock.notifyAll();
            }
        }

        private void refreshTasks(List<ExecutorModelEvent> events)
                throws InterruptedException, KeeperException {
            List<String> taskNamesList = zk.getChildren(
                    EXECUTOR_COLLECTION_PATH, watcher);

            Set<String> taskNamesSet = new HashSet<String>();
            taskNamesSet.addAll(taskNamesList);

            // Remove tasks which no longer exist in ZK
            Iterator<String> currentTasksIterator = tasksMap.keySet()
                    .iterator();
            while (currentTasksIterator.hasNext()) {
                String name = currentTasksIterator.next();
                if (!taskNamesSet.contains(name)) {
                    currentTasksIterator.remove();
                    events.add(new ExecutorModelEvent(
                            ExecutorModelEventType.TASK_REMOVED, name));
                }
            }

            // Add/update the other Task
            for (String name : taskNamesList) {
                refreshTask(name, events);
            }
        }

        /**
         * Adds or updates the given Task to the internal cache.
         */
        private void refreshTask(final String taskName,
                List<ExecutorModelEvent> events) throws InterruptedException,
                KeeperException {
            try {
                TaskInfoBean task = loadTask(taskName, true);
                task.makeImmutable();

                TaskInfoBean oldTask = tasksMap.get(taskName);

                if (oldTask != null
                        && oldTask.getZkDataVersion() == task
                                .getZkDataVersion()) {
                    // nothing changed
                } else {
                    final boolean isNew = oldTask == null;
                    tasksMap.put(taskName, task);
                    events.add(new ExecutorModelEvent(
                            isNew ? ExecutorModelEventType.TASK_ADDED
                                    : ExecutorModelEventType.TASK_UPDATED,
                            taskName));
                }
            } catch (TaskNotFoundException e) {
                Object oldTask = tasksMap.remove(taskName);

                if (oldTask != null) {
                    events.add(new ExecutorModelEvent(
                            ExecutorModelEventType.TASK_REMOVED, taskName));
                }
            }
        }
    }

}
