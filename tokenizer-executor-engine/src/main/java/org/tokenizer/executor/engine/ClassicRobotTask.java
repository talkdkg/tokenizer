/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.UUID;

import org.apache.nutch.net.URLFilter;
import org.apache.nutch.urlfilter.automaton.AutomatonURLFilter;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

public class ClassicRobotTask extends AbstractFetcherTask<ClassicRobotTaskConfiguration> {

    private URLFilter urlFilter;

    public ClassicRobotTask(UUID uuid, String friendlyName, ZooKeeperItf zk,
        ClassicRobotTaskConfiguration taskConfiguration, CrawlerRepository crawlerRepository,
        WritableExecutorModel fetcherModel, HostLocker hostLocker) {
        super(uuid, friendlyName, zk, taskConfiguration, crawlerRepository, fetcherModel, hostLocker);

        if (getTaskConfiguration().getUrlFilterConfig() == null) {
            this.taskConfiguration.setUrlFilterConfig(ClassicRobotTaskConfiguration.DEFAULT_URL_FILTER);
        }
        Reader reader = new StringReader(this.taskConfiguration.getUrlFilterConfig());
        try {
            this.urlFilter = new AutomatonURLFilter(reader);
        }
        catch (IllegalArgumentException e) {
            LOG.error("", e);
            throw (e);
        }
        catch (IOException e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        }

    }


    @Override
    protected boolean accept(String url) {
        if (urlFilter != null) {
            return urlFilter.accept(url);
        }
        else {
            return false;
        }
    }

}
