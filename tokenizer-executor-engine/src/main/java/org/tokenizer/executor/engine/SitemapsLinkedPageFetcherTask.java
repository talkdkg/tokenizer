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

import java.util.UUID;

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.SitemapsLinkedPageFetcherTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.RobotUtils;

public class SitemapsLinkedPageFetcherTask extends AbstractTask<SitemapsLinkedPageFetcherTaskConfiguration> {

    private boolean stop;

    // Delay 4 hours between subsequent refresh of sitemaps
    private static final long DELAY = 4 * 3600 * 1000L;

    private final BaseHttpFetcher fetcher;
    private BaseRobotRules robotRules = null;

    public SitemapsLinkedPageFetcherTask(final UUID uuid, final String friendlyName,
            final ZooKeeperItf zk,
        final SitemapsLinkedPageFetcherTaskConfiguration taskConfiguration,
            final CrawlerRepository repository,
            final WritableExecutorModel fetcherModel,
            final HostLocker hostLocker) {
        super(uuid, friendlyName, zk, taskConfiguration, repository, fetcherModel, hostLocker);

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

}
