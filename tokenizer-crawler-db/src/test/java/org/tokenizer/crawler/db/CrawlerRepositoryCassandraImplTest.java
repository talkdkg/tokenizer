package org.tokenizer.crawler.db;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class CrawlerRepositoryCassandraImplTest {

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
    public void genericTest() {
        try {
            UrlRecords urlRecords = repository.listUrlRecords(200, 100);
            int i = 0;
            for (UrlRecord urlRecord : urlRecords) {
                System.out.println(++i);
                System.out.println(urlRecord);
            }
            System.out.println(urlRecords.size());
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @Test
    public void webpageListTest() {
        try {
            WebpageRecords webpageRecords = repository.listWebpageRecords(
                    "www.tokenizer.ca", 0, 10);
            int i = 0;
            for (WebpageRecord webpageRecord : webpageRecords) {
                System.out.println(++i);
                System.out.println(webpageRecord);
            }
            System.out.println(webpageRecords.size());
            byte[] digest = { 5, 62, 10, 12, 23, 66, -77, -118, -1, 92, -47,
                    -20, 18, 23, -123, 114 };
            WebpageRecord webpageRecord = repository.getWebpageRecord(digest);
            System.out.println("webpageRecord:\n" + webpageRecord);
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
        String host = "www.amazon.com";
        int splitAttemptCounter = 0;
        int defaultPageSize = 100;
        CrawlerRepositoryCassandraImpl repo = new CrawlerRepositoryCassandraImpl();
        repo.setSeeds("108.175.12.244:9160");
        repo.setup();
        repository = repo;
        System.out.println(repository.countUrlRecords(0));
        System.out.println(repository.countUrlRecords2());
        System.out.println(repository.countWebpageRecords("www.amazon.com", 1));
        repository.listWebpageRecords(host, splitAttemptCounter,
                defaultPageSize);
    }
}
