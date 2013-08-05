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
package org.tokenizer.executor.engine;

public class SolrFields {

    public static final String LILY_ID = "lily.id";

    /**
     * Solr is required to store URL and Timestamp fields, URL is "stored" and
     * Timestamp is "indexed", for better performance and failover (we might have
     * URL in Solr, and empty Lily server)
     */
    public static final String BASE_URL = "baseUrl";
    public static final String TLD = "tld";
    public static final String TIMESTAMP = "timestamp";

}
