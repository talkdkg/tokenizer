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

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.Logs;
import org.lilyproject.util.zookeeper.LeaderElection;
import org.lilyproject.util.zookeeper.LeaderElectionCallback;
import org.lilyproject.util.zookeeper.LeaderElectionSetupException;
import org.lilyproject.util.zookeeper.ZooKeeperItf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.FetcherUtils;
//import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlScanner;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

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

    private LeaderElection leaderElection;

    private final SimpleHttpFetcher httpClient;

    // special instance for robots.txt only:
    private final BaseHttpFetcher robotFetcher;
    BaseRobotRules robotRules = null;

    public ClassicRobotTask(String taskName, ZooKeeperItf zk,
            TaskConfiguration taskConfiguration,
            CrawlerHBaseRepository crawlerRepository,
            WritableExecutorModel model, HostLocker hostLocker) {
        super(taskName, zk, taskConfiguration, crawlerRepository, model,
                hostLocker);

        this.httpClient = new SimpleHttpFetcher(FetcherUtils.USER_AGENT);
        this.httpClient.setRedirectMode(RedirectMode.FOLLOW_ALL);
        this.robotFetcher = RobotUtils
                .createFetcher(FetcherUtils.USER_AGENT, 1);
        this.robotFetcher.setDefaultMaxContentSize(4 * 1024 * 1024);

        
        this.leaderElectionCallback = new MyLeaderElectionCallback(); 
        
        
        LOG.debug("Instance created");

    }


    @Override
    public void run() {
        while (!Thread.interrupted()) {
            refreshRobotRules();
            String url = "http://" + taskConfiguration.getTld();
            UrlRecord urlRecord = new UrlRecord();
            urlRecord.setUrl(url);

            // create if it doesn't exist
            try {
                crawlerRepository.create(urlRecord);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                process();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private class MyLeaderElectionCallback implements LeaderElectionCallback {

        public void activateAsLeader() throws Exception {
            LOG.warn("activateAsLeader...");
            if (thread != null && thread.isAlive()) {
                LOG.warn("Start was requested, but old thread was still there. Stopping it now.");
                thread.interrupt();
                Logs.logThreadJoin(thread);
                thread.join();
            } else {
                thread = new Thread(ClassicRobotTask.this,
                        ClassicRobotTask.this.taskName);
                thread.setDaemon(true);
                thread.start();
                LOG.warn("Activated as Leader.");
            }
        }

        public void deactivateAsLeader() throws Exception {
            LOG.warn("deactivateAsLeader...");
            shutdown();
            LOG.warn("Deactivated as Leader.");
        }

    }


    /**
     * Real job is done here
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    protected void process() throws InterruptedException, IOException {

        UrlScanner urlScanner = new UrlScanner(taskConfiguration.getTld(),
                crawlerRepository);

        for (UrlRecord urlRecord : urlScanner) {

            LOG.debug("urlRecord: {}", urlRecord);

            if (urlRecord.getTimestamp() > 0)
                continue;

            LOG.debug("Trying URL: {}", urlRecord.getUrl());
            FetchedResult fetchedResult = PersistenceUtils.fetch(urlRecord,
                    crawlerRepository, robotRules, metricsCache, httpClient,
                    taskConfiguration.getTld());
            LOG.debug("Result: {} {}", urlRecord.getUrl(),
                    urlRecord.getHttpResponseCode());
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
                    new URL("http://" + taskConfiguration.getTld()
                            + "/robots.txt"));
        } catch (MalformedURLException e) {
            LOG.error("", e);
            return false;
        }
        return true;
    }

}
