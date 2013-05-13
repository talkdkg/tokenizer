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
import java.util.UUID;

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.UrlSitemapIDX;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.SitemapsFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsLinkedPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.UnknownFormatException;

public class SitemapsLinkedPageFetcherTask extends AbstractTask {
    private boolean stop;
    // Delay 4 hours between subsequent refresh of sitemaps
    private static final long DELAY = 4 * 3600 * 1000L;
    private SitemapsLinkedPageFetcherTaskConfiguration taskConfiguration;

    private final BaseHttpFetcher fetcher;
    private BaseRobotRules robotRules = null;

    public SitemapsLinkedPageFetcherTask(final UUID uuid, final String friendlyName,
            final ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            final CrawlerRepository repository,
            final WritableExecutorModel fetcherModel,
            final HostLocker hostLocker) {
        super(uuid, friendlyName, zk, repository, fetcherModel, hostLocker);
        this.taskConfiguration = (SitemapsLinkedPageFetcherTaskConfiguration) taskConfiguration;

        UserAgent userAgent = new UserAgent(
                this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(),
                this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");

        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        fetcher = RobotUtils.createFetcher(userAgent, 1);
        fetcher.setDefaultMaxContentSize(64 * 1024 * 1024);
        LOG.debug("Instance created");
    }

    int i=0; 
    
    @Override
    protected void process() throws InterruptedException, ConnectionException {
            LOG.info("[{}] Processing...", i++);
    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return this.taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (SitemapsLinkedPageFetcherTaskConfiguration) taskConfiguration;
    }
}
