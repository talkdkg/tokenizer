/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.executor.engine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.RssFetcherTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.FetcherListener;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

import crawlercommons.fetcher.http.SimpleHttpFetcher;

public class RssFetcherTask extends AbstractTask<RssFetcherTaskConfiguration> {

    SimpleHttpFetcher simpleHttpClient = new SimpleHttpFetcher(FetcherUtils.USER_AGENT);
    static FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
    static FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedInfoCache);

    public RssFetcherTask(final UUID uuid, final String friendlyName, final ZooKeeperItf zk,
        final RssFetcherTaskConfiguration taskConfiguration, final CrawlerRepository repository,
        final WritableExecutorModel fetcherModel, final HostLocker hostLocker) {

        super(uuid, friendlyName, zk, taskConfiguration, repository, fetcherModel, hostLocker);

        RssFetcherTask.feedFetcher.setUserAgent(FetcherUtils.USER_AGENT.getUserAgentString());
        FetcherEventListenerImpl listener = new RssFetcherTask.FetcherEventListenerImpl();
        RssFetcherTask.feedFetcher.addFetcherEventListener(listener);

    }

    class FetcherEventListenerImpl implements FetcherListener {

        /**
         * @see com.sun.syndication.fetcher.FetcherListener#fetcherEvent(com.sun.syndication.fetcher.FetcherEvent)
         */
        @Override
        public void fetcherEvent(final FetcherEvent event) {
            String eventType = event.getEventType();
            if (FetcherEvent.EVENT_TYPE_FEED_POLLED.equals(eventType)) {
                LOG.debug("EVENT: Feed Polled. URL = {}", event.getUrlString());
            }
            else if (FetcherEvent.EVENT_TYPE_FEED_RETRIEVED.equals(eventType)) {
                LOG.debug("EVENT: Feed Retrieved. URL = {}", event.getUrlString());
                try {
                    process(event.getFeed());
                }
                catch (Throwable e) {
                    LOG.error("", e);
                }
            }
            else if (FetcherEvent.EVENT_TYPE_FEED_UNCHANGED.equals(eventType)) {
                LOG.debug("EVENT: Feed Unchanged. URL = {}" + event.getUrlString());
            }
        }
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        for (String seed : taskConfiguration.getSeeds()) {
            // try {
            try {
                URL feedUrl = new URL(seed);
                LOG.info("Retrieving feed " + feedUrl);
                SyndFeed feed = feedFetcher.retrieveFeed(feedUrl);
                LOG.info(feedUrl + " retrieved");
                LOG.info(feedUrl + " has a title: " + feed.getTitle() + " and contains " + feed.getEntries().size()
                    + " entries.");
            }
            catch (MalformedURLException e) {
                LOG.error("", e);
            }
            catch (IOException e) {
                LOG.error("", e);
            }
            catch (FeedException e) {
                LOG.error("", e);
            }
            catch (FetcherException e) {
                LOG.error("", e);
            }
        }
    }

    static class Cache<K, V> extends LinkedHashMap<K, V> {

        private final int capacity;

        public Cache(final int capacity) {
            super(capacity + 1, 1.1f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private static Cache<SyndEntry, String> cache = new Cache<SyndEntry, String>(16 * 1024);

    private void process(final SyndFeed feed) throws InterruptedException {
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries) {
            String test = cache.get(entry);
            if (test == null) {
                cache.put(entry, "");
            }
            else {
                continue;
            }
            // TODO:
            // PersistenceUtils.injectIfNotExists(entry, repository,
            // metricsCache);
        }
    }


}
