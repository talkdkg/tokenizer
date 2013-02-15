/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private static final String SOLR_URL = TokenizerConfig.getProperties().getProperty("solr.url", "http://localhost:8080/solr/url_records");
    private static final String SOLR_URL_BASE = TokenizerConfig.getProperties().getProperty("solr.url.base", "http://localhost:8080/solr");

    private static SolrServer solrServer = null;
    private static SolrServer solrServerMessages = null;


    public static SolrServer getSolrServer() {
        if (solrServer == null) {
            synchronized (SolrUtils.class) {
                if (solrServer == null) {
                    HttpSolrServer httpSolrServer = new HttpSolrServer(SOLR_URL);
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
                    HttpSolrServer httpSolrServer = new HttpSolrServer(SOLR_URL_BASE + "/messages");
                    httpSolrServer.setSoTimeout(30000); // socket read timeout
                    httpSolrServer.setConnectionTimeout(30000);
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
