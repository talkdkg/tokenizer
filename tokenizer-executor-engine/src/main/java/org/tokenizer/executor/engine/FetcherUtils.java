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

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.UrlRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.robots.BaseRobotRules;

public class FetcherUtils {

    public static boolean checkRobotRules(final UrlRecord urlRecord, final CrawlerRepository repository,
            final BaseRobotRules baseRobotRules, final MetricsCache metricsCache) throws InterruptedException,
            ConnectionException {
        String url = urlRecord.getBaseUrl();
        if (!baseRobotRules.isAllowed(url)) {
            urlRecord.setHttpStatus(-1);
            urlRecord.setFetchTime(System.currentTimeMillis());
            repository.update(urlRecord);
            metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);
            return false;
        }
        return true;
    }

}
