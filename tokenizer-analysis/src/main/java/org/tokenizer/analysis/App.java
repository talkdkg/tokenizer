package org.tokenizer.analysis;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.util.Bytes;
import org.lilyproject.util.hbase.HBaseTableFactory;
import org.lilyproject.util.hbase.HBaseTableFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.CrawlerHBaseSchema;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlRecordDecoder;
import org.tokenizer.crawler.db.CrawlerHBaseSchema.Table;
import org.tokenizer.crawler.db.UrlScanner;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    HBaseTableFactory tableFactory;
    protected HTableInterface urlTable;
    protected HTableInterface webpageTable;
    public static byte[] POSTFIX = new byte[] { 0 };
    public static final byte[] ZERO_LONG = Bytes.toBytes(0L);
    public static final byte[] ZERO_INT = Bytes.toBytes(0);

    // CrawlerHBaseRepository repo = new CrawlerHBaseRepository();
    public static void main(String[] args) throws Throwable {
        printUrl();
    }

    public static long countUrl() {
        LOG.info("Counting URLs...");
        Configuration customConf = new Configuration();
        customConf.setStrings("hbase.zookeeper.quorum", "localhost");
        customConf.setLong("hbase.rpc.timeout", 600000);
        customConf.setLong("hbase.client.scanner.caching", 1000);
        Configuration configuration = HBaseConfiguration.create(customConf);
        AggregationClient aggregationClient = new AggregationClient(
                configuration);
        Scan scan = new Scan();
        scan.addFamily(CrawlerHBaseSchema.UrlCf.DATA.bytes);
        long rowCount = 0L;
        try {
            rowCount = aggregationClient.rowCount(Table.URL.bytes, null, scan);
        } catch (Throwable e) {
            LOG.error("", e);
        }
        return rowCount;
    }

    public static void printUrl() throws IOException, InterruptedException {
        LOG.info("Printing URLs...");
        Configuration customConf = new Configuration();
        customConf.setStrings("hbase.zookeeper.quorum", "localhost");
        customConf.setLong("hbase.rpc.timeout", 600000);
        customConf.setLong("hbase.client.scanner.caching", 1000);
        Scan scan = new Scan();
        scan.addFamily(CrawlerHBaseSchema.UrlCf.DATA.bytes);
        HBaseTableFactory tableFactory = new HBaseTableFactoryImpl(customConf);
        HTableInterface urlTable = CrawlerHBaseSchema.getUrlTable(tableFactory);
        byte[] startRow = Bytes.add(
                UrlRecordDecoder.encode("http://www.amazon.com"), POSTFIX);
        scan.setStartRow(startRow);
        ResultScanner rs = null;
        try {
            rs = urlTable.getScanner(scan);
            for (Result r = rs.next(); r != null; r = rs.next()) {
                
                UrlRecord urlRecord = CrawlerHBaseRepository.decodeUrlRecord(r);
                
                if (urlRecord.getUrl().contains("/product-reviews/") && 
                        urlRecord.getHttpResponseCode() == 200)
                    System.out.println(urlRecord);
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (rs != null)
                rs.close();
        }
    }
}
