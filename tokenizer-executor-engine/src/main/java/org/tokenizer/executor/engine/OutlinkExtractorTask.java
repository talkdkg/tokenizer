package org.tokenizer.executor.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.nutch.net.URLFilter;
import org.apache.nutch.urlfilter.automaton.AutomatonURLFilter;
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
import org.tokenizer.crawler.db.model.TimestampUrlIDX;
import org.tokenizer.crawler.db.model.UrlRecord;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.OutlinkExtractorTaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class OutlinkExtractorTask extends AbstractTask<OutlinkExtractorTaskConfiguration> {

    private static final int MAX_ENTRIES = 10000;
    private Map<String, String> urlCache = new LinkedHashMap<String, String>(MAX_ENTRIES + 1, .75F, true) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean removeEldestEntry(@SuppressWarnings("rawtypes")
        final Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    private static final int MAX_PARSE_DURATION = 180 * 1000;

    private static BaseUrlValidator urlValidator = new SimpleUrlValidator();
    private static BaseUrlNormalizer urlNormalizer = new SimpleUrlNormalizer();


    private URLFilter urlFilter;

    
    // @formatter:off
    public OutlinkExtractorTask(
            UUID uuid, 
            String friendlyName, 
            ZooKeeperItf zk,
            OutlinkExtractorTaskConfiguration taskConfiguration, 
            CrawlerRepository crawlerRepository,
            WritableExecutorModel model, 
            HostLocker hostLocker) {
        super(
                uuid, 
                friendlyName, 
                zk, 
                taskConfiguration, 
                crawlerRepository, 
                model, 
                hostLocker);
        //@formatter:on

        Reader reader = new StringReader(this.taskConfiguration.getUrlFilterConfig());
        try {
            this.urlFilter = new AutomatonURLFilter(reader);
        } catch (IllegalArgumentException e) {
            LOG.error("", e);
            throw (e);
        } catch (IOException e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void process() throws InterruptedException, ConnectionException {

        /*
        Somehow "maxROws=100" causes following:
            Caused by: org.apache.thrift.transport.TTransportException: Frame size (23093808) larger than max length (16384000)!

        See also settings in *.yaml file:
            # Frame size for thrift (maximum field length).
            thrift_framed_transport_size_in_mb: 15

            # The max length of a thrift message, including all fields and
            # internal thrift overhead.
            thrift_max_message_length_in_mb: 16

        */
        
        List<WebpageRecord> webpageRecords = crawlerRepository.listWebpageRecordsByExtractOutlinksAttemptCounter(
                taskConfiguration.getHost(), taskConfiguration.getExtractOutlinksAttemptCounter(), 10);

        for (WebpageRecord webpageRecord : webpageRecords) {
            parse(webpageRecord);
        }

    }

    private boolean accept(final String url) {
        if (urlFilter != null) {
            return urlFilter.accept(url);
        }
        else {
            return false;
        }
    }

    protected void parse(final WebpageRecord webpageRecord) throws InterruptedException, ConnectionException {

        ParserPolicy parserPolicy = new ParserPolicy(MAX_PARSE_DURATION);
        SimpleParser parser = new SimpleParser(parserPolicy);

        ParsedDatum parsedDatum = parser.parse(webpageRecord.toFetchedResult());

        webpageRecord.incrementExtractOutlinksAttemptCounter();
        
        if (parsedDatum == null) {
            return;
        }

        Outlink[] outlinks = parsedDatum.getOutlinks();

        for (Outlink outlink : outlinks) {

            //LOG.debug("outlink: {}", outlink);

            String url = outlink.getToUrl();

            if (!urlValidator.isValid(url)) {
                continue;
            }

            url = urlNormalizer.normalize(url);

            String host = HttpUtils.getHost(url);
            if (HttpUtils.EMPTY_STRING.equals(host)) {
                continue;
            }

            // This is definition of "domain constrained crawl" (vertical crawl):
             if (!webpageRecord.getHost().equals(host)) {
                LOG.trace("extrenal host ignored; initial: {} external: {}", webpageRecord.getHost(), host);
                continue;
            }

            if (!accept(url)) {
                continue;
            }

            if (!urlCache.containsKey(url)) {

                UrlRecord urlRecord = crawlerRepository.retrieveUrlRecord(url);

                if (urlRecord == null) {
                    TimestampUrlIDX timestampUrlIDX = new TimestampUrlIDX(url);
                    crawlerRepository.insert(timestampUrlIDX);
                    urlRecord = new UrlRecord(url);
                    crawlerRepository.insert(urlRecord);
                }

                urlCache.put(url, null);

                metricsCache.increment(MetricsCache.URL_INJECTED);

                LOG.debug("URL extracted: {}", url);
                
            }

        }

    }

}
