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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.BaseFetcher;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.UnknownFormatException;

public class SitemapsTask extends AbstractTask {
  
  private static final Logger LOG = LoggerFactory.getLogger(SitemapsTask.class);
  
  private boolean stop;
  
  private Thread thread;
  
  private LeaderElection leaderElection;
  
  // Delay 4 hours between subsequent refresh of sitemaps
  private static final long DELAY = 4 * 3600 * 1000L;
  
  public SitemapsTask(String fetchName, ZooKeeperItf zk,
      TaskConfiguration fetcherConfiguration, Repository repository,
      WritableExecutorModel fetcherModel, HostLocker hostLocker) {
    super(fetchName, zk, fetcherConfiguration, repository, fetcherModel,
        hostLocker);
    
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
  public synchronized void start() throws InterruptedException,
      LeaderElectionSetupException, KeeperException {
    leaderElection = new LeaderElection(zk, "Master "
        + this.getClass().getCanonicalName(), "/org/tokenizer/executor/engine/"
        + this.getClass().getCanonicalName() + "/" + this.taskName,
        new MyLeaderElectionCallback());
  }
  
  @Override
  public void run() {
    
    while (!stop && !Thread.interrupted()) {
      try {
        
        if (stop || Thread.interrupted()) {
          return;
        }
        
        processFetch();
        Thread.sleep(DELAY);
        
      } catch (InterruptedException e) {
        break;
        
      } catch (Throwable t) {
        LOG.error("Error processing", t);
      }
    }
  }
  
  private void processFetch() throws InterruptedException {
    
    BaseFetcher fetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT, 1);
    LOG.warn("User-Agent: {}", fetcher.getUserAgent().getUserAgentString());
    fetcher.setDefaultMaxContentSize(Integer.MAX_VALUE);
    
    BaseRobotsParser parser = new SimpleRobotRulesParser();
    
    URL robotsUrl;
    
    try {
      robotsUrl = new URL("http://" + taskConfiguration.getTld()
          + "/robots.txt");
    } catch (MalformedURLException e) {
      LOG.error("", e);
      return;
    }
    
    BaseRobotRules rules = RobotUtils.getRobotRules(fetcher, parser, robotsUrl);
    
    List<String> sitemaps = rules.getSitemaps();
    
    for (String sitemapIndexUrl : sitemaps) {
      LOG.info("fetching sitemap index: {}", sitemapIndexUrl);
      SiteMapParser sitemapParser = new SiteMapParser();
      String contentType = "text/xml";
      
      FetchedResult result;
      
      try {
        result = fetcher.get(sitemapIndexUrl);
        contentType = result.getContentType();
      } catch (BaseFetchException e) {
        LOG.error("", e);
        continue;
      }
      
      byte[] content = result.getContent();
      AbstractSiteMap abstractSitemap;
      
      try {
        abstractSitemap = sitemapParser.parseSiteMap(contentType, content,
            new URL(sitemapIndexUrl));
      } catch (MalformedURLException e) {
        LOG.error("", e);
        continue;
      } catch (UnknownFormatException e) {
        LOG.error("", e);
        continue;
      } catch (IOException e) {
        LOG.error("", e);
        continue;
      }
      
      if (!abstractSitemap.isIndex()) {
        LOG.error("found sitemap instead of sitemap index: {}", sitemapIndexUrl);
        continue;
      }
      
      SiteMapIndex sitemapIndex = (SiteMapIndex) abstractSitemap;
      
      LOG.info("parsed sitemap index: " + sitemapIndex.toString());
      
      metricsCache.increment(MetricsCache.SITEMAP_INDEXES_PROCESSED);
      
      for (SiteMap sitemap : sitemapIndex.getSitemaps()) {
        
        String sitemapUrl = sitemap.getUrl().toString();
        LOG.debug("fetching sitemap: {}", sitemapUrl);
        try {
          result = fetcher.get(sitemapUrl);
        } catch (BaseFetchException e) {
          // TODO: this is stupid... I am forced:
          if (e.getMessage().contains("Aborted due to INTERRUPTED")) {
            throw new InterruptedException();
          } else {
            LOG.error("", e);
            continue;
          }
        }
        
        content = result.getContent();
        
        LOG.debug("contentType: {} \t URL: {}", result.getContentType(),
            result.getFetchedUrl());
        
        try {
          abstractSitemap = sitemapParser.parseSiteMap(result.getContentType(),
              result.getContent(), sitemap.getUrl());
        } catch (UnknownFormatException e) {
          LOG.error("", e);
          continue;
        } catch (IOException e) {
          LOG.error("", e);
          continue;
        }
        
        if (abstractSitemap.isIndex()) {
          LOG.error("found sitemap index instead of sitemap: {}",
              sitemapIndexUrl);
          continue;
        }
        
        sitemap = (SiteMap) abstractSitemap;
        for (SiteMapURL siteMapURL : sitemap.getSiteMapUrls()) {
          PersistenceUtils.injectIfNotExists(siteMapURL.getUrl().toString(),
              repository, metricsCache);
        }
        metricsCache.increment(MetricsCache.SITEMAPS_PROCESSED);
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
      thread = new Thread(SitemapsTask.this, "SitemapsTask");
      thread.start();
      LOG.warn("Activated as Leader.");
    }
    
    public void deactivateAsLeader() throws Exception {
      LOG.warn("deactivateAsLeader...");
      shutdown();
      LOG.warn("Deactivated as Leader.");
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
  public boolean init() {
    return true;
  }
  
}
