package org.tokenizer.executor.engine;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.weblog.FetchedResultRecord;
import org.tokenizer.crawler.db.weblog.HostRecord;
import org.tokenizer.crawler.db.weblog.WeblogRecord;
import org.tokenizer.crawler.db.weblog.WeblogRecord.Weblog;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsCrawlerTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.robots.RobotUtils;

public class WeblogsCrawlerTask extends AbstractTask {

     private WeblogsCrawlerTaskConfiguration taskConfiguration;
    private static final int DEFAULT_MAX_THREADS = 1024;
    private final SimpleHttpFetcher httpClient;

    public WeblogsCrawlerTask(UUID uuid, String friendlyName, ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            CrawlerRepository crawlerRepository, WritableExecutorModel model,
            HostLocker hostLocker) {
        super(uuid, friendlyName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (WeblogsCrawlerTaskConfiguration) taskConfiguration;
        UserAgent userAgent = new UserAgent(
                this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(),
                this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(1024 * 1024);

    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (WeblogsCrawlerTaskConfiguration) taskConfiguration;
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        WeblogRecord weblogRecord = crawlerRepository.getLastWeblogRecord();
        LOG.debug("Last Counter: {}", weblogRecord.getCount());

        for (Weblog weblog : weblogRecord.getWeblogs()) {
            String url = weblog.getUrl();
            FetchedResult fetchedResult = null;
            // long start = System.currentTimeMillis();
            try {
                LOG.debug("trying url: {}", url);
                fetchedResult = httpClient.get(url, null);
            } catch (RedirectFetchException e) {
                String redirectedUrl = e.getRedirectedUrl();
                try {
                    LOG.debug("trying redirected url: {}", redirectedUrl);
                    fetchedResult = httpClient.get(redirectedUrl, null);
                } catch (RedirectFetchException e1) {
                    String redirectedUrl2 = e.getRedirectedUrl();
                    LOG.error("secondary redirect: {}",redirectedUrl2 );
                } catch (HttpFetchException e1) {
                    LOG.error(e1.getMessage());
                } catch (BaseFetchException e1) {
                    if (e1.getMessage().contains("Aborted due to INTERRUPTED"))
                        throw new InterruptedException("Aborted...");
                    LOG.error(e1.getMessage());
                }
                metricsCache.increment(MetricsCache.REDIRECT_COUNT);
            } catch (HttpFetchException e) {
                LOG.error(e.getMessage());
            } catch (BaseFetchException e) {
                if (e.getMessage().contains("Aborted due to INTERRUPTED"))
                    throw new InterruptedException("Aborted...");
                metricsCache.increment(MetricsCache.OTHER_ERRORS);
                LOG.error(e.getMessage());
            }
            if (fetchedResult != null) {
                FetchedResultRecord fetchedResultRecord = new FetchedResultRecord(
                        url, new Date(), fetchedResult);
                crawlerRepository.insert(fetchedResultRecord);
               
            }

        }
    }

}
