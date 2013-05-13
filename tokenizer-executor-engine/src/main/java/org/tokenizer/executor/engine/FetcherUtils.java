package org.tokenizer.executor.engine;

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.robots.BaseRobotRules;

public class FetcherUtils {

    
    
    
    public static boolean checkRobotRules(final UrlRecord urlRecord,
            final CrawlerRepository repository,
            final BaseRobotRules baseRobotRules, final MetricsCache metricsCache)
            throws InterruptedException, ConnectionException {
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
