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
package org.tokenizer.executor.worker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.engine.AbstractTask;
import org.tokenizer.executor.engine.ClassicRobotTask;
import org.tokenizer.executor.engine.HostLocker;
import org.tokenizer.executor.engine.RssFetcherTask;
import org.tokenizer.executor.engine.SimpleMultithreadedFetcher;
import org.tokenizer.executor.engine.SitemapsTask;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskDefinition;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfigurationBuilder;
import org.tokenizer.executor.model.configuration.TaskConfigurationException;

/**
 * Worker is responsible for starting/stopping tasks.
 * 
 * <p>
 * Worker does not shut down the tasks when the ZooKeeper connection is lost.
 */
public class ExecutorWorker {
  
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
      .getLogger(ExecutorWorker.class);
  
  private WritableExecutorModel executorModel;
  
  private CrawlerHBaseRepository repository;
  
  private ZooKeeperItf zk;
  
  private ExecutorModelListener listener = new MyListener();
  
  private BlockingQueue<ExecutorModelEvent> eventQueue = new LinkedBlockingQueue<ExecutorModelEvent>();
  
  private Thread eventWorkerThread;
  
  private Map<String,AbstractTask> tasks = new HashMap<String,AbstractTask>();
  
  /** will be shared between tasks; multithreaded access */
  private HostLocker hostLocker;
  
  public ExecutorWorker(WritableExecutorModel executorModel, ZooKeeperItf zk,
      CrawlerHBaseRepository repository) throws IOException,
      TaskNotFoundException {
    this.executorModel = executorModel;
    this.zk = zk;
    try {
      hostLocker = new HostLocker(zk);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } catch (KeeperException e) {
      throw new RuntimeException(e);
    }
  }
  
  @PostConstruct
  public void init() {
    
    eventWorkerThread = new Thread(new EventWorker(),
        "Tokenizer Executor - EventWorkerThread");
    eventWorkerThread.start();
    
    synchronized (tasks) {
      if (executorModel == null) {
        LOG.warn("Model is null");
        return;
      }
      
      Collection<TaskDefinition> taskDefinitions = executorModel
          .getTaskDefinitions(listener);
      
      LOG.info("Executor model contains " + taskDefinitions.size()
          + " definitions");
      
      // TODO: we call task.start() few times which creates few
      // LeaderElections?..
      
      for (TaskDefinition taskDefinition : taskDefinitions) {
        AbstractTask task = createTask(taskDefinition);
        tasks.put(taskDefinition.getName(), task);
        if (task.init() && !taskDefinition.getGeneralState().isStopState()
            && !taskDefinition.getGeneralState().isDeleteState()) {
          try {
            task.start();
          } catch (InterruptedException e) {
            LOG.error("", e);
          } catch (LeaderElectionSetupException e) {
            LOG.error("", e);
          } catch (KeeperException e) {
            LOG.error("", e);
          }
        }
      }
      
    }
    
  }
  
  @PreDestroy
  public void stop() {
    
    if (eventWorkerThread == null) {
      LOG.warn("event worker thrad is null");
    } else {
      eventWorkerThread.interrupt();
      try {
        Logs.logThreadJoin(eventWorkerThread);
        eventWorkerThread.join();
      } catch (InterruptedException e) {
        LOG.info("Interrupted while joining eventWorkerThread.");
      }
    }
    
    for (AbstractTask task : tasks.values()) {
      task.stop();
    }
    
  }
  
  private class MyListener implements ExecutorModelListener {
    public void process(ExecutorModelEvent event) {
      try {
        eventQueue.put(event);
      } catch (InterruptedException e) {
        LOG.info("listener thread interrupted...");
      }
    }
  }
  
  private class EventWorker implements Runnable {
    public void run() {
      
      LOG.info("Starting EventWorker thread...");
      
      // TODO: this is temporary solution...
      LOG.info("Waiting 30 seconds until ZooKeeper stabilizes Tasks (see init() method)...");
      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        LOG.error("", e);
      }
      
