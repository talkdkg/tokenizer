/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.crawler.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tokenizer.crawler.db.model.HostRecord;

/**
 * The class <code>HostRecordTest</code> contains tests for the class <code>{@link HostRecord}</code>.
 * 
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class HostRecordTest {

    /**
     * Run the HostRecord(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testHostRecord_1() throws Exception {
        String host = "";

        HostRecord result = new HostRecord(host);

        // add additional test code here
        assertNotNull(result);
        assertEquals(null, result.getTld());

        result = new HostRecord("www.amazon.com");
        assertNotNull(result);
        assertEquals("com", result.getTld());
        assertEquals("com.amazon.www", new String(result.getHostInverted(), "ASCII"));
        assertEquals("com.amazon.www", new String(result.getHostInverted(), "UTF-8"));

    }

    /**
     * Run the byte[] getHostInverted() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHostInverted_1() throws Exception {
        HostRecord fixture = new HostRecord((String) null);

        byte[] result = fixture.getHostInverted();

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
    public void testGetHostInverted_2() throws Exception {
        HostRecord fixture = new HostRecord("");

        byte[] result = fixture.getHostInverted();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the byte[] getHostInverted() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHostInverted_3() throws Exception {
        HostRecord fixture = new HostRecord("");

        byte[] result = fixture.getHostInverted();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the Map<String, Object> getPayload() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetPayload_1() throws Exception {
        HostRecord fixture = new HostRecord("");

        Map<String, Object> result = fixture.getPayload();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getTld() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTld_1() throws Exception {
        HostRecord fixture = new HostRecord((String) null);

        String result = fixture.getTld();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the String getTld() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTld_2() throws Exception {
        HostRecord fixture = new HostRecord("");

        String result = fixture.getTld();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the String getTld() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTld_3() throws Exception {
        HostRecord fixture = new HostRecord("");

        String result = fixture.getTld();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Run the String getTld() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTld_4() throws Exception {
        HostRecord fixture = new HostRecord("");

        String result = fixture.getTld();

        // add additional test code here
        assertEquals(null, result);
    }

    /**
     * Perform pre-test initialization.
     * 
     * @throws Exception
     *             if the initialization fails for some reason
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Before
    public void setUp() throws Exception {
        // add additional set up code here
    }

    /**
     * Perform post-test clean-up.
     * 
     * @throws Exception
     *             if the clean-up fails for some reason
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @After
    public void tearDown() throws Exception {
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     * 
     * @param args
     *            the command line arguments
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    public static void main(String[] args) {
        new org.junit.runner.JUnitCore().run(HostRecordTest.class);
    }
}
