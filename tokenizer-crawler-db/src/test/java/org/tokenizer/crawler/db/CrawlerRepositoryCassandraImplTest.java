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
        repo.setSeeds("s001:9160");
        repo.setup();
        repository = repo;
        System.out.println(repository.countUrlRecords(0));
        System.out.println(repository.countUrlRecords2());
        System.out.println(repository.countWebpageRecords("www.amazon.com", 1));
        // repository.listWebpageRecords(host, splitAttemptCounter,
        // defaultPageSize);
        // List<byte[]> rowKeys = repository.loadUrlRecordRowKeys();
        // System.out.println(rowKeys.size());
        // byte[][] bytes = new byte[rowKeys.size()][];
        // int i = 0;
        // for (byte[] rowKey : rowKeys) {
        // bytes[i] = rowKey;
        // i++;
        // }
        // byte[] b = new byte[16 * rowKeys.size()];
        // i = 0;
        // for (byte[] rowKey : rowKeys) {
        // if (rowKey.length > 16)
        // throw new RuntimeException("too high");
        // for (int offset = 0; offset < 16; offset++) {
        // b[i * 16 + offset] = rowKey[offset];
        // }
        // i++;
        // }
        // System.out.println("byte[][]: " + MemoryMeasurer.measureBytes(bytes)
        // + " bytes");
        // System.out.println("List<byte[]>: "
        // + MemoryMeasurer.measureBytes(rowKeys) + " bytes");
        // System.out.println("byte[][]: " + MemoryMeasurer.measureBytes(bytes)
        // + " bytes");
        // System.out.println("b[]: " + MemoryMeasurer.measureBytes(b) +
        // " bytes");
        // System.out.println("byte[8]: "
        // + MemoryMeasurer.measureBytes(new byte[8]) + " bytes");
        // System.out.println("byte[1][8]>: "
        // + MemoryMeasurer.measureBytes(new byte[1][8]) + " bytes");
    }
}
