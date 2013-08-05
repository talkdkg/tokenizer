/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.core.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;

/**
 * 
 * Static Utility class to get access to Solr. TODO: fine-tune with new version
 * of HttpClient
 * 
 * @author Fuad
 * 
 */
public class SolrUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SolrUtils.class);
    private static final String SOLR_URL_BASE = TokenizerConfig.getProperties()
            .getProperty("solr.url.base", "http://localhost:8080/solr");

    private static SolrServer solrServer = null;
    private static SolrServer solrServerMessages = null;

    public static SolrServer getSolrServer() {
        if (solrServer == null) {
            synchronized (SolrUtils.class) {
                if (solrServer == null) {
                    HttpSolrServer httpSolrServer = new HttpSolrServer(
                            SOLR_URL_BASE + "/url_records");
                    httpSolrServer.setSoTimeout(30000); // socket read timeout
                    httpSolrServer.setConnectionTimeout(30000);
                    httpSolrServer.setDefaultMaxConnectionsPerHost(100);
                    httpSolrServer.setMaxTotalConnections(100);
                    httpSolrServer.setMaxRetries(1); // defaults to 0. > 1 not
                                                     // recommended.

                    solrServer = httpSolrServer;
                }
            }
        }
        return solrServer;
    }

    public static SolrServer getSolrServerForMessages() {
        if (solrServerMessages == null) {
            synchronized (SolrUtils.class) {
                if (solrServerMessages == null) {
                    HttpSolrServer httpSolrServer = new HttpSolrServer(
                            SOLR_URL_BASE + "/messages");
                    httpSolrServer.setSoTimeout(60000); // socket read timeout
                    httpSolrServer.setConnectionTimeout(60000);
                    httpSolrServer.setDefaultMaxConnectionsPerHost(100);
                    httpSolrServer.setMaxTotalConnections(100);
                    httpSolrServer.setMaxRetries(1); // defaults to 0. > 1 not
                                                     // recommended.

                    solrServerMessages = httpSolrServer;
                }
            }
        }
        return solrServerMessages;
    }

}
