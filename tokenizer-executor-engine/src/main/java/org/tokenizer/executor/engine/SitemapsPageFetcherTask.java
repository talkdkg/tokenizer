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
import org.tokenizer.executor.model.configuration.SitemapsPageFetcherTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import crawlercommons.fetcher.FetchedResult;

public class SitemapsPageFetcherTask extends AbstractFetcherTask<SitemapsPageFetcherTaskConfiguration> {

    public SitemapsPageFetcherTask(final UUID uuid, final String friendlyName, final ZooKeeperItf zk,
            final SitemapsPageFetcherTaskConfiguration taskConfiguration, final CrawlerRepository repository,
            final WritableExecutorModel fetcherModel, final HostLocker hostLocker) {
        super(uuid, friendlyName, zk, taskConfiguration, repository, fetcherModel, hostLocker);
    }

    @Override
    protected boolean accept(String url) {
        return true;
    }

    @Override
    protected void processFetchedResult(final FetchedResult fetchedResult) {
        LOG.debug("processFetchedResult called for SitemapsPageFetcherTask...");
        return;
    }

}
