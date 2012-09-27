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
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.tokenizer.core.datum.Outlink;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.core.parser.SimpleParser;
import org.tokenizer.core.urls.BaseUrlFilter;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlFilter;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.ParserPolicy;
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
  BaseUrlValidator urlValidator = new SimpleUrlValidator();
  BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();
  BaseUrlFilter urlFilter = new SimpleUrlFilter();
  
  public ClassicRobotTask(String fetchName, ZooKeeperItf zk,
      TaskConfiguration fetcherConfiguration, Repository repository,
      WritableExecutorModel model, HostLocker hostLocker) {
    super(fetchName, zk, fetcherConfiguration, repository, model,
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
        // refresh home page, store, parse, store outlinks, auto-index to Solr
        String url = "http://" + taskConfiguration.getTld();
        PersistenceUtils.injectIfNotExists(url, repository, metricsCache);
        // query Solr and refresh pages "by query"
        process();
      } catch (InterruptedException e) {
        LOG.debug("interrupted...");
        stop = true;
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
   */
  private void process() {
    
    SolrQuery query = new SolrQuery();
    query.setQuery(taskConfiguration.getProperties().get("query"));
    
    query.setRows(rows);
    
    QueryResponse queryResponse;
    try {
      queryResponse = query(query);
    } catch (SolrServerException e) {
      LOG.error("", e);
      return;
    }
    
    SolrDocumentList docs = queryResponse.getResults();
    
    for (SolrDocument doc : docs) {
      if (stop == true) break;
      try {
        FetchedResult fetchedResult = PersistenceUtils.fetch(doc, repository,
            robotRules, metricsCache, httpClient);
        parse(fetchedResult);
      } catch (InterruptedException e) {
        stop = true;
        break;
      }
    }
    
  }
  
  // private Map<String, SolrServer> shardConnections;
  
  /**
   * @throws SolrServerException
   */
  public QueryResponse query(SolrQuery query) throws SolrServerException {
    return getSolrServer().query(query);
  }
  
  public boolean init() {
    
    try {
      rows = new Integer(taskConfiguration.getProperties().get("rows"));
    } catch (NumberFormatException e) {
      LOG.error("Check configuration file \"rows\" property; using default {}",
          rows);
    }
    
    return true;
    
  }
  
  public SolrServer getSolrServer() {
    
    // TODO: implement...
    return null;
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
  
  private static final int MAX_PARSE_DURATION = 180 * 1000;
  
  private boolean parse(FetchedResult fetchedResult) {
    ParserPolicy parserPolicy = new ParserPolicy(MAX_PARSE_DURATION);
    SimpleParser parser = new SimpleParser(parserPolicy);
    try {
      ParsedDatum parsed = parser.parse(fetchedResult);
      Outlink[] outlinks = parsed.getOutlinks();
      for (Outlink outlink : outlinks) {
        String url = outlink.getToUrl();
        if (!urlValidator.isValid(url)) continue;
        url = urlNormalizer.normalize(url);
        String host = null;
        try {
          host = (new URL(url)).getHost();
        } catch (MalformedURLException e) {
          LOG.debug("can't retrieve host from URL: {}", url);
          continue;
        }
        // This is definition of "domain restricted crawl" (vertical crawl):
        if (!taskConfiguration.getTld().equals(host)) {
          LOG.debug("extrenal host ignored: {}, URL: {}", host, url);
          continue;
        }
        // performance improvement trick:
        if (cache.containsKey(url)) continue;
        PersistenceUtils.injectIfNotExists(url, repository, metricsCache);
        cache.put(url, null);
      }
    } catch (Exception e) {
      LOG.error("", e);
      return false;
    }
    return true;
  }
  
  // Create cache to store URLs from Outlinks
  // If the cache is to be used by multiple thread
  // the cache must be wrapped with code to synchronize the methods
  // cache = (Map)Collections.synchronizedMap(cache);
  final int MAX_ENTRIES = 1000;
  @SuppressWarnings("serial")
  Map<String,String> cache = new LinkedHashMap<String,String>(MAX_ENTRIES + 1,
      .75F, true) {
    public boolean removeEldestEntry(
        @SuppressWarnings("rawtypes") Map.Entry eldest) {
      return size() > MAX_ENTRIES;
    }
  };
  
}
