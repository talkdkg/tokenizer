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
import java.util.List;
import java.util.UUID;

import org.apache.tika.utils.CharsetUtils;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.WebpageRecord;
import org.tokenizer.crawler.db.model.TimestampUrlIDX;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.SitemapsPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
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

public class SitemapsPageFetcherTask extends AbstractTask {

    private SitemapsPageFetcherTaskConfiguration taskConfiguration;

    private static final int ROBOTS_PROTECTED_STATUS = -1;
    private static final String ROBOTS_PROTECTED_REASON = "Robots Protected";

    private final SimpleHttpFetcher httpClient;
    private final BaseHttpFetcher robotFetcher;
    BaseRobotRules robotRules = null;
    private static final int DEFAULT_MAX_THREADS = 1024;

    private static BaseUrlValidator urlValidator = new SimpleUrlValidator();
    private static BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();

    public SitemapsPageFetcherTask(final UUID uuid, final String friendlyName, final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration, final CrawlerRepository repository,
            final WritableExecutorModel fetcherModel, final HostLocker hostLocker) {
        super(uuid, friendlyName, zk, repository, fetcherModel, hostLocker);
        this.taskConfiguration = (SitemapsPageFetcherTaskConfiguration) taskConfiguration;

        UserAgent userAgent = new UserAgent(this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(), this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        this.httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024);
        robotFetcher = RobotUtils.createFetcher(userAgent, 1);
        robotFetcher.setDefaultMaxContentSize(1024 * 1024);
        LOG.debug("Instance created");
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {

        refreshRobotRules();

        List<TimestampUrlIDX> timestampUrlIDXs = crawlerRepository.loadTimestampUrlIDX(taskConfiguration.getHost());

        for (TimestampUrlIDX timestampUrlIDX : timestampUrlIDXs) {

            long start = System.currentTimeMillis();
            String url = timestampUrlIDX.getUrl();

            LOG.debug("Trying URL: {}", url);

            UrlRecord urlRecord = crawlerRepository.getUrlRecord(url);

            if (!robotRules.isAllowed(url)) {

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(System.currentTimeMillis());
                crawlerRepository.insert(timestampUrlIDX);

                if (urlRecord == null) {
                    urlRecord = new UrlRecord(url);
                    urlRecord.setFetchTime(System.currentTimeMillis());
                    urlRecord.setHttpStatus(ROBOTS_PROTECTED_STATUS);
                    urlRecord.setReasonPhrase(ROBOTS_PROTECTED_REASON);
                    crawlerRepository.insert(urlRecord);
                } else {
                    urlRecord.setFetchTime(System.currentTimeMillis());
                    urlRecord.setHttpStatus(ROBOTS_PROTECTED_STATUS);
                    urlRecord.setReasonPhrase(ROBOTS_PROTECTED_REASON);
                    crawlerRepository.update(urlRecord);
                }
                metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);
                continue;
            }

            if (urlRecord == null) {
                urlRecord = new UrlRecord(url);
                crawlerRepository.insert(urlRecord);
            }

            FetchedResult fetchedResult = null;
            try {
                fetchedResult = httpClient.get(url, null);
            } catch (RedirectFetchException e) {

                metricsCache.increment(MetricsCache.REDIRECT_COUNT);

                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(System.currentTimeMillis());
                crawlerRepository.insert(timestampUrlIDX);

                urlRecord.setHttpStatus(e.getHttpStatusCode());
                urlRecord.setFetchTime(System.currentTimeMillis());
                crawlerRepository.update(urlRecord);

                String redirectedUrl = e.getRedirectedUrl();
                redirectedUrl = urlNormalizer.normalize(redirectedUrl);
                LOG.debug("Redirected to (normalized URL): {}", redirectedUrl);

                if (!urlValidator.isValid(redirectedUrl)) {
                    LOG.debug("Redirected URL is not valid: {}", redirectedUrl);
                    continue;
                }
                
                String redirectedHost = HttpUtils.getHost(redirectedUrl);
                if (!urlRecord.getBaseHost().equals(redirectedHost)) {
                    LOG.debug("redirected extrenal host ignored: {}", redirectedHost);
                    continue;
                }

                if (crawlerRepository.loadUrlRecord(redirectedUrl) == null) {
                    TimestampUrlIDX o = new TimestampUrlIDX(redirectedUrl);
                    crawlerRepository.insert(timestampUrlIDX);
                    UrlRecord redirectedUrlRecord = new UrlRecord(redirectedUrl);
                    crawlerRepository.insert(redirectedUrlRecord);
                }
            } catch (HttpFetchException e) {
                urlRecord.setHttpStatus(e.getHttpStatus());
                urlRecord.setFetchTime(System.currentTimeMillis());
                crawlerRepository.update(urlRecord);
                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(System.currentTimeMillis());
                crawlerRepository.insert(timestampUrlIDX);
            } catch (BaseFetchException e) {
                if (e.getMessage().contains("Aborted due to INTERRUPTED"))
                    throw new InterruptedException("Aborted...");
                urlRecord.setHttpStatus(-2);
                urlRecord.setFetchTime(System.currentTimeMillis());
                crawlerRepository.update(urlRecord);
                crawlerRepository.delete(timestampUrlIDX);
                timestampUrlIDX.setTimestamp(System.currentTimeMillis());
                crawlerRepository.insert(timestampUrlIDX);
            }

            if (fetchedResult.getHttpStatus() >= 200 && fetchedResult.getHttpStatus() < 300) {
                metricsCache.increment(MetricsCache.TOTAL_HTTP_RESPONSE_TIME_MS, System.currentTimeMillis() - start);
                metricsCache.increment(MetricsCache.URL_OK_KEY);
            } else {
                metricsCache.increment(MetricsCache.URL_ERROR_KEY);
            }

            urlRecord.setContentType(fetchedResult.getContentType());
            urlRecord.setFetchAttemptCounter(urlRecord.getFetchAttemptCounter() + 1);
            urlRecord.setFetchedUrl(fetchedResult.getFetchedUrl());
            urlRecord.setFetchTime(System.currentTimeMillis());
            urlRecord.setHeaders(fetchedResult.getHeaders());
            urlRecord.setHostAddress(fetchedResult.getHostAddress());
            urlRecord.setHttpStatus(fetchedResult.getHttpStatus());
            urlRecord.setNewBaseUrl(fetchedResult.getNewBaseUrl());
            urlRecord.setNumRedirects(fetchedResult.getNumRedirects());
            urlRecord.setReasonPhrase(fetchedResult.getReasonPhrase());

            String charset = CharsetUtils.clean(HttpUtils.getCharsetFromContentType(fetchedResult.getContentType()));
            WebpageRecord webpageRecord = new WebpageRecord(urlRecord.getBaseUrl(), urlRecord.getFetchTime(), charset,
                    fetchedResult.getContent(), null);
            crawlerRepository.insertIfNotExists(webpageRecord);

            urlRecord.setWebpageDigest(webpageRecord.getDigest());
            crawlerRepository.update(urlRecord);

        }

        // fetchedResult = PersistenceUtils.fetch(urlRecord,
        // crawlerRepository, robotRules, metricsCache, httpClient,
        // taskConfiguration.getHost(), urlFilter);
        // LOG.trace("Fetching Result: {} {}", urlRecord, fetchedResult);

        if (timestampUrlIDXs == null || timestampUrlIDXs.size() == 0) {
            // to prevent spin loop in case if collection is empty:
            LOG.warn("no URLs found; sleeping 1 hour...");
            Thread.sleep(1 * 3600 * 1000L);
        }

    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (SitemapsPageFetcherTaskConfiguration) taskConfiguration;
    }

    /**
     * Should be called few times a day
     * 
     * @return
     */
    private synchronized boolean refreshRobotRules() {
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

}
