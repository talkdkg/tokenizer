/*
 * Copyright 2013 Tokenizer Inc.
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

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(FacetedMessageSearch.class);

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
