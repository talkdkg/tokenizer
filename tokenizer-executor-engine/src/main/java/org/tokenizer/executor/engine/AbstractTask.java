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
package org.tokenizer.executor.engine;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.model.api.TaskDefinition;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

public abstract class AbstractTask implements Runnable {
  
  private static final Logger LOG = LoggerFactory.getLogger(AbstractTask.class);
  
  protected final String taskName;
  protected final ZooKeeperItf zk;
  protected final TaskConfiguration taskConfiguration;
  protected final CrawlerHBaseRepository crawlerRepository;
  protected final WritableExecutorModel model;
  protected final HostLocker hostLocker;
  protected final MetricsCache metricsCache;
  
  public AbstractTask(String taskName, ZooKeeperItf zk,
      TaskConfiguration taskConfiguration,
      CrawlerHBaseRepository crawlerRepository, WritableExecutorModel model,
      HostLocker hostLocker) {
    this.taskName = taskName;
    this.zk = zk;
    this.taskConfiguration = taskConfiguration;
    this.crawlerRepository = crawlerRepository;
    this.model = model;
    this.hostLocker = hostLocker;
    this.metricsCache = new MetricsCache(taskName, model);
    LOG.debug("Resetting; it will produce TASK_DEFINITION_UPDATED event");
    //this.metricsCache.reset();
  }
  
  public MetricsCache getMetricsCache() {
    return metricsCache;
  }
  
  public TaskDefinition getTaskDefinition() {
    TaskDefinition taskDefinition = null;
    try {
      taskDefinition = model.getTaskDefinition(taskName);
    } catch (TaskNotFoundException e) {
      LOG.error(taskName);
    }
    return taskDefinition;
  }
  
  public abstract void start() throws InterruptedException,
      LeaderElectionSetupException, KeeperException;
  
  public abstract void stop() throws InterruptedException;
  
  public abstract Thread getThread();
  
  public abstract boolean isStop();
  
  /**
   * Must be called explicitly before class can be used Reason: I want to catch
   * all "recoverable" errors such as Solr temporary unavailability and to
   * return true/false (instead of throwing RuntimeException)
   * 
   * @return
   */
  public abstract boolean init();
  
}
