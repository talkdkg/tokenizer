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
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.engine.RssFetcherTask.FetcherEventListenerImpl;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import com.google.common.cache.Cache;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

import crawlercommons.fetcher.http.SimpleHttpFetcher;

public class RssFetcherTask extends AbstractTask {
    private static final Logger LOG = LoggerFactory
            .getLogger(RssFetcherTask.class);
    SimpleHttpFetcher simpleHttpClient = new SimpleHttpFetcher(
            FetcherUtils.USER_AGENT);
    static FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
    static FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);

    public RssFetcherTask(String fetchName, ZooKeeperItf zk,
            TaskConfiguration fetcherConfiguration,
            CrawlerHBaseRepository repository,
            WritableExecutorModel fetcherModel, HostLocker hostLocker) {
        super(fetchName, zk, fetcherConfiguration, repository, fetcherModel,
                hostLocker);
        RssFetcherTask.feedFetcher.setUserAgent(FetcherUtils.USER_AGENT
                .getUserAgentString());
        FetcherEventListenerImpl listener = new RssFetcherTask.FetcherEventListenerImpl();
        RssFetcherTask.feedFetcher.addFetcherEventListener(listener);
    }

    class FetcherEventListenerImpl implements FetcherListener {
        /**
         * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
         */
        public void fetcherEvent(FetcherEvent event) {
            String eventType = event.getEventType();
            if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
                LOG.debug("EVENT: Feed Polled. URL = {}", event.getUrlString());
            } else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                LOG.debug("EVENT: Feed Retrieved. URL = {}",
                        event.getUrlString());
                try {
                    process(event.getFeed());
                } catch (Throwable e) {
                    LOG.error("", e);
                }
            } else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                LOG.debug("EVENT: Feed Unchanged. URL = {}"
                        + event.getUrlString());
            }
        }
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        for (String seed : taskConfiguration.getSeeds()) {
            try {
                URL feedUrl = new URL(seed);
                LOG.info("Retrieving feed " + feedUrl);
                SyndFeed feed = feedFetcher.retrieveFeed(feedUrl);
                LOG.info(feedUrl + " retrieved");
                LOG.info(feedUrl + " has a title: " + feed.getTitle()
                        + " and contains " + feed.getEntries().size()
                        + " entries.");
            } catch (Throwable e) {
                LOG.error("Error with {} {}", seed, e.getMessage());
            }
        }
    }

    static class Cache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public Cache(int capacity) {
            super(capacity + 1, 1.1f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private static Cache<SyndEntry, String> cache = new Cache<SyndEntry, String>(
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
            // TODO:
            // PersistenceUtils.injectIfNotExists(entry, repository,
            // metricsCache);
        }
    }
}