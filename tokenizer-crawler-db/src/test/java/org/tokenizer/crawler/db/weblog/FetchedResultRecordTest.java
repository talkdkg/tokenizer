package org.tokenizer.crawler.db.weblog;

import java.text.DateFormat;
import crawlercommons.fetcher.FetchedResult;
import java.util.Date;
import org.apache.tika.metadata.Metadata;
import crawlercommons.fetcher.Payload;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>FetchedResultRecordTest</code> contains tests for the class <code>{@link FetchedResultRecord}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class FetchedResultRecordTest {
    /**
     * Run the FetchedResultRecord(String,Date,FetchedResult) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testFetchedResultRecord_1()
        throws Exception {
        String url = "";
        Date timestamp = new Date();
        FetchedResult fetchedResult = new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, "");

        FetchedResultRecord result = new FetchedResultRecord(url, timestamp, fetchedResult);

        // add additional test code here
        assertNotNull(result);
        assertEquals(null, result.getHost());
        assertEquals("", result.getUrl());
        assertEquals(null, result.getHostInverted());
    }

    /**
     * Run the FetchedResult getFetchedResult() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetFetchedResult_1()
        throws Exception {
        FetchedResultRecord fixture = new FetchedResultRecord("", new Date(), new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, ""));

        FetchedResult result = fixture.getFetchedResult();

        // add additional test code here
        assertNotNull(result);
        assertEquals("FetchedResult [_baseUrl=, _fetchedUrl=, _fetchTime=1, _content=\n, \n_contentType=, _responseRate=1, _headers=, _newBaseUrl=, _numRedirects=1, _hostAddress=, _httpStatus=1, _reasonPhrase=, _payload=crawlercommons.fetcher.Payload@0]", result.toString());
        assertEquals("", result.getHostAddress());
        assertEquals("", result.getContentType());
        assertEquals(0, result.getContentLength());
        assertEquals("", result.getReasonPhrase());
        assertEquals("", result.getBaseUrl());
        assertEquals("", result.getFetchedUrl());
        assertEquals(1L, result.getFetchTime());
        assertEquals(1, result.getResponseRate());
        assertEquals("", result.getNewBaseUrl());
        assertEquals(1, result.getNumRedirects());
        assertEquals(1, result.getHttpStatus());
    }

    /**
     * Run the String getHost() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHost_1()
        throws Exception {
        FetchedResultRecord fixture = new FetchedResultRecord("", new Date(), new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, ""));

        String result = fixture.getHost();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the byte[] getHostInverted() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHostInverted_1()
        throws Exception {
        FetchedResultRecord fixture = new FetchedResultRecord("", new Date(), new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, ""));

        byte[] result = fixture.getHostInverted();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the Date getTimestamp() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTimestamp_1()
        throws Exception {
        FetchedResultRecord fixture = new FetchedResultRecord("", new Date(), new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, ""));

        Date result = fixture.getTimestamp();

        // add additional test code here
        assertNotNull(result);
        //assertEquals(DateFormat.getInstance().format(new Date(1364842365039L)), DateFormat.getInstance().format(result));
        //assertEquals(1364842365039L, result.getTime());
    }

    /**
     * Run the String getUrl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetUrl_1()
        throws Exception {
        FetchedResultRecord fixture = new FetchedResultRecord("", new Date(), new FetchedResult("", "", 1L, new Metadata(), new byte[] {}, "", 1, new Payload(), "", 1, "", 1, ""));

        String result = fixture.getUrl();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *         if the initialization fails for some reason
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Before
    public void setUp()
        throws Exception {
        // add additional set up code here
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception
     *         if the clean-up fails for some reason
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @After
    public void tearDown()
        throws Exception {
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     *
     * @param args the command line arguments
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    public static void main(String[] args) {
        new org.junit.runner.JUnitCore().run(FetchedResultRecordTest.class);
    }
}