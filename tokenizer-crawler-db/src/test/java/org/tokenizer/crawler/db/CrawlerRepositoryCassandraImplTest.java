package org.tokenizer.crawler.db;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class CrawlerRepositoryCassandraImplTest {

    private static CrawlerRepository repository;

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
                    "www.amazon.com", 0, 10);
            int i = 0;
            for (WebpageRecord webpageRecord : webpageRecords) {
                System.out.println(++i);
                System.out.println(webpageRecord);
            }
            System.out.println(webpageRecords.size());
            byte[] digest = { 100, -81, 77, -68, -44, 69, -48, -101, -25, 92,
                    120, -37, -96, -68, -119, -78 };
            WebpageRecord webpageRecord = repository.getWebpageRecord(digest);
            System.out.println(webpageRecord);
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
}
