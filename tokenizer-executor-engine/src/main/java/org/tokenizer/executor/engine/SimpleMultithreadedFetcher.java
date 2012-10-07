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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class SimpleMultithreadedFetcher extends AbstractTask {
  
  private static final Logger LOG = LoggerFactory
      .getLogger(SimpleMultithreadedFetcher.class);
  
  /** used for generic retrieval */
  private final SimpleHttpFetcher simpleHttpClient;
  
  /** for robots.txt and sitemaps */
  private final BaseHttpFetcher baseFetcher;
  
  private Set<String> hosts = Collections
      .synchronizedSet(new HashSet<String>());
  
  private boolean stop = false;
  
  private static final int MAX_THREADS = 64;
  List<Thread> threads = new ArrayList<Thread>();
  
  private final static long HOSTS_REFRESH_DELAY = 60 * 60 * 1000L;
  
  public SimpleMultithreadedFetcher(String fetchName, ZooKeeperItf zk,
      TaskConfiguration fetcherConfiguration,
      CrawlerHBaseRepository repository, WritableExecutorModel fetcherModel,
      HostLocker hostLocker) {
    
    super(fetchName, zk, fetcherConfiguration, repository, fetcherModel,
        hostLocker);
    
    this.simpleHttpClient = new SimpleHttpFetcher(FetcherUtils.USER_AGENT);
    this.baseFetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT, 1024);
    this.baseFetcher.setDefaultMaxContentSize(4 * 1024 * 1024);
    
  }
  
  @Override
  public void start() {
    
    stop();
    
    LOG.warn(
        "starting threads... hardcoded to MIN(<total number of sites>, {})...",
        MAX_THREADS);
    
    stop = false;
    
    // TODO: the problem is that IF hosts is empty we will need to restart task
    // explicitly over time
    for (int i = 1; i < MAX_THREADS; i++) {
      if (i > hosts.size()) break;
      Thread thread = new Thread(new MyFetcherThread(), "MyFetcherThread[" + i
          + "]");
      threads.add(thread);
      LOG.warn("starting thread {}", thread.getName());
      thread.start();
    }
    
  }
  
  @Override
  public void stop() {
    if (!threads.isEmpty()) {
      LOG.warn("Start was requested, but old threads are still there. Stopping now...");
      stop = true;
      for (Thread thread : threads) {
        if (!thread.isAlive()) {
          threads.remove(thread);
          thread = null;
          continue;
        }
        thread.interrupt();
        LOG.warn("Waiting for a thread {} to die...", thread.getName());
        threads.remove(thread);
        thread = null;
      }
      threads.clear();
    }
  }
  
  @Override
  public Thread getThread() {
    throw new RuntimeException("Not implemented!");
  }
  
  @Override
  public boolean isStop() {
    return stop;
  }
  
  public boolean init() {
    
    try {
      HostsMonitorThread monitor = new HostsMonitorThread();
      monitor.start();
      while (hosts.isEmpty())
        Thread.currentThread().sleep(1000);
      
    } catch (Throwable e) {
      LOG.error("", e);
      return false;
    }
    return true;
    
  }
  
  @Override
  public void run() {
    throw new RuntimeException("Not implemented!");
  }
  
  private class MyFetcherThread implements Runnable {
    
    @Override
    public void run() {
      
      while (!stop) {
        for (String host : hosts) {
          
          try {
            if (hostLocker.exists(host)) continue;
          } catch (HostLockException e) {
            LOG.error("", e);
            continue;
          } catch (InterruptedException e) {
            LOG.error("InterruptedException catched... exiting...");
            return;
          } catch (KeeperException e) {
            LOG.error("", e);
            continue;
          }
          
          try {
            if (!hostLocker.lock(host)) continue;
          } catch (HostLockException e) {
            LOG.error("", e);
            continue;
          }
          
          BaseRobotsParser parser = new SimpleRobotRulesParser();
          BaseRobotRules rules = null;
          try {
            rules = RobotUtils.getRobotRules(baseFetcher, parser, new URL(
                "http://" + host + "/robots.txt"));
          } catch (MalformedURLException e) {
            LOG.error("", e);
            continue;
            // throw new CrawlerError(e);
          }
          
          // TODO: fetch URLs
          
          hostLocker.unlockLogFailure(host);
          
        }
      }
      
    }
    
  }
  
  public class HostsMonitorThread extends Thread {
    
    @Override
    public void run() {
      try {
        while (!stop) {
          refreshHosts();
          Thread.currentThread().sleep(HOSTS_REFRESH_DELAY);
        }
      } catch (InterruptedException ex) {
        LOG.error("interrupted...");
        return;
      }
    }
    
    /**
     * Retrieves (virtually unlimited) list of hosts
     */
    private synchronized void refreshHosts() throws InterruptedException {
      
      // TODO:
    }
  }
}
