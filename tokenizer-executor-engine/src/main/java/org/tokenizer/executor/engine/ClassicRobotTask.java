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

import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetcherUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlRecords;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

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

    public ClassicRobotTask(final String taskName, final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            final CrawlerRepository crawlerRepository,
            final WritableExecutorModel model, final HostLocker hostLocker) {
        super(taskName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (ClassicRobotTaskConfiguration) taskConfiguration;
        this.httpClient = new SimpleHttpFetcher(FetcherUtils.USER_AGENT);
        this.httpClient.setRedirectMode(RedirectMode.FOLLOW_ALL);
        this.robotFetcher = RobotUtils
                .createFetcher(FetcherUtils.USER_AGENT, 1);
        this.robotFetcher.setDefaultMaxContentSize(1024 * 1024);
        this.httpClient.setDefaultMaxContentSize(1024 * 1024);
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
        UrlRecords urlRecords = crawlerRepository.listUrlRecords(
                taskConfiguration.getHost(), 0, 100);
        for (UrlRecord urlRecord : urlRecords) {
            LOG.debug("Trying URL: {}", urlRecord);
            FetchedResult fetchedResult = PersistenceUtils.fetch(urlRecord,
                    crawlerRepository, robotRules, metricsCache, httpClient,
                    taskConfiguration.getHost());
            LOG.debug("Result: {} {}", urlRecord.getUrl(),
                    urlRecord.getHttpResponseCode());
        }
        // to prevent spin loop in case if collection is empty:
        LOG.warn("sleeping 10 seconds...");
        Thread.currentThread().sleep(10000);
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
