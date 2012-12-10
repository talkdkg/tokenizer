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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.utils.CharsetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.datum.Outlink;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.parser.SimpleParser;
import org.tokenizer.core.urls.BaseUrlFilter;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlFilter;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.ParserPolicy;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.robots.BaseRobotRules;

public class PersistenceUtils {

    private static final Logger LOG = LoggerFactory
            .getLogger(PersistenceUtils.class);

    public static FetchedResult fetch(UrlRecord urlRecord,
            CrawlerRepository repository, BaseRobotRules baseRobotRules,
            MetricsCache metricsCache, SimpleHttpFetcher simpleHttpClient,
            String hostConstraint) throws ConnectionException,
            InterruptedException {
        metricsCache.increment(MetricsCache.URL_TOTAL_KEY);
        if (!checkRobotRules(urlRecord, repository, baseRobotRules,
                metricsCache))
            return null;
        FetchedResult fetchedResult = fetch(urlRecord, repository,
                metricsCache, simpleHttpClient);
        if (fetchedResult == null) {
            repository.update(urlRecord);
            return null;
        }
        parse(fetchedResult, repository, hostConstraint);
        WebpageRecord webpageRecord = update(fetchedResult, urlRecord,
                repository, metricsCache);
        urlRecord.setDigest(webpageRecord.getDigest());
        repository.update(urlRecord);
        return fetchedResult;
    }

    public static WebpageRecord update(FetchedResult fetchedResult,
            UrlRecord urlRecord, CrawlerRepository repository,
            MetricsCache metricsCache) throws InterruptedException,
            ConnectionException {
        WebpageRecord webpageRecord = new WebpageRecord(
                fetchedResult.getContent());
        String charset = CharsetUtils.clean(HttpUtils
                .getCharsetFromContentType(fetchedResult.getContentType()));
        webpageRecord.setCharset(charset);
        webpageRecord.setTimestamp(urlRecord.getTimestamp());
        webpageRecord.setUrl(urlRecord.getUrl());
        repository.insertIfNotExists(webpageRecord);
        return webpageRecord;
    }

    public static boolean checkRobotRules(UrlRecord urlRecord,
            CrawlerRepository repository, BaseRobotRules baseRobotRules,
            MetricsCache metricsCache) throws InterruptedException,
            ConnectionException {
        String url = urlRecord.getUrl();
        if (!baseRobotRules.isAllowed(url)) {
            urlRecord.setHttpResponseCode(-1);
            urlRecord.setTimestamp(new Date());
            repository.update(urlRecord);
            metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);
            return false;
        }
        return true;
    }

    public static FetchedResult fetch(UrlRecord record,
            CrawlerRepository repository, MetricsCache metricsCache,
            SimpleHttpFetcher simpleHttpClient) throws InterruptedException {
        String url = record.getUrl();
        FetchedResult fetchedResult = null;
        long start = System.currentTimeMillis();
        try {
            fetchedResult = simpleHttpClient.get(url, null);
        } catch (HttpFetchException e) {
            record.setHttpResponseCode(((HttpFetchException) e).getHttpStatus());
            record.setTimestamp(new Date());
            return null;
        } catch (BaseFetchException e) {
            if (e.getMessage().contains("Aborted due to INTERRUPTED"))
                throw new InterruptedException("Aborted...");
            // e.printStackTrace();
            // record.setHttpResponseCode(-2);
            record.setTimestamp(new Date());
            metricsCache.increment(MetricsCache.OTHER_ERRORS);
            return null;
        }
        record.setTimestamp(new Date());
        if (fetchedResult.getHttpStatus() >= 200
                && fetchedResult.getHttpStatus() < 300) {
            metricsCache.increment(MetricsCache.TOTAL_RESPONSE_TIME_KEY,
                    System.currentTimeMillis() - start);
            metricsCache.increment(MetricsCache.URL_OK_KEY);
        } else {
            metricsCache.increment(MetricsCache.URL_ERROR_KEY);
        }
        record.setHttpResponseCode(fetchedResult.getHttpStatus());
        return fetchedResult;
    }

    public static void injectIfNotExists(SyndEntry entry,
            CrawlerRepository repository, MetricsCache metricsCache)
            throws InterruptedException {
        // TODO:
        // record = repository.create(record);
        // metricsCache.increment(MetricsCache.LILY_INJECTS_COUNT);
    }

    private static void createRecord(SyndEntry entry,
            CrawlerRepository repository) throws InterruptedException {
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
    private static BaseUrlFilter urlFilter = new SimpleUrlFilter();
    private static final int MAX_PARSE_DURATION = 180 * 1000;
    // Create cache to store URLs from Outlinks
    // If the cache is to be used by multiple thread
    // the cache must be wrapped with code to synchronize the methods
    // cache = (Map)Collections.synchronizedMap(cache);
    private static final int MAX_ENTRIES = 1000;
    @SuppressWarnings("serial")
    private static Map<String, String> cache = new LinkedHashMap<String, String>(
            MAX_ENTRIES + 1, .75F, true) {

        public boolean removeEldestEntry(@SuppressWarnings("rawtypes")
        Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    private static boolean parse(FetchedResult fetchedResult,
            CrawlerRepository repository, String hostConstraint)
            throws InterruptedException, ConnectionException {
        ParserPolicy parserPolicy = new ParserPolicy(MAX_PARSE_DURATION);
        SimpleParser parser = new SimpleParser(parserPolicy);
        ParsedDatum parsed = parser.parse(fetchedResult);
        Outlink[] outlinks = parsed.getOutlinks();
        for (Outlink outlink : outlinks) {
            String url = outlink.getToUrl();
            if (!urlValidator.isValid(url)) {
                continue;
            }
            url = urlNormalizer.normalize(url);
            String host = HttpUtils.getHost(url);
            if (HttpUtils.EMPTY_STRING.equals(host)) {
                continue;
            }
            // This is definition of "domain restricted crawl" (vertical crawl):
            if (!hostConstraint.equals(host)) {
                LOG.debug("extrenal host ignored: {}, URL: {}", host, url);
                continue;
            }
            // TODO: move it to repository; performance improvement trick:
            if (cache.containsKey(url)) {
                continue;
            }
            UrlRecord urlRecord = new UrlRecord(url);
            repository.insertIfNotExists(urlRecord);
            cache.put(url, null);
        }
        return true;
    }
}
