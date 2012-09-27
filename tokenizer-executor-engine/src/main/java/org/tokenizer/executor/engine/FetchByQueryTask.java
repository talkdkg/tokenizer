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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.zookeeper.KeeperException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.CrawlerError;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import crawlercommons.fetcher.BaseFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class FetchByQueryTask extends AbstractTask {
  
  private static final Logger LOG = LoggerFactory
      .getLogger(FetchByQueryTask.class);
  
  private boolean stop;
  private Thread thread;
  private LeaderElection leaderElection;
  
  // Delay 1 minute; give a chanse to Solr to stabilize
  // TODO: it should be config parameter
  private static final long DELAY = 60 * 1000L;
  
  SimpleHttpClient simpleHttpClient = new SimpleHttpClient(
      FetcherUtils.USER_AGENT);
  
  public FetchByQueryTask(String fetchName, ZooKeeperItf zk,
      TaskConfiguration fetcherConfiguration, Repository repository,
      WritableExecutorModel fetcherModel, HostLocker hostLocker) {
    super(fetchName, zk, fetcherConfiguration, repository, fetcherModel,
        hostLocker);
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
      // Stop "watcher" politely... otherwise it will restart automatically ;-)
      leaderElection.stop();
      LOG.warn("Stopped.");
    } catch (InterruptedException e) {
      LOG.error("Interrupted while trying to stop Leader Election...", e);
    }
  }
  
  @Override
  public void run() {
    while (!stop && !Thread.interrupted()) {
      try {
        
        if (stop || Thread.interrupted()) {
          break;
        }
        
        process();
        Thread.sleep(DELAY);
        
      } catch (InterruptedException e) {
        break;
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
      
      thread = new Thread(FetchByQueryTask.this, "FetchByQueryTask");
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
    
    // TODO: FE - I believe we do not call "join" here...
    // thread.join();
    
    thread = null;
    LOG.warn("Shutted down.");
  }
  
  private static final int MAX_CONTENT_SIZE = 1 * 1024 * 1024;
  
  /**
   * Real job is done here
   */
  @SuppressWarnings("static-access")
  private void process() {
    
    String host = taskConfiguration.getTld();
    try {
      if (hostLocker.exists(host)) return;
    } catch (HostLockException e) {
      LOG.error("", e);
      return;
    } catch (InterruptedException e) {
      LOG.error("InterruptedException catched... exiting...");
      stop = true;
      return;
    } catch (KeeperException e) {
      LOG.error("", e);
      return;
    }
    
    try {
      if (!hostLocker.lock(host)) return;
    } catch (HostLockException e) {
      LOG.error("", e);
      return;
    }
    
    BaseFetcher fetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT, 1);
    LOG.warn("User-Agent: {}", fetcher.getUserAgent().getUserAgentString());
    fetcher.setDefaultMaxContentSize(MAX_CONTENT_SIZE);
    
    BaseRobotsParser parser = new SimpleRobotRulesParser();
    
    BaseRobotRules rules = null;
    try {
      rules = RobotUtils.getRobotRules(fetcher, parser, new URL("http://"
          + taskConfiguration.getTld() + "/robots.txt"));
    } catch (MalformedURLException e) {
      throw new CrawlerError(e);
    }
    
    SolrQuery query = new SolrQuery();
    query.setQuery(taskConfiguration.getProperties().get("query"));
    
    int rows = 1000;
    try {
      rows = new Integer(taskConfiguration.getProperties().get("rows"));
    } catch (NumberFormatException e) {
      LOG.error("Check configuration file \"rows\" property", e);
    }
    
    query.setRows(rows);
    
    QueryResponse queryResponse;
    try {
      queryResponse = query(query);
    } catch (SolrServerException e) {
      LOG.error("Solr Exception, can't continue, sleeping 10 seconds...", e);
      try {
        Thread.currentThread().sleep(10000);
      } catch (InterruptedException e1) {
        stop = true;
      }
      return;
    }
    
    SolrDocumentList docs = queryResponse.getResults();
    
    for (SolrDocument doc : docs) {
      try {
        PersistenceUtils.fetch(doc, repository, rules, metricsCache,
            simpleHttpClient);
      } catch (InterruptedException e) {
        LOG.error("InterruptedException catched... exiting...");
        stop = true;
        break;
      }
    }
    
    hostLocker.unlockLogFailure(host);
    
  }
  
  /**
   * @throws SolrServerException
   */
  public QueryResponse query(SolrQuery query) throws SolrServerException {
    return getSolrServer().query(query);
  }
  
  public boolean init() {
    
    return true;
    
  }
  
  public SolrServer getSolrServer() {
    // TODO:
    return null;
  }
  
  @Override
  public Thread getThread() {
    return this.thread;
  }
  
  @Override
  public boolean isStop() {
    return this.stop;
  }
  
}
