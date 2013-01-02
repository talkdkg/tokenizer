package org.tokenizer.crawler.db;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class CrawlerRepositoryCassandraImplTest {

    private static Logger LOG = LoggerFactory
            .getLogger(CrawlerRepositoryCassandraImplTest.class);
    private static CrawlerRepositoryCassandraImpl repository;

    @BeforeClass
    public static void setup() throws Exception {
        CrawlerRepositoryCassandraImpl repo = new CrawlerRepositoryCassandraImpl();
        repo.setup();
        repository = repo;
    }

    @AfterClass
    public static void teardown() {
    }

    // @Test
    public void listWebpageRecordsTest() throws Exception {
        try {
            List<WebpageRecord> webpageRecords = repository.listWebpageRecords(
                    "www.amazon.com", 0, 10);
            for (WebpageRecord webpageRecord : webpageRecords) {
                System.out.println(webpageRecord);
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    // @Test
    public void listXmlRecordsTest() throws Exception {
        List<XmlRecord> xmlRecords = repository.listXmlRecords(
                "www.amazon.com", 0, 100);
        for (XmlRecord xmlRecord : xmlRecords) {
            System.out.println(xmlRecord);
        }
    }

    // @Test
    public void genericTest() throws Exception {
        try {
            List<UrlRecord> urlRecords = repository.listUrlRecords(
                    "www.amazon.com", 0, 100);
            urlRecords = repository.listUrlRecords("www.cnet.com", 0, 100);
            urlRecords = repository.listUrlRecords("reviews.cnet.com", 0, 100);
            urlRecords = repository.listUrlRecords("www.expertreviews.co.uk",
                    0, 100);
            int i = 0;
            // for (UrlRecord urlRecord : urlRecords) {
            // System.out.println(++i);
            // System.out.println(urlRecord);
            // }
            System.out.println(urlRecords.size());
            // LOG.debug("total records: {}", repository.countUrlRecords2());
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @Test
    public void webpageCounterTest() {
        try {
            repository.countWebpageRecords("www.amazon.com", 0);
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws Exception {
        setup();
        repository.reindex();
        List<WebpageRecord> webpageRecords = repository.listWebpageRecords(
                "www.amazon.com", 0, 1000);
        LOG.debug("{} results found", webpageRecords.size());
        for (WebpageRecord webpageRecord : webpageRecords) {
            // System.out.println(webpageRecord);
        }
    }
}