      while (true) {
        
        synchronized (tasks) {
          try {
            
            int queueSize = eventQueue.size();
            if (queueSize >= 10) {
              LOG.warn("EventWorker queue getting large: {}" + queueSize);
            }
            
            ExecutorModelEvent event = eventQueue.take();
            LOG.debug("Event removed from queue: {}", event);
            
            if (event.getType() == ExecutorModelEventType.TASK_DEFINITION_ADDED
                || event.getType() == ExecutorModelEventType.TASK_DEFINITION_UPDATED) {
              
              TaskDefinition taskDefinition = executorModel
                  .getTaskDefinition(event.getTaskDefinitionName());
              TaskGeneralState taskGeneralState = taskDefinition
                  .getGeneralState();
              
              AbstractTask task = tasks.get(event.getTaskDefinitionName());
              
              if (task == null) {
                
                if (taskGeneralState.isDeleteState()) continue;
                
                task = createTask(taskDefinition);
                // this can happen if XML config was wrong - createTask returned
                // null
                if (task == null) {
                  LOG.error("can't create task: {}",
                      event.getTaskDefinitionName());
                  continue;
                }
                // if we don't have it in Map it means it is new; start it
                // automatically
                if (task.init()
                    && taskGeneralState.equals(TaskGeneralState.ACTIVE)
                    || taskGeneralState
                        .equals(TaskGeneralState.START_REQUESTED)) task.start();
                tasks.put(event.getTaskDefinitionName(), task);
                continue;
              }
              
              // might happen that we have newer data already ;) ? - why not...
              if (task.getTaskDefinition().getZkDataVersion() >= taskDefinition
                  .getZkDataVersion()) {
                continue;
              }
              
              // task definition contains extra attributes such as stats. We do
              // not need to restart task if Metrics has been changed:
              boolean relevantChanges = !Arrays.equals(task.getTaskDefinition()
                  .getConfiguration(), taskDefinition.getConfiguration());
              
              if (!relevantChanges) {
                LOG.info("Not relevant changes...");
                if (task.init()
                    && taskGeneralState
                        .equals(TaskGeneralState.START_REQUESTED)) task.start();
                if (taskGeneralState.equals(TaskGeneralState.STOP_REQUESTED)) task
                    .stop();
                continue;
              }
              
              // remaining is when smth changed... stop, remove, create, start
              // if necessary:
              LOG.info("Relevant changes... stop/recreate/start");
              task.stop();
              task = createTask(taskDefinition);
              
              if (task.init()
                  && taskGeneralState.equals(TaskGeneralState.ACTIVE)
                  || taskGeneralState.equals(TaskGeneralState.START_REQUESTED)) task
                  .start();
              
              tasks.put(event.getTaskDefinitionName(), task);
              
            } else if (event.getType() == ExecutorModelEventType.TASK_DEFINITION_REMOVED) {
              LOG.debug("Task definition removed...");
              tasks.get(event.getTaskDefinitionName()).stop();
              tasks.remove(event.getTaskDefinitionName());
            }
            
          } catch (InterruptedException e) {
            LOG.warn("event listener interrupted... committing Metrics...");
            for (Map.Entry<String,AbstractTask> task : tasks.entrySet()) {
              LOG.warn("committing metrics for {}...", task.getKey());
              task.getValue().getMetricsCache().commit();
            }
            // TODO: is it best practice?
            LOG.warn("interrupted... exiting");
            return;
          } catch (LeaderElectionSetupException e) {
            LOG.info("LeaderElectionSetupException... continue");
            continue;
          } catch (KeeperException e) {
            LOG.info("KeeperException... continue");
            continue;
          } catch (Throwable t) {
            LOG.error("unknown error... exiting", t);
            return;
          }
        }
        
      }
    }
  }
  
  private AbstractTask createTask(TaskDefinition taskDefinition) {
    
    AbstractTask task = null;
    
    TaskConfiguration taskConfiguration;
    try {
      taskConfiguration = TaskConfigurationBuilder
          .build(new ByteArrayInputStream(taskDefinition.getConfiguration()));
    } catch (TaskConfigurationException e) {
      LOG.error("", e);
      return null;
    }
    
    if (taskConfiguration.getType().equals("SitemapsTask")) {
      task = new SitemapsTask(taskConfiguration.getName(), zk,
          taskConfiguration, repository, executorModel, hostLocker);
    } else if (taskConfiguration.getType().equals("ClassicRobotTask")) {
      task = new ClassicRobotTask(taskConfiguration.getName(), zk,
          taskConfiguration, repository, executorModel, hostLocker);
    } else if (taskConfiguration.getType().equals("RssFetcherTask")) {
      task = new RssFetcherTask(taskConfiguration.getName(), zk,
          taskConfiguration, repository, executorModel, hostLocker);
    } else if (taskConfiguration.getType().equals("SimpleMultithreadedFetcher")) {
      task = new SimpleMultithreadedFetcher(taskConfiguration.getName(), zk,
          taskConfiguration, repository, executorModel, hostLocker);
    }
    
    return task;
    
  }
  
}
