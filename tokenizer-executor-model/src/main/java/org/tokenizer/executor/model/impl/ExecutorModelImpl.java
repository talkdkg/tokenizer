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

import java.io.ByteArrayInputStream;
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
import org.tokenizer.executor.model.api.TaskDefinition;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfigurationBuilder;
import org.tokenizer.executor.model.configuration.TaskConfigurationException;

public class ExecutorModelImpl implements WritableExecutorModel {
  
  private final static Log LOG = LogFactory.getLog(ExecutorModelImpl.class);
  
  private ZooKeeperItf zk;
  
  /**
   * Cache of the definitions as they are stored in ZK. Updated based on ZK
   * watcher events. Updates to this cache should synchronize on
   * {@link #model_lock}.
   */
  private Map<String,TaskDefinition> taskDefinitionsMap = new ConcurrentHashMap<String,TaskDefinition>(
      16, 0.75f, 1);
  
  private final Object model_lock = new Object();
  
  private Set<ExecutorModelListener> listeners = Collections
      .newSetFromMap(new IdentityHashMap<ExecutorModelListener,Boolean>());
  
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
  
  public TaskDefinition newTaskDefinition(String name) {
    return new TaskDefinitionImpl(name);
  }
  
  public void addTaskDefinition(TaskDefinition taskDefinition)
      throws TaskExistsException, TaskModelException, TaskValidityException {
    assertValid(taskDefinition);
    
    final String taskDefinitionPath = EXECUTOR_COLLECTION_PATH + "/"
        + taskDefinition.getName();
    final byte[] data = TaskDefinitionConverter.INSTANCE
        .toJsonBytes(taskDefinition);
    
    try {
      zk.retryOperation(new ZooKeeperOperation<String>() {
        public String execute() throws KeeperException, InterruptedException {
          return zk.create(taskDefinitionPath, data,
              ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
      });
    } catch (KeeperException.NodeExistsException e) {
      throw new TaskExistsException(taskDefinition.getName());
    } catch (Exception e) {
      throw new TaskModelException("Error creating TaskDefinition.", e);
    }
  }
  
  private void assertValid(TaskDefinition taskDefinition)
      throws TaskValidityException {
    if (taskDefinition.getName() == null
        || taskDefinition.getName().length() == 0) throw new TaskValidityException(
        "Name should not be null or zero-length");
    
    if (taskDefinition.getConfiguration() == null) throw new TaskValidityException(
        "Configuration should not be null.");
    
    if (taskDefinition.getGeneralState() == null) throw new TaskValidityException(
        "General state should not be null.");
    
    if (taskDefinition.getBatchBuildState() == null) throw new TaskValidityException(
        "Build state should not be null.");
    
    try {
      TaskConfigurationBuilder.validate(new ByteArrayInputStream(taskDefinition
          .getConfiguration()));
    } catch (TaskConfigurationException e) {
      throw new TaskValidityException(
          "The task configuration is not XML well-formed or valid.", e);
    }
  }
  
  public void updateTaskDefinitionInternal(final TaskDefinition taskDefinition)
      throws InterruptedException, KeeperException, TaskNotFoundException,
      TaskConcurrentModificationException, TaskValidityException {
    
    assertValid(taskDefinition);
    
    final byte[] newData = TaskDefinitionConverter.INSTANCE
        .toJsonBytes(taskDefinition);
    
    try {
      zk.retryOperation(new ZooKeeperOperation<Stat>() {
        public Stat execute() throws KeeperException, InterruptedException {
          return zk.setData(
              EXECUTOR_COLLECTION_PATH + "/" + taskDefinition.getName(),
              newData, taskDefinition.getZkDataVersion());
        }
      });
    } catch (KeeperException.NoNodeException e) {
      throw new TaskNotFoundException(taskDefinition.getName());
    } catch (KeeperException.BadVersionException e) {
      throw new TaskConcurrentModificationException(taskDefinition.getName());
    }
  }
  
  public void updateTaskDefinition(final TaskDefinition taskDefinition,
      String lock) throws InterruptedException, KeeperException,
      TaskNotFoundException, TaskConcurrentModificationException,
      ZkLockException, TaskUpdateException, TaskValidityException {
    
    if (!ZkLock.ownsLock(zk, lock)) {
      throw new TaskUpdateException(
          "You are not owner of the TaskDefinition lock, your lock path is: "
              + lock);
    }
    
    assertValid(taskDefinition);
    
    TaskDefinition currentTaskDefinition = getMutableTaskDefinition(taskDefinition
        .getName());
    
    if (currentTaskDefinition.getGeneralState() == TaskGeneralState.DELETE_REQUESTED
        || currentTaskDefinition.getGeneralState() == TaskGeneralState.DELETING) {
      throw new TaskUpdateException("TaskDefinition in state "
          + taskDefinition.getGeneralState() + " cannot be modified.");
    }
    
    if (taskDefinition.getBatchBuildState() == TaskBatchBuildState.BUILD_REQUESTED
        && currentTaskDefinition.getBatchBuildState() != TaskBatchBuildState.INACTIVE) {
      throw new TaskUpdateException(
          "Cannot move TaskDefinition build state from "
              + currentTaskDefinition.getBatchBuildState() + " to "
              + taskDefinition.getBatchBuildState());
    }
    
    if (currentTaskDefinition.getGeneralState() == TaskGeneralState.DELETE_REQUESTED) {
      throw new TaskUpdateException("TaskDefinition in the state "
          + TaskGeneralState.DELETE_REQUESTED + " cannot be updated.");
    }
    
    updateTaskDefinitionInternal(taskDefinition);
    
  }
  
  public void deleteTaskDefinition(final String name) throws TaskModelException {
    final String taskDefinitionPath = EXECUTOR_COLLECTION_PATH + "/" + name;
    final String taskDefinitionLockPath = taskDefinitionPath + "/lock";
    
    try {
      // Make a copy of the TaskDefinition data in the executor trash
      zk.retryOperation(new ZooKeeperOperation<Object>() {
        public Object execute() throws KeeperException, InterruptedException {
          byte[] data = zk.getData(taskDefinitionPath, false, null);
          
          String trashPath = EXECUTOR_TRASH_PATH + "/" + name;
          
          // TaskDefinition with the same name might have existed before and
          // hence already exist in the executor trash, handle this by appending
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
      
      // The loop below is normally not necessary, since we disallow taking new
      // locks on task definitions which are being deleted.
      int tryCount = 0;
      while (true) {
        boolean success = zk.retryOperation(new ZooKeeperOperation<Boolean>() {
          public Boolean execute() throws KeeperException, InterruptedException {
            try {
              // Delete the TaskDefinition lock if it exists
              if (zk.exists(taskDefinitionLockPath, false) != null) {
                List<String> children = Collections.emptyList();
                try {
                  children = zk.getChildren(taskDefinitionLockPath, false);
                } catch (KeeperException.NoNodeException e) {
                  // ok
                }
                
                for (String child : children) {
                  try {
                    zk.delete(taskDefinitionLockPath + "/" + child, -1);
                  } catch (KeeperException.NoNodeException e) {
                    // ignore, node was already removed
                  }
                }
                
                try {
                  zk.delete(taskDefinitionLockPath, -1);
                } catch (KeeperException.NoNodeException e) {
                  // ignore
                }
              }
              
              zk.delete(taskDefinitionPath, -1);
              
              return true;
            } catch (KeeperException.NotEmptyException e) {
              // Someone again took a lock on the TaskDefinition, retry
            }
            return false;
          }
        });
        
        if (success) break;
        
        tryCount++;
        if (tryCount > 10) {
          throw new TaskModelException(
              "Failed to delete TaskDefinition because it still has child data. TaskDefinition: "
                  + name);
        }
      }
    } catch (Throwable t) {
      if (t instanceof InterruptedException) Thread.currentThread().interrupt();
      throw new TaskModelException("Failed to delete TaskDefinition " + name, t);
    }
  }
  
  public String lockTaskDefinitionInternal(String name, boolean checkDeleted)
      throws ZkLockException, TaskNotFoundException, InterruptedException,
      KeeperException, TaskModelException {
    
    TaskDefinition taskDefinition = getTaskDefinition(name);
    
    if (checkDeleted) {
      if (taskDefinition.getGeneralState() == TaskGeneralState.DELETE_REQUESTED
          || taskDefinition.getGeneralState() == TaskGeneralState.DELETING) {
        throw new TaskModelException("A task definition in state "
            + taskDefinition.getGeneralState() + " cannot be locked.");
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
      // We do not make use of ZkUtil.createPath (= recursive path creation) on
      // purpose, because if the parent path does not exist, this means the
      // TaskDefinition does not exist, and we do not want to create a task
      // definition path (with null data) like that.
      try {
        zk.retryOperation(new ZooKeeperOperation<String>() {
          public String execute() throws KeeperException, InterruptedException {
            return zk.create(lockPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
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
  
  public String lockTaskDefinition(String name) throws ZkLockException,
      TaskNotFoundException, InterruptedException, KeeperException,
      TaskModelException {
    return lockTaskDefinitionInternal(name, true);
  }
  
  public void unlockTaskDefinition(String lock) throws ZkLockException {
    ZkLock.unlock(zk, lock);
  }
  
  public void unlockTaskDefinition(String lock, boolean ignoreMissing)
      throws ZkLockException {
    ZkLock.unlock(zk, lock, ignoreMissing);
  }
  
  public TaskDefinition getTaskDefinition(String name)
      throws TaskNotFoundException {
    TaskDefinition taskDefinition = taskDefinitionsMap.get(name);
    if (taskDefinition == null) {
      throw new TaskNotFoundException(name);
    }
    return taskDefinition;
  }
  
  public boolean hasTaskDefinition(String name) {
    return taskDefinitionsMap.containsKey(name);
  }
  
  public TaskDefinition getMutableTaskDefinition(String name)
      throws InterruptedException, KeeperException, TaskNotFoundException {
    return loadTaskDefinition(name, false);
  }
  
  public Collection<TaskDefinition> getTaskDefinitions() {
    return new ArrayList<TaskDefinition>(taskDefinitionsMap.values());
  }
  
  public Collection<TaskDefinition> getTaskDefinitions(
      ExecutorModelListener listener) {
    synchronized (model_lock) {
      registerListener(listener);
      return new ArrayList<TaskDefinition>(taskDefinitionsMap.values());
    }
  }
  
  private TaskDefinitionImpl loadTaskDefinition(String taskDefinitionName,
      boolean forCache) throws InterruptedException, KeeperException,
      TaskNotFoundException {
    final String childPath = EXECUTOR_COLLECTION_PATH + "/"
        + taskDefinitionName;
    final Stat stat = new Stat();
    
    byte[] data;
    try {
      if (forCache) {
        // do not retry, install watcher
        data = zk.getData(childPath, watcher, stat);
      } else {
        // do retry, do not install watcher
        data = zk.retryOperation(new ZooKeeperOperation<byte[]>() {
          public byte[] execute() throws KeeperException, InterruptedException {
            return zk.getData(childPath, false, stat);
          }
        });
      }
    } catch (KeeperException.NoNodeException e) {
      throw new TaskNotFoundException(taskDefinitionName);
    }
    
    TaskDefinitionImpl taskDefinition = new TaskDefinitionImpl(
        taskDefinitionName);
    taskDefinition.setZkDataVersion(stat.getVersion());
    TaskDefinitionConverter.INSTANCE.fromJsonBytes(data, taskDefinition);
    
    return taskDefinition;
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
          modelCacheRefresher.triggerRefreshAllDefinitions();
        } else if (NodeDataChanged.equals(event.getType())
            && event.getPath().startsWith(EXECUTOR_COLLECTION_PATH + "/")) {
          String name = event.getPath().substring(
              EXECUTOR_COLLECTION_PATH.length() + 1);
          modelCacheRefresher.triggerTaskDefinitionToRefresh(name);
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
        // Each time the connection is established, we trigger refreshing, since
        // the previous refresh
        // might have failed with a ConnectionLoss exception
        modelCacheRefresher.triggerRefreshAllDefinitions();
      }
    }
  }
  
  /**
   * Responsible for updating our internal cache of Definition's. Should be
   * triggered upon each related change on ZK, as well as on ZK connection
   * established, since this refresher simply fails on ZK connection loss
   * exceptions, rather than retrying.
   */
  private class ModelCacheRefresher implements Runnable {
    private volatile Set<String> definitionsToRefresh = new HashSet<String>();
    private volatile boolean refreshAllDefinitions;
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
      // Upon startup, be sure to run a refresh of all definitions
      this.refreshAllDefinitions = true;
      
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
            Set<String> definitionsToRefresh = null;
            boolean refreshAllDefinitions = false;
            
            synchronized (refreshLock) {
              if (this.refreshAllDefinitions
                  || this.definitionsToRefresh.isEmpty()) {
                refreshAllDefinitions = true;
              } else {
                definitionsToRefresh = new HashSet<String>(
                    this.definitionsToRefresh);
              }
              this.refreshAllDefinitions = false;
              this.definitionsToRefresh.clear();
            }
            
            if (refreshAllDefinitions) {
              synchronized (model_lock) {
                refreshTaskDefinitions(events);
              }
            } else {
              synchronized (model_lock) {
                for (String name : definitionsToRefresh) {
                  refreshTaskDefinition(name, events);
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
            // We notify the listeners here because we want to be sure events
            // for every
            // change are delivered, even if halfway through the refreshing we
            // would have
            // failed due to some error like a ZooKeeper connection loss
            if (!events.isEmpty() && !stopped
                && !Thread.currentThread().isInterrupted()) {
              notifyListeners(events);
            }
          }
          
          synchronized (refreshLock) {
            if (!this.refreshAllDefinitions
                && this.definitionsToRefresh.isEmpty()) {
              refreshLock.wait();
            }
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
    
    public void triggerTaskDefinitionToRefresh(String name) {
      synchronized (refreshLock) {
        definitionsToRefresh.add(name);
        refreshLock.notifyAll();
      }
    }
    
    public void triggerRefreshAllDefinitions() {
      synchronized (refreshLock) {
        refreshAllDefinitions = true;
        refreshLock.notifyAll();
      }
    }
    
    private void refreshTaskDefinitions(List<ExecutorModelEvent> events)
        throws InterruptedException, KeeperException {
      List<String> taskDefinitionNamesList = zk.getChildren(
          EXECUTOR_COLLECTION_PATH, watcher);
      
      Set<String> taskDefinitionNamesSet = new HashSet<String>();
      taskDefinitionNamesSet.addAll(taskDefinitionNamesList);
      
      // Remove definitions which no longer exist in ZK
      Iterator<String> currentTaskDefinitionNamesIterator = taskDefinitionsMap
          .keySet().iterator();
      while (currentTaskDefinitionNamesIterator.hasNext()) {
        String name = currentTaskDefinitionNamesIterator.next();
        if (!taskDefinitionNamesSet.contains(name)) {
          currentTaskDefinitionNamesIterator.remove();
          events.add(new ExecutorModelEvent(
              ExecutorModelEventType.TASK_DEFINITION_REMOVED, name));
        }
      }
      
      // Add/update the other TaskDefinitions
      for (String name : taskDefinitionNamesList) {
        refreshTaskDefinition(name, events);
      }
    }
    
    /**
     * Adds or updates the given TaskDefinition to the internal cache.
     */
    private void refreshTaskDefinition(final String taskDefinitionName,
        List<ExecutorModelEvent> events) throws InterruptedException,
        KeeperException {
      try {
        TaskDefinitionImpl taskDefinition = loadTaskDefinition(
            taskDefinitionName, true);
        taskDefinition.makeImmutable();
        
        TaskDefinition oldTaskDefinition = taskDefinitionsMap
            .get(taskDefinitionName);
        
        if (oldTaskDefinition != null
            && oldTaskDefinition.getZkDataVersion() == taskDefinition
                .getZkDataVersion()) {
          // nothing changed
        } else {
          final boolean isNew = oldTaskDefinition == null;
          taskDefinitionsMap.put(taskDefinitionName, taskDefinition);
          events.add(new ExecutorModelEvent(
              isNew ? ExecutorModelEventType.TASK_DEFINITION_ADDED
                  : ExecutorModelEventType.TASK_DEFINITION_UPDATED,
              taskDefinitionName));
        }
      } catch (TaskNotFoundException e) {
        Object oldTaskDefinition = taskDefinitionsMap
            .remove(taskDefinitionName);
        
        if (oldTaskDefinition != null) {
          events.add(new ExecutorModelEvent(
              ExecutorModelEventType.TASK_DEFINITION_REMOVED,
              taskDefinitionName));
        }
      }
    }
  }
  
}
