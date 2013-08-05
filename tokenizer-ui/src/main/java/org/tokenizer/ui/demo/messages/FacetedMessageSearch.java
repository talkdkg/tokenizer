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
import org.apache.solr.client.solrj.response.FacetField;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class FacetedMessageSearch extends AbstractMessageSearch {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FacetedMessageSearch.class);

    FacetField hostFacetField;
    FacetField featureFacetField;
    FacetField sentimentFacetField;

    @Override
    protected void initialQuerySolr(final MyQuery q) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(q.getQuery());
        solrQuery.setRows(0);
        solrQuery.setSortField("_docid_", ORDER.asc);
        solrQuery.setParam("df", "content_en");
        solrQuery.setParam("q.op", "AND");

        solrQuery.addFacetField("feature_ss");
        solrQuery.addFacetField("sentiment_s");
        solrQuery.addFacetField("host_s");

        solrQuery.setSortField("_docid_", ORDER.asc);
        solrQuery.setParam("df", "content_en");

        LOG.debug("solrQuery: {}", solrQuery);
        try {
            queryResponse = solrServer.query(solrQuery);
        } catch (SolrServerException e) {
            LOG.error(e.getMessage());
            beans = new ArrayList<MessageBean>();
        }
        LOG.debug(queryResponse.toString());

        hostFacetField = queryResponse.getFacetField("host_s");
        featureFacetField = queryResponse.getFacetField("feature_ss");
        sentimentFacetField = queryResponse.getFacetField("sentiment_s");

    }

    @Override
    Component buildFacets() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        Table table = new Table();
        table.addContainerProperty("Source", String.class, null);
        table.addContainerProperty("Count", Integer.class, null);
        for (FacetField.Count c : hostFacetField.getValues()) {
            Object newItemId = table.addItem();
            Item row = table.getItem(newItemId);
            row.getItemProperty("Source").setValue(c.getName());
            row.getItemProperty("Count").setValue((int) c.getCount());
        }
        layout.addComponent(table);

        table = new Table();
        table.addContainerProperty("Feature", String.class, null);
        table.addContainerProperty("Count", Integer.class, null);
        for (FacetField.Count c : featureFacetField.getValues()) {
            Object newItemId = table.addItem();
            Item row = table.getItem(newItemId);
            row.getItemProperty("Feature").setValue(c.getName());
            row.getItemProperty("Count").setValue((int) c.getCount());
        }
        layout.addComponent(table);

        table = new Table();
        table.addContainerProperty("Sentiment", String.class, null);
        table.addContainerProperty("Count", Integer.class, null);
        for (FacetField.Count c : sentimentFacetField.getValues()) {
            Object newItemId = table.addItem();
            Item row = table.getItem(newItemId);
            row.getItemProperty("Sentiment").setValue(c.getName());
            row.getItemProperty("Count").setValue((int) c.getCount());
        }
        layout.addComponent(table);

        return layout;

    }

}
