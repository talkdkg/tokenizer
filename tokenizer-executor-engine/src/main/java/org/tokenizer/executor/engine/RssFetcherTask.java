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

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class RssFetcherTask extends AbstractTask {
  
  private static final Logger LOG = LoggerFactory
      .getLogger(RssFetcherTask.class);
  
  private boolean stop;
  
  private Thread thread;
  
  private LeaderElection leaderElection;
  
  SimpleHttpClient simpleHttpClient = new SimpleHttpClient(
      FetcherUtils.USER_AGENT);
  
  public RssFetcherTask(String fetchName, ZooKeeperItf zk,
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
    
    feedFetcher.setUserAgent(FetcherUtils.USER_AGENT.getUserAgentString());
    FetcherEventListenerImpl listener = new RssFetcherTask.FetcherEventListenerImpl();
    feedFetcher.addFetcherEventListener(listener);
    
    while (!stop && !Thread.interrupted()) {
      try {
        
        if (stop || Thread.interrupted()) {
          return;
        }
        
        processFetch();
        LOG.debug("sleep 1 minute...");
        Thread.sleep(60000);
        
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
      thread = new Thread(RssFetcherTask.this, "RssFetcherTask");
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
  
  // IMPLEMENTATION
  
  static FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
  static FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);
  
  class FetcherEventListenerImpl implements FetcherListener {
    /**
     * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
     */
    public void fetcherEvent(FetcherEvent event) {
      String eventType = event.getEventType();
      if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
        LOG.debug("EVENT: Feed Polled. URL = {}", event.getUrlString());
      } else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
        LOG.debug("EVENT: Feed Retrieved. URL = {}", event.getUrlString());
        
        try {
          process(event.getFeed());
        } catch (Throwable e) {
          LOG.error("", e);
        }
        
      } else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
        LOG.debug("EVENT: Feed Unchanged. URL = {}" + event.getUrlString());
      }
      
    }
  }
  
  private void processFetch() throws InterruptedException {
    
    for (String seed : taskConfiguration.getSeeds()) {
      try {
        URL feedUrl = new URL(seed);
        LOG.info("Retrieving feed " + feedUrl);
        SyndFeed feed = feedFetcher.retrieveFeed(feedUrl);
        LOG.info(feedUrl + " retrieved");
        LOG.info(feedUrl + " has a title: " + feed.getTitle()
            + " and contains " + feed.getEntries().size() + " entries.");
      } catch (Throwable e) {
        LOG.error("Error with {} {}", seed, e.getMessage());
      }
    }
    
  }
  
  static class Cache<K,V> extends LinkedHashMap<K,V> {
    private final int capacity;
    
    public Cache(int capacity) {
      super(capacity + 1, 1.1f, true);
      this.capacity = capacity;
    }
    
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
      return size() > capacity;
    }
  }
  
  private static Cache<SyndEntry,String> cache = new Cache<SyndEntry,String>(
      16 * 1024);
  
  private void process(SyndFeed feed) throws InterruptedException {
    @SuppressWarnings("unchecked")
    List<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();
    for (SyndEntry entry : entries) {
      String test = cache.get(entry);
      if (test == null) {
        cache.put(entry, "");
      } else {
        continue;
      }
      PersistenceUtils.injectIfNotExists(entry, repository, metricsCache);
    }
  }
  
  @Override
  public boolean init() {
    return true;
  }
  
}
