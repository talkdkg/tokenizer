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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TimestampUrlIDXTest {

    @Test
    public void testWeblogRecord_1() throws Exception {
        TimestampUrlIDX result = new TimestampUrlIDX("http://www.amazon.com/%C3%81gnes-Szak%C3%A1ly/dp/B001QR2DR4");
        assertNotNull(result);
        System.out.println(result);

    }

}
