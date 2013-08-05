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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.robots.BaseRobotRules;

public class PersistenceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceUtils.class);

    public static boolean checkRobotRules(final UrlRecord urlRecord, final CrawlerRepository repository,
            final BaseRobotRules baseRobotRules, final MetricsCache metricsCache) throws InterruptedException,
            ConnectionException {
        String url = urlRecord.getBaseUrl();
        if (!baseRobotRules.isAllowed(url)) {
            urlRecord.setHttpStatus(-1);
            urlRecord.setFetchTime(System.currentTimeMillis());
            repository.update(urlRecord);
            metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);
            return false;
        }
        return true;
    }

    @Deprecated
    public static FetchedResult fetch(final UrlRecord record, final CrawlerRepository repository,
            final MetricsCache metricsCache, final SimpleHttpFetcher simpleHttpClient) throws InterruptedException {
        String url = record.getBaseUrl();
        FetchedResult fetchedResult = null;
        long start = System.currentTimeMillis();
        try {
            fetchedResult = simpleHttpClient.get(url, null);
        } catch (RedirectFetchException e) {
            String redirectedUrl = e.getRedirectedUrl();
            LOG.debug("Redirected to {}", redirectedUrl);
            record.setHttpStatus(e.getHttpStatusCode());
            record.setFetchTime(System.currentTimeMillis());
            metricsCache.increment(MetricsCache.REDIRECT_COUNT);
            String normalizedRedirectedUrl = urlNormalizer.normalize(redirectedUrl);
            String redirectedHost = HttpUtils.getHost(normalizedRedirectedUrl);
            if (record.getBaseHost().equals(redirectedHost)) {
                UrlRecord urlRecord = new UrlRecord(normalizedRedirectedUrl);
                try {
                    repository.insert(urlRecord);
                } catch (ConnectionException e1) {
                    LOG.error("repository not available... sleeping 60 seconds", e);
                    Thread.sleep(60000);
                }
            }
            else {
                LOG.debug("redirected extrenal host ignored: {}", normalizedRedirectedUrl);
            }
            return null;
        } catch (HttpFetchException e) {
            record.setHttpStatus(e.getHttpStatus());
            record.setFetchTime(System.currentTimeMillis());
            return null;
        } catch (BaseFetchException e) {
            if (e.getMessage().contains("Aborted due to INTERRUPTED")) {
                throw new InterruptedException("Aborted...");
            }
            // e.printStackTrace();
            // record.setHttpResponseCode(-2);
            record.setFetchTime(System.currentTimeMillis());
            metricsCache.increment(MetricsCache.OTHER_ERRORS);
            return null;
        }
        record.setFetchTime(System.currentTimeMillis());
        if (fetchedResult.getHttpStatus() >= 200 && fetchedResult.getHttpStatus() < 300) {
            metricsCache.increment(MetricsCache.TOTAL_HTTP_RESPONSE_TIME_MS, System.currentTimeMillis() - start);
            metricsCache.increment(MetricsCache.URL_OK_KEY);
        }
        else {
            metricsCache.increment(MetricsCache.URL_ERROR_KEY);
        }
        record.setHttpStatus(fetchedResult.getHttpStatus());
        return fetchedResult;
    }

    public static void injectIfNotExists(final SyndEntry entry, final CrawlerRepository repository,
            final MetricsCache metricsCache) throws InterruptedException {
        // TODO:
        // record = repository.create(record);
        // metricsCache.increment(MetricsCache.LILY_INJECTS_COUNT);
    }

    private static void createRecord(final SyndEntry entry, final CrawlerRepository repository)
            throws InterruptedException {
        // TODO: not implemented yet
        String url = entry.getLink().trim();
        List<SyndCategory> categories = entry.getCategories();
        List<String> cats = new ArrayList<String>();
        for (SyndCategory category : categories) {
            cats.add(category.getName().trim());
        }
    }

    private static BaseUrlValidator urlValidator = new SimpleUrlValidator();
    private static BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();
    // Create cache to store URLs from Outlinks
    // If the cache is to be used by multiple thread
    // the cache must be wrapped with code to synchronize the methods
    // cache = (Map)Collections.synchronizedMap(cache);

}
