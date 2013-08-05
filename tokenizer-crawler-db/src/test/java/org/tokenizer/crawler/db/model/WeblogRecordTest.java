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
package org.tokenizer.crawler.db.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class <code>WeblogRecordTest</code> contains tests for the class <code>{@link WeblogRecord}</code>.
 * 
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class WeblogRecordTest {
    /**
     * Run the WeblogRecord() constructor test.
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testWeblogRecord_1() throws Exception {
        WeblogRecord result = new WeblogRecord();
        assertNotNull(result);
        // add additional test code here
    }

    /**
     * Run the void add(Weblog) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testAdd_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());
        WeblogRecord.Weblog weblog = new WeblogRecord.Weblog();

        fixture.add(weblog);

        // add additional test code here
    }

    /**
     * Run the int getCount() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetCount_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());

        int result = fixture.getCount();

        // add additional test code here
        assertEquals(1, result);
    }

    /**
     * Run the int getCountInverted() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetCountInverted_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());

        int result = fixture.getCountInverted();

        // add additional test code here
        assertEquals(2147483646, result);
    }

    /**
     * Run the List<WeblogRecord.Weblog> getWeblogs() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetWeblogs_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());

        List<WeblogRecord.Weblog> result = fixture.getWeblogs();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setCount(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetCount_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());
        int count = 1;

        fixture.setCount(count);

        // add additional test code here
    }

    /**
     * Run the void setCountInverted(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetCountInverted_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());
        int countInverted = 1;

        fixture.setCountInverted(countInverted);

        // add additional test code here
    }

    /**
     * Run the void setWeblogs(List<Weblog>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetWeblogs_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());
        List<WeblogRecord.Weblog> weblogs = new LinkedList();

        fixture.setWeblogs(weblogs);

        // add additional test code here
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testToString_1() throws Exception {
        WeblogRecord fixture = new WeblogRecord();
        fixture.setCount(1);
        fixture.setWeblogs(new LinkedList());

        String result = fixture.toString();

        // add additional test code here
        assertEquals("WeblogBatchRecord [count=1, weblogs=[]]", result);
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
        new org.junit.runner.JUnitCore().run(WeblogRecordTest.class);
    }
}
