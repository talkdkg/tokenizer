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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class ClassicRobotTask extends AbstractTask {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClassicRobotTask.class);
    private final SimpleHttpFetcher httpClient;
    private final BaseHttpFetcher robotFetcher;
    BaseRobotRules robotRules = null;
    private ClassicRobotTaskConfiguration taskConfiguration;
    private static final int DEFAULT_MAX_THREADS = 1024;

    public ClassicRobotTask(final String taskName, final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (ClassicRobotTaskConfiguration) taskConfiguration;
        this.httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS,
                FetcherUtils.USER_AGENT);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024);
        robotFetcher = RobotUtils.createFetcher(FetcherUtils.USER_AGENT, 1);
        robotFetcher.setDefaultMaxContentSize(1024 * 1024);
        LOG.debug("Instance created");
    }

    /**
     * Real job is done here
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    protected void process() throws InterruptedException, ConnectionException {
        refreshRobotRules();
        UrlRecord home = new UrlRecord("http://" + taskConfiguration.getHost()
                + "/");
        crawlerRepository.insertIfNotExists(home);
        int httpResponseCode = 0;
        int maxResults = 10000;
        List<UrlRecord> urlRecords = crawlerRepository.listUrlRecords(
                taskConfiguration.getHost(), httpResponseCode, maxResults);
        for (UrlRecord urlRecord : urlRecords) {
            LOG.debug("Trying URL: {}", urlRecord);
            FetchedResult fetchedResult = PersistenceUtils.fetch(urlRecord,
                    crawlerRepository, robotRules, metricsCache, httpClient,
                    taskConfiguration.getHost());
            LOG.trace("Fetching Result: {} {}", urlRecord, fetchedResult);
        }
        if (urlRecords == null || urlRecords.size() == 0) {
            // to prevent spin loop in case if collection is empty:
            LOG.warn("no URLs found with httpResponseCode == 0; sleeping 600 seconds...");
            Thread.sleep(600000);
        }
    }

    /**
     * Should be called few times a day
     * 
     * @return
     */
    private synchronized boolean refreshRobotRules() {
        BaseRobotsParser parser = new SimpleRobotRulesParser();
        try {
            robotRules = RobotUtils.getRobotRules(robotFetcher, parser,
                    new URL("http://" + taskConfiguration.getHost()
                            + "/robots.txt"));
        } catch (MalformedURLException e) {
            LOG.error("", e);
            return false;
        }
        return true;
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (ClassicRobotTaskConfiguration) taskConfiguration;
    }
}
