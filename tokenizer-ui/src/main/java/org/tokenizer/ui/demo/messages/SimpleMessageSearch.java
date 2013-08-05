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
package org.tokenizer.ui.demo.messages;

import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.tokenizer.ui.v7.view.MessageSearchComponent;

import com.vaadin.ui.Component;

public class SimpleMessageSearch extends AbstractMessageSearch {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageSearchComponent.class);

    @Override
    protected void initialQuerySolr(final MyQuery q) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(q.getQuery());
        solrQuery.setRows(0);
        solrQuery.setSortField("_docid_", ORDER.asc);
        solrQuery.setParam("df", "content_en");
        solrQuery.setParam("q.op", "AND");
        LOG.debug("solrQuery: {}", solrQuery);
        try {
            queryResponse = solrServer.query(solrQuery);
        } catch (SolrServerException e) {
            LOG.error(e.getMessage());
            beans = new ArrayList<MessageBean>();
        }
        LOG.debug(queryResponse.toString());
    }

    @Override
    Component buildFacets() {

        return null;
    }

}
