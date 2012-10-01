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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlScanner;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import crawlercommons.fetcher.BaseFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class ClassicRobotTask extends AbstractTask {
  
  private static final Logger LOG = LoggerFactory
      .getLogger(ClassicRobotTask.class);
  
  private boolean stop;
  private Thread thread;
  private LeaderElection leaderElection;
  
  /** Default for Solr queries */
  private int rows = 1000;
  
  private final SimpleHttpClient httpClient;
  
  // special instance for robots.txt only:
  private final BaseFetcher robotFetcher;
  BaseRobotRules robotRules = null;
  
  public ClassicRobotTask(String fetchName, ZooKeeperItf zk,
      TaskConfiguration fetcherConfiguration,
      CrawlerHBaseRepository crawlerRepository, WritableExecutorModel model,
      HostLocker hostLocker) {
    super(fetchName, zk, fetcherConfiguration, crawlerRepository, model,
        hostLocker);
    this.httpClient = new SimpleHttpClient(FetcherUtils.USER_AGENT);
    this.robotFetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT, 1);
    this.robotFetcher.setDefaultMaxContentSize(4 * 1024 * 1024);
  }
  
  @Override
  public void start() throws InterruptedException,
      LeaderElectionSetupException, KeeperException {
    leaderElection = new LeaderElection(zk, "Master "
        + this.getClass().getCanonicalName(), "/org/tokenizer/executor/engine/"
        + this.getClass().getCanonicalName() + "/" + this.taskName,
        new MyLeaderElectionCallback());
  }
  
  @Override
  public void stop() {
    LOG.warn("stop...");
    try {
      leaderElection.stop();
      LOG.warn("Stopped.");
    } catch (InterruptedException e) {
      LOG.error("Interrupted while trying to stop Leader Election...", e);
    }
  }
  
  @Override
  public Thread getThread() {
    return this.thread;
  }
  
  @Override
  public boolean isStop() {
    return this.stop;
  }
  
  @Override
  public void run() {
    while (!stop && !Thread.interrupted()) {
      try {
        refreshRobotRules();
        String url = "http://" + taskConfiguration.getTld();
        UrlRecord urlRecord = new UrlRecord();
        urlRecord.setUrl(url);
        // create if it doesn't exist
        crawlerRepository.create(urlRecord);
        process();
      } catch (Throwable t) {
        LOG.error("Error processing", t);
      }
    }
  }
  
  private class MyLeaderElectionCallback implements LeaderElectionCallback {
    
    public void activateAsLeader() throws Exception {
      LOG.warn("activateAsLeader...");
      if (thread != null) {
        LOG.warn("Start was requested, but old thread was still there. Stopping it now.");
        stop = true;
        thread.interrupt();
        Logs.logThreadJoin(thread);
        thread.join();
      }
      stop = false;
      
      // if (thread != null) shutdown();
      // stop = false;
      
      thread = new Thread(ClassicRobotTask.this, ClassicRobotTask.this.taskName);
      thread.start();
      LOG.warn("Activated as Leader.");
    }
    
    public void deactivateAsLeader() throws Exception {
      LOG.warn("deactivateAsLeader...");
      shutdown();
      LOG.warn("Deactivated as Leader.");
    }
    
  }
  
  private synchronized void shutdown() throws InterruptedException {
    LOG.warn("shutdown...");
    stop = true;
    if (!thread.isAlive()) {
      return;
    }
    thread.interrupt();
    Logs.logThreadJoin(thread);
    thread.join();
    thread = null;
    LOG.warn("Shutted down.");
  }
  
  /**
   * Real job is done here
   * 
   * @throws InterruptedException
   */
  private void process() throws InterruptedException {
    
    UrlScanner urlScanner = new UrlScanner(taskConfiguration.getTld(),
        crawlerRepository);
    
    for (UrlRecord urlRecord : urlScanner) {
      
      if (urlRecord.getTimestamp() > 0) continue;
      
      FetchedResult fetchedResult = PersistenceUtils.fetch(urlRecord,
          crawlerRepository, robotRules, metricsCache, httpClient,
          taskConfiguration.getTld());
      
    }
    
  }
  
  public boolean init() {
    
    return true;
    
  }
  
  /**
   * Should be called few times a day
   * 
   * @return
   */
  private synchronized boolean refreshRobotRules() {
    BaseRobotsParser parser = new SimpleRobotRulesParser();
    try {
      robotRules = RobotUtils.getRobotRules(robotFetcher, parser, new URL(
          "http://" + taskConfiguration.getTld() + "/robots.txt"));
    } catch (MalformedURLException e) {
      LOG.error("", e);
      return false;
    }
    return true;
  }
  
}
