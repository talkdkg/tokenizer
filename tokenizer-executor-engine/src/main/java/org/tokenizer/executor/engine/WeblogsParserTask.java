package org.tokenizer.executor.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.nutch.net.URLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.datum.Outlink;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.parser.SimpleParser;
import org.tokenizer.core.urls.BaseUrlNormalizer;
import org.tokenizer.core.urls.BaseUrlValidator;
import org.tokenizer.core.urls.SimpleUrlNormalizer;
import org.tokenizer.core.urls.SimpleUrlValidator;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.ParserPolicy;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.weblog.FetchedResultRecord;
import org.tokenizer.crawler.db.weblog.HostRecord;
import org.tokenizer.crawler.db.weblog.UrlHeadRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsCrawlerTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsParserTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.Payload;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import org.apache.http.client.methods.*;

public class WeblogsParserTask extends AbstractTask {

    private WeblogsParserTaskConfiguration taskConfiguration;
    private static final int DEFAULT_MAX_THREADS = 1024;
    private final SimpleHttpFetcher2 httpClient;
    private final ParserPolicy parserPolicy = new ParserPolicy();
    private final SimpleParser parser = new SimpleParser(parserPolicy);
    private static BaseUrlValidator urlValidator = new SimpleUrlValidator();
    private static BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();

    public WeblogsParserTask(UUID uuid, String friendlyName, ZooKeeperItf zk,
            final TaskConfiguration taskConfiguration,
            CrawlerRepository crawlerRepository, WritableExecutorModel model,
            HostLocker hostLocker) {
        super(uuid, friendlyName, zk, crawlerRepository, model, hostLocker);
        this.taskConfiguration = (WeblogsParserTaskConfiguration) taskConfiguration;
        UserAgent userAgent = new UserAgent(
                this.taskConfiguration.getAgentName(),
                this.taskConfiguration.getEmailAddress(),
                this.taskConfiguration.getWebAddress(),
                UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
        LOG.warn("userAgent: {}", userAgent.getUserAgentString());
        httpClient = new SimpleHttpFetcher2(DEFAULT_MAX_THREADS, userAgent);
        httpClient.setSocketTimeout(30000);
        httpClient.setConnectionTimeout(30000);
        httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
        httpClient.setDefaultMaxContentSize(0);

    }

    @Override
    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    @Override
    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = (WeblogsParserTaskConfiguration) taskConfiguration;
    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {
        LOG.info("processing...");

        List<HostRecord> hostRecords = crawlerRepository.listHostRecords("com",
                0, 1000);

        for (HostRecord hostRecord : hostRecords) {
            LOG.debug(hostRecord.toString());
            List<FetchedResultRecord> fetchedResultRecords = crawlerRepository
                    .listFetchedResultRecords(hostRecord.getHost());
            for (FetchedResultRecord fetchedResultRecord : fetchedResultRecords) {
                LOG.debug(fetchedResultRecord.toString());
                List<Outlink> outlinks = parse(fetchedResultRecord
                        .getFetchedResult());

                int i = 0;
                for (Outlink outlink : outlinks) {
                    i++;
                    String url = outlink.getToUrl();
                    HttpRequestBase request = new HttpHead();
                    try {
                        FetchedResult fetchedResult = httpClient.fetch(request,
                                url, new Payload());
                        LOG.debug(i + " url: {}; content type: {}", url,
                                fetchedResult.getContentType());
                        
                        UrlHeadRecord urlHeadRecord = new UrlHeadRecord(fetchedResult);
                        crawlerRepository.insertIfNotExists(urlHeadRecord);
                        
                        
                    } catch (RedirectFetchException e) {
                        String redirectedUrl2 = e.getRedirectedUrl();
                        LOG.error("secondary redirect: {}", redirectedUrl2);
                    } catch (BaseFetchException e) {
                        if (e.getMessage().contains(
                                "Aborted due to INTERRUPTED"))
                            throw new InterruptedException("Aborted...");
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    private List<Outlink> parse(final FetchedResult fetchedResult)
            throws InterruptedException, ConnectionException {
        ParsedDatum parsed = parser.parse(fetchedResult);
        if (parsed == null)
            return null;
        Outlink[] outlinks = parsed.getOutlinks();

        List<Outlink> list = new ArrayList<Outlink>();
        for (Outlink outlink : outlinks) {
            LOG.debug("outlink: {}", outlink);
            String url = outlink.getToUrl();
            if (!urlValidator.isValid(url)) {
                continue;
            }
            url = urlNormalizer.normalize(url);
            String host = HttpUtils.getHost(url);
            if (HttpUtils.EMPTY_STRING.equals(host)) {
                continue;
            }

            Outlink o = new Outlink(url, outlink.getAnchor(),
                    outlink.getRelAttributes());
            list.add(o);
        }
        return list;
    }

}
