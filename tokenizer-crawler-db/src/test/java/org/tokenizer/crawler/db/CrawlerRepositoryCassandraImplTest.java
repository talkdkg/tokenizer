package org.tokenizer.crawler.db;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
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
}
