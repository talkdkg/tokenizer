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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tika.utils.CharsetUtils;
import org.tokenizer.core.datum.Outlink;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.parser.SimpleParser;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.ParserPolicy;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.TimestampUrlIDX;
import org.tokenizer.crawler.db.model.UrlRecord;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.AbstractFetcherTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public abstract class AbstractFetcherTask<T extends AbstractFetcherTaskConfiguration> extends AbstractTask<T> {

    private static final int ROBOTS_PROTECTED_STATUS = -1;
    private static final int FETCHED_RESULT_EMPTY_STATUS = -2;
    private static final String ROBOTS_PROTECTED_REASON = "Robots Protected";

    private static final int MAX_ENTRIES = 10000;
    private Map<String, String> urlCache = new LinkedHashMap<String, String>(MAX_ENTRIES + 1, .75F, true) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean removeEldestEntry(@SuppressWarnings("rawtypes")
        final Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    private static final long DEFAULT_REFRESH_DELAY = 10 * 1000;

    private static final int MAX_PARSE_DURATION = 180 * 1000;

    private final SimpleHttpFetcher httpClient;
    private final BaseHttpFetcher robotFetcher;
    BaseRobotRules robotRules = null;
    private static final int DEFAULT_MAX_THREADS = 1024;

    private static BaseUrlValidator urlValidator = new SimpleUrlValidator();
    private static BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();

    //@formatter:off
    @SuppressWarnings("unchecked")
    public AbstractFetcherTask(
        final UUID uuid, 
        final String friendlyName, 
        final ZooKeeperItf zk,
        final T taskConfiguration, 
        final CrawlerRepository crawlerRepository,
        final WritableExecutorModel fetcherModel, 
        final HostLocker hostLocker) {
        //@formatter:on

        super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository, fetcherModel, hostLocker);

        UserAgent userAgent = new UserAgent(this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(), this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");

        // getTaskConfiguration().getAgentName();

        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        this.httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024);
        robotFetcher = RobotUtils.createFetcher(userAgent, 1);
        robotFetcher.setDefaultMaxContentSize(1024 * 1024);
        LOG.info("Instance created");

    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {

        refreshRobotRules();
        refreshHome();

        List<TimestampUrlIDX> timestampUrlIDXs = crawlerRepository.loadTimestampUrlIDX(taskConfiguration.getHost());

        for (TimestampUrlIDX timestampUrlIDX : timestampUrlIDXs) {

            if (!accept(timestampUrlIDX.getUrl())) {
                crawlerRepository.delete(timestampUrlIDX);
                UrlRecord urlRecord = crawlerRepository.retrieveUrlRecord(timestampUrlIDX.getUrl());
                if (urlRecord != null) {
                    crawlerRepository.delete(urlRecord);
                    crawlerRepository.deleteWebpageRecord(urlRecord.getWebpageDigest());
                }
                continue;
            }

            long start = System.currentTimeMillis();
            long fetchAttemptTimestamp = System.currentTimeMillis();

            String url = timestampUrlIDX.getUrl();

            LOG.debug("Trying URL: {}", url);

            UrlRecord urlRecord = crawlerRepository.retrieveUrlRecord(url);

            if (urlRecord == null) {
                urlRecord = new UrlRecord(url);
                crawlerRepository.insert(urlRecord);
            }

            if (!robotRules.isAllowed(url)) {

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
                crawlerRepository.insert(timestampUrlIDX);

                urlRecord.setFetchTime(fetchAttemptTimestamp);
                urlRecord.setHttpStatus(ROBOTS_PROTECTED_STATUS);
                urlRecord.setReasonPhrase(ROBOTS_PROTECTED_REASON);
                crawlerRepository.update(urlRecord);
                metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);

                continue;

            }

            FetchedResult fetchedResult = null;
            try {
                fetchedResult = httpClient.get(url, null);
            } catch (RedirectFetchException e) {

                metricsCache.increment(MetricsCache.REDIRECT_COUNT);

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
                crawlerRepository.insert(timestampUrlIDX);

                urlRecord.setHttpStatus(e.getHttpStatusCode());
                urlRecord.setFetchTime(fetchAttemptTimestamp);
                crawlerRepository.update(urlRecord);

                String redirectedUrl = e.getRedirectedUrl();
                redirectedUrl = urlNormalizer.normalize(redirectedUrl);
                LOG.debug("Redirected to (normalized URL): {}", redirectedUrl);

                if (!urlValidator.isValid(redirectedUrl)) {
                    LOG.debug("Redirected URL is not valid: {}", redirectedUrl);
                    continue;
                }

                String redirectedHost = HttpUtils.getHost(redirectedUrl);
                if (!urlRecord.getHost().equals(redirectedHost)) {
                    LOG.debug("redirected extrenal host ignored: {}", redirectedHost);
                    continue;
                }

                if (crawlerRepository.retrieveUrlRecord(redirectedUrl) == null) {
                    TimestampUrlIDX o = new TimestampUrlIDX(redirectedUrl);
                    crawlerRepository.insert(timestampUrlIDX);
                    UrlRecord redirectedUrlRecord = new UrlRecord(redirectedUrl);
                    crawlerRepository.insert(redirectedUrlRecord);
                }

            } catch (HttpFetchException e) {

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
                crawlerRepository.insert(timestampUrlIDX);

                urlRecord.setHttpStatus(e.getHttpStatus());
                urlRecord.setFetchTime(fetchAttemptTimestamp);
                crawlerRepository.update(urlRecord);

            } catch (BaseFetchException e) {
                if (e.getMessage().contains("Aborted due to INTERRUPTED")) {
                    throw new InterruptedException("Aborted...");
                }
                // urlRecord.setHttpStatus(-2);
                // urlRecord.setFetchTime(fetchAttemptTimestamp);
                // crawlerRepository.update(urlRecord);
                // crawlerRepository.delete(timestampUrlIDX);
                // timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
                // crawlerRepository.insert(timestampUrlIDX);
            }

            if (fetchedResult != null && fetchedResult.getHttpStatus() >= 200 && fetchedResult.getHttpStatus() < 300) {
                metricsCache.increment(MetricsCache.TOTAL_HTTP_RESPONSE_TIME_MS, System.currentTimeMillis() - start);
                metricsCache.increment(MetricsCache.URL_OK_KEY);
            }
            else {
                metricsCache.increment(MetricsCache.URL_ERROR_KEY);
            }

            urlRecord.setFetchTime(fetchAttemptTimestamp);
            urlRecord.setFetchAttemptCounter(urlRecord.getFetchAttemptCounter() + 1);
            if (fetchedResult == null) {

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
                crawlerRepository.insert(timestampUrlIDX);

                urlRecord.setHttpStatus(FETCHED_RESULT_EMPTY_STATUS);
                crawlerRepository.update(urlRecord);
                continue;

            }

            urlRecord.setContentType(fetchedResult.getContentType());
            urlRecord.setFetchedUrl(fetchedResult.getFetchedUrl());
            urlRecord.setHeaders(fetchedResult.getHeaders());
            urlRecord.setHostAddress(fetchedResult.getHostAddress());
            urlRecord.setHttpStatus(fetchedResult.getHttpStatus());
            urlRecord.setNewBaseUrl(fetchedResult.getNewBaseUrl());
            urlRecord.setNumRedirects(fetchedResult.getNumRedirects());
            urlRecord.setReasonPhrase(fetchedResult.getReasonPhrase());

            // String charset = CharsetUtils.clean(HttpUtils.getCharsetFromContentType(fetchedResult.getContentType()));
            WebpageRecord webpageRecord = new WebpageRecord(fetchedResult);
            crawlerRepository.insertIfNotExists(webpageRecord);

            urlRecord.setWebpageDigest(webpageRecord.getDigest());
            crawlerRepository.update(urlRecord);

            // if all prev. were successful then... note that previously we did it before UrlRecord updates...
            crawlerRepository.delete(timestampUrlIDX);
            timestampUrlIDX.setTimestamp(fetchAttemptTimestamp);
            crawlerRepository.insert(timestampUrlIDX);

            processFetchedResult(fetchedResult);

        }

        // fetchedResult = PersistenceUtils.fetch(urlRecord,
        // crawlerRepository, robotRules, metricsCache, httpClient,
        // taskConfiguration.getHost(), urlFilter);
        // LOG.trace("Fetching Result: {} {}", urlRecord, fetchedResult);

        if (timestampUrlIDXs == null || timestampUrlIDXs.size() == 0) {
            // to prevent spin loop in case if collection is empty:
            LOG.warn("no not-crawled URLs were found; sleeping 1 hour...");
            Thread.sleep(1 * 3600 * 1000L);
        }

    }

    /**
     * Should be called few times a day
     * 
     * @return
     */
    protected synchronized boolean refreshRobotRules() {
        BaseRobotsParser parser = new SimpleRobotRulesParser();
        try {
            robotRules = RobotUtils.getRobotRules(robotFetcher, parser, new URL("http://" + taskConfiguration.getHost()
                    + "/robots.txt"));
        } catch (MalformedURLException e) {
            LOG.error("", e);
            return false;
        }
        return true;
    }

    protected void refreshHome() throws ConnectionException, InterruptedException {

        String url = "http://" + taskConfiguration.getHost() + "/";
        UrlRecord urlRecord = crawlerRepository.retrieveUrlRecord(url);

        if (urlRecord == null) {
            LOG.info("UrlRecord not found: {}", url);
            urlRecord = new UrlRecord(url);
            crawlerRepository.insert(urlRecord);
        }
        else {
            if (System.currentTimeMillis() - urlRecord.getFetchTime() <= DEFAULT_REFRESH_DELAY) {
                LOG.warn("Sleeping {} ms before attempting to refresh homepage", DEFAULT_REFRESH_DELAY);
                Thread.sleep(DEFAULT_REFRESH_DELAY);
            }
            TimestampUrlIDX timestampUrlIDX = new TimestampUrlIDX(url);
            timestampUrlIDX.setTimestamp(urlRecord.getFetchTime());
            LOG.info("Deleting existing TimestampUrlIDX record for homepage: {}", url);
            crawlerRepository.delete(timestampUrlIDX);
        }
        LOG.info("Resetting (or Inserting) TimestampUrlIDX record for homepage: {}", url);
        TimestampUrlIDX timestampUrlIDX = new TimestampUrlIDX(url);
        timestampUrlIDX.setTimestamp(0);
        crawlerRepository.insert(timestampUrlIDX);

    }

    protected abstract boolean accept(final String url);

    protected void processFetchedResult(final FetchedResult fetchedResult) throws InterruptedException,
            ConnectionException {

        ParserPolicy parserPolicy = new ParserPolicy(MAX_PARSE_DURATION);
        SimpleParser parser = new SimpleParser(parserPolicy);

        ParsedDatum parsedDatum = parser.parse(fetchedResult);
        if (parsedDatum == null) {
            return;
        }

        Outlink[] outlinks = parsedDatum.getOutlinks();

        for (Outlink outlink : outlinks) {

            LOG.debug("outlink: {}", outlink);

            String url = outlink.getToUrl();

            if (!urlValidator.isValid(url)) {
                continue;
            }

            url = urlNormalizer.normalize(url);

            String host = HttpUtils.getHost(url);
            if (HttpUtils.EMPTY_STRING.equals(host)) {
                continue;
            }

            // This is definition of "domain constrained crawl" (vertical crawl):
            String baseHost = HttpUtils.getHost(fetchedResult.getBaseUrl());
            if (!baseHost.equals(host)) {
                LOG.debug("extrenal host ignored: {}", host);
                continue;
            }

            if (!accept(url)) {
                continue;
            }

            if (!urlCache.containsKey(url) && robotRules.isAllowed(url)) {

                UrlRecord urlRecord = crawlerRepository.retrieveUrlRecord(url);

                if (urlRecord == null) {
                    TimestampUrlIDX timestampUrlIDX = new TimestampUrlIDX(url);
                    crawlerRepository.insert(timestampUrlIDX);
                    urlRecord = new UrlRecord(url);
                    crawlerRepository.insert(urlRecord);
                }

                urlCache.put(url, null);

            }

        }

    }

}
