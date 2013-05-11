package org.tokenizer.crawler.db;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.nutch.net.URLFilter;
import crawlercommons.fetcher.Payload;
import org.apache.nutch.urlfilter.automaton.AutomatonURLFilter;
import org.apache.tika.metadata.Metadata;
import org.junit.*;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.model.FetchedResultRecord;
import org.tokenizer.crawler.db.model.WeblogRecord;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.OperationResultImpl;
import crawlercommons.fetcher.FetchedResult;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.shallows.EmptyColumnList;
import com.netflix.astyanax.test.TestKeyspace;
import com.netflix.astyanax.thrift.model.ThriftRowImpl;

/**
 * The class <code>CrawlerRepositoryCassandraImplTest</code> contains tests for the class <code>{@link CrawlerRepositoryCassandraImpl}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
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

        new org.junit.runner.JUnitCore().run(CrawlerRepositoryCassandraImplTest.class);

    }

    
    /**
     * Run the CrawlerRepositoryCassandraImpl() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testCrawlerRepositoryCassandraImpl_1()
        throws Exception {

        CrawlerRepositoryCassandraImpl result = new CrawlerRepositoryCassandraImpl();

        // add additional test code here
        assertNotNull(result);
    }

    /**
     * Run the CrawlerRepositoryCassandraImpl(int) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testCrawlerRepositoryCassandraImpl_2()
        throws Exception {
        int port = 1;

        CrawlerRepositoryCassandraImpl result = new CrawlerRepositoryCassandraImpl(port);

        // add additional test code here
        assertNotNull(result);
    }


    /**
     * Run the void filter(String,URLFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testFilter_1()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");
        String host = "";
        URLFilter urlFilter = new AutomatonURLFilter();

        fixture.filter(host, urlFilter);

        // add additional test code here
    }

    /**
     * Run the void filter(String,URLFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testFilter_2()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");
        String host = "";
        URLFilter urlFilter = new AutomatonURLFilter();

        fixture.filter(host, urlFilter);

        // add additional test code here
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_1()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_2()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_3()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_4()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_5()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_6()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_7()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_8()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_9()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_10()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_11()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_12()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }

    /**
     * Run the void getKeyspaceDefinition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetKeyspaceDefinition_13()
        throws Exception {
        CrawlerRepositoryCassandraImpl fixture = new CrawlerRepositoryCassandraImpl(1);
        fixture.setSeeds("");
        fixture.keyspace = new TestKeyspace("");

        fixture.getKeyspaceDefinition();

        // add additional test code here
        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NullPointerException
        //       at org.tokenizer.crawler.db.CrawlerRepositoryCassandraImpl.getKeyspaceDefinition(CrawlerRepositoryCassandraImpl.java:576)
    }




}