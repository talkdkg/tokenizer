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
package org.tokenizer.ui.v7.view;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.tika.utils.CharsetUtils;
import org.tokenizer.core.solr.SolrUtils;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.DefaultValues;
import org.tokenizer.crawler.db.MessageRecord;
import org.tokenizer.crawler.db.XmlRecord;
import org.tokenizer.crawler.db.model.UrlRecord;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import com.google.inject.Inject;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UrlSearchComponent extends CustomComponent {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UrlSearchComponent.class);
    private static final int COMMON_FIELD_WIDTH = 48;
    private VerticalLayout mainLayout;
    MyQuery myQuery;
    List<UrlBean> beans;
    QueryResponse queryResponse;
    UrlBean currentBean;
    Component searchResultsComponent;
    LazyQueryContainer lazyQueryContainer;
    Component crawledContentTabSheet;
    WritableExecutorModel model;
    CrawlerRepository repository;

    @Inject
    public UrlSearchComponent(final WritableExecutorModel model, final CrawlerRepository repository) {
        this.model = model;
        this.repository = repository;

        buildMainLayout();
        setCompositionRoot(mainLayout);
        setCaption("Personal details");
        // mainLayout.setSizeFull();

    }

    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.addComponent(buildSearchForm());
        mainLayout.addComponent(buildFormControls());
    }

    private Component buildSearchForm() {
        ComponentContainer componentContainer = new FormLayout();
        FieldGroup fieldGroup = new FieldGroup();
        TextField query = new TextField("Query");
        query.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(query);
        fieldGroup.bind(query, "query");
        TextField httpStatus = new TextField("httpStatus");
        httpStatus.setWidth(8, Unit.EM);
        componentContainer.addComponent(httpStatus);
        fieldGroup.bind(httpStatus, "httpStatus");
        myQuery = new MyQuery();
        BeanItem<MyQuery> myQueryItem = new BeanItem<MyQuery>(myQuery);
        fieldGroup.setItemDataSource(myQueryItem);
        fieldGroup.setBuffered(false);
        return componentContainer;
    }

    private Component buildFormControls() {
        ComponentContainer formControls = new HorizontalLayout();
        Button search = new Button("Search", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                LOG.debug(myQuery.toString());
                querySolr(myQuery);
                Component newSearchResultsComponent = buildSearchResults();
                if (searchResultsComponent == null) {
                    mainLayout.addComponent(newSearchResultsComponent);
                }
                else {
                    mainLayout.replaceComponent(searchResultsComponent, newSearchResultsComponent);
                }
                searchResultsComponent = newSearchResultsComponent;
            }
        });
        search.setClickShortcut(KeyCode.ENTER);
        search.addStyleName("primary");
        formControls.addComponent(search);
        return formControls;
    }

    private void querySolr(final MyQuery q) {
        SolrServer solrServer = SolrUtils.getSolrServer();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(q.getQuery());
        solrQuery.setFacet(true);
        solrQuery.setFacetMinCount(10);
        solrQuery.addFacetField("host");
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSnippets(1);
        solrQuery.setHighlightFragsize(4096);
        solrQuery.setParam("hl.fl", "url");
        solrQuery.setRows(10);
        // for better performance:
        solrQuery.setSortField("_docid_", ORDER.asc);
        try {
            queryResponse = solrServer.query(solrQuery);
        } catch (SolrServerException e) {
            LOG.error(e.getMessage());
            beans = new ArrayList<UrlBean>();
        }
        LOG.debug(queryResponse.toString());
        beans = queryResponse.getBeans(UrlBean.class);
        for (UrlBean bean : beans) {
            String id = bean.getId();
            if (queryResponse.getHighlighting().get(id) != null) {
                List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("url");
                bean.setHighlightSnippet(highlightSnippets.get(0));
            }
            LOG.debug(bean.toString());
        }
    }

    private Component buildSearchResults() {
        VerticalLayout layout = new VerticalLayout();
        final Table table = new Table(null);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        UrlQueryFactory urlQueryFactory = new UrlQueryFactory();
        lazyQueryContainer = new LazyQueryContainer(urlQueryFactory, false, 100);
        lazyQueryContainer.addContainerProperty("id", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("host", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("highlightSnippet", String.class, "", true, false);
        table.setContainerDataSource(lazyQueryContainer);
        table.setVisibleColumns(new String[] { "host", "highlightSnippet" });
        table.setColumnHeaders(new String[] { "Host", "URL" });
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object itemId = table.getValue();
                BeanItem<UrlBean> o = (BeanItem<UrlBean>) table.getItem(itemId);
                currentBean = o.getBean();
                LOG.warn(currentBean.toString());
                Component newCrawledContentTabSheet = buildCrawledContentTabSheet();
                if (crawledContentTabSheet == null) {
                    mainLayout.addComponent(newCrawledContentTabSheet);
                }
                else {
                    mainLayout.replaceComponent(crawledContentTabSheet, newCrawledContentTabSheet);
                }
                crawledContentTabSheet = newCrawledContentTabSheet;
            }
        });
        table.addGeneratedColumn("highlightSnippet", new ColumnGenerator() {

            @Override
            public Object generateCell(final Table source, final Object itemId, final Object columnId) {
                final Item item = source.getItem(itemId);
                final Property prop = item.getItemProperty(columnId);
                final String text = (String) prop.getValue();
                final Label label = new Label(text, ContentMode.HTML);
                return label;
            }
        });
        layout.addComponent(table);
        Label label = new Label("Elapsed time: " + queryResponse.getElapsedTime() + "(ms)");
        layout.addComponent(label);
        label = new Label("Query time: " + queryResponse.getQTime() + "(ms)");
        layout.addComponent(label);
        label = new Label("Total found: " + queryResponse.getResults().getNumFound());
        layout.addComponent(label);
        return layout;
    }

    private Component buildCrawledContentTabSheet() {
        Label htmlLabel;
        Label sourceLabel;
        VerticalLayout xmlLayout;
        VerticalLayout messageLayout;
        TabSheet component = new TabSheet();
        component.setCaption("Crawled Content");
        htmlLabel = new Label();
        htmlLabel.setContentMode(ContentMode.HTML);
        Panel htmlPanel = new Panel("HTML", htmlLabel);
        component.addTab(htmlPanel);
        sourceLabel = new Label();
        sourceLabel.setContentMode(ContentMode.PREFORMATTED);
        
        Panel sourcePanel = new Panel("Source", sourceLabel);
        component.addTab(sourcePanel);
        // XML Snippets:
        xmlLayout = new VerticalLayout();
        Panel xml = new Panel("XML Snippets", xmlLayout);
        component.addTab(xml);
        // Parsed XML:
        messageLayout = new VerticalLayout();
        Panel messages = new Panel("Parsed Messages", messageLayout);
        component.addTab(messages);

        // component.setHeight(100, Unit.PERCENTAGE);
        component.setSizeUndefined();

        // byte[] urlRecordDigest = MD5.hex2Byte(currentBean.id);
        WebpageRecord webpage = null;
        String html = null;
        try {
            UrlRecord urlRecord = repository.retrieveUrlRecord(currentBean.url);
            //if (urlRecord.getWebpageDigest() == null || urlRecord.getWebpageDigest() == DefaultValues.EMPTY_ARRAY) {
            //    return component;
            //}
            webpage = repository.retrieveWebpageRecord(urlRecord.getWebpageDigest());
            if (webpage == null) {
                return component;
            }

            String charset = webpage.getCharset();
            html = new String(webpage.getContent(), charset);
            
            LOG.debug("Charset: {}", charset);
            
            
            // TODO: !!!
            List<XmlRecord> xmlRecords = repository.listXmlRecords(new byte[0][0]);
            for (XmlRecord xmlRecord : xmlRecords) {
                if (xmlRecord == null) {
                    continue;
                }
                LOG.debug(xmlRecord.toString());
                FormLayout form = new FormLayout();
                Panel panel = new Panel("XML Snippet", form);
                TextField field = new TextField("Digest");
                field.setValue(MD5.toHexString(xmlRecord.getDigest()));
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Host");
                field.setValue(xmlRecord.getHost());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                TextArea text = new TextArea("Content");
                text.setValue(new String(xmlRecord.getContent(), "UTF-8"));
                text.setReadOnly(true);
                text.setSizeFull();
                text.setRows(25);
                form.addComponent(text);
                xmlLayout.addComponent(panel);
            }
            
            // TODO: !!!
            List<MessageRecord> messageRecords = repository.listMessageRecords(new byte[0][0]);
            
            for (MessageRecord messageRecord : messageRecords) {
                if (messageRecord == null) {
                    continue;
                }
                FormLayout form = new FormLayout();
                Panel panel = new Panel("Parsed Message", form);
                TextField field = new TextField("Digest");
                field.setValue(MD5.toHexString(messageRecord.getDigest()));
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Host");
                field.setValue(messageRecord.getHost());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Topic");
                field.setValue(messageRecord.getTopic());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Date");
                field.setValue(messageRecord.getDate());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Author");
                field.setValue(messageRecord.getAuthor());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Age");
                field.setValue(messageRecord.getAge());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Sex");
                field.setValue(messageRecord.getSex());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                field = new TextField("Title");
                field.setValue(messageRecord.getTitle());
                field.setReadOnly(true);
                form.addComponent(field);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                TextArea text = new TextArea("Content");
                text.setValue(messageRecord.getContent());
                text.setReadOnly(true);
                text.setSizeFull();
                text.setRows(25);
                form.addComponent(text);
                field = new TextField("User Rating");
                field.setValue(messageRecord.getUserRating());
                field.setReadOnly(true);
                field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
                form.addComponent(field);
                messageLayout.addComponent(panel);
            }
        } catch (ConnectionException e) {
            LOG.error("", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            html = e.getMessage();
        }
        htmlLabel.setValue(html);
        sourceLabel.setValue(html);
        return component;
    }

    public class MyQuery implements Serializable {

        private String query = "";
        private String httpStatus = "";

        public String getHttpStatus() {
            return httpStatus;
        }

        public void setHttpStatus(final String httpStatus) {
            this.httpStatus = httpStatus;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(final String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return "MyQuery [query=" + query + ", httpStatus=" + httpStatus + "]";
        }
    }

    public static class UrlBean {

        @Field
        String id;
        @Field
        String host;
        @Field
        String url;
        String highlightSnippet;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(final String host) {
            this.host = host;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getHighlightSnippet() {
            return highlightSnippet;
        }

        public void setHighlightSnippet(final String highlightSnippet) {
            this.highlightSnippet = highlightSnippet;
        }

        @Override
        public String toString() {
            return "UrlBean [id=" + id + ", host=" + host + ", url=" + url + ", highlightSnippet=" + highlightSnippet
                    + "]";
        }
    }

    public class UrlQuery implements Query {

        SolrServer solrServer;
        long numFound = 0;

        public UrlQuery() {
            solrServer = SolrUtils.getSolrServer();
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(myQuery.getQuery());
            try {
                Integer.parseInt(myQuery.getHttpStatus());
                solrQuery.setFilterQueries("httpStatus:" + myQuery.getHttpStatus());
            } catch (NumberFormatException e) {
            }
            solrQuery.setStart(0);
            solrQuery.setRows(0);
            solrQuery.setSortField("_docid_", ORDER.asc);
            try {
                LOG.debug("Querying Solr... {}", solrQuery);
                queryResponse = solrServer.query(solrQuery);
                numFound = queryResponse.getResults().getNumFound();
            } catch (SolrServerException e) {
            }
        }

        @Override
        public Item constructItem() {
            PropertysetItem item = new PropertysetItem();
            return item;
        }

        @Override
        public boolean deleteAllItems() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Item> loadItems(final int startIndex, final int count) {
            LOG.debug("loadItems() called... startIndex: {}, count: {}", startIndex, count);
            SolrServer solrServer = SolrUtils.getSolrServer();
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(myQuery.getQuery());
            try {
                Integer.parseInt(myQuery.getHttpStatus());
                solrQuery.setFilterQueries("httpStatus:" + myQuery.getHttpStatus());
            } catch (NumberFormatException e) {
            }
            // solrQuery.setFacet(true);
            // solrQuery.setFacetMinCount(10);
            // solrQuery.addFacetField("host");
            solrQuery.setHighlight(true);
            solrQuery.setHighlightSnippets(1);
            solrQuery.setHighlightFragsize(4096);
            solrQuery.setParam("hl.fl", "url");
            solrQuery.setStart(startIndex);
            solrQuery.setRows(count);
            // for better performance:
            solrQuery.setSortField("_docid_", ORDER.asc);
            try {
                LOG.debug("Querying Solr... {}", solrQuery);
                queryResponse = solrServer.query(solrQuery);
            } catch (SolrServerException e) {
                LOG.error(e.getMessage());
                beans = new ArrayList<UrlBean>();
            }
            LOG.debug(queryResponse.toString());
            beans = queryResponse.getBeans(UrlBean.class);
            for (UrlBean bean : beans) {
                String id = bean.getId();
                if (queryResponse.getHighlighting().get(id) != null) {
                    List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("url");
                    bean.setHighlightSnippet(highlightSnippets.get(0));
                }
                LOG.debug(bean.toString());
            }
            List<Item> items = new ArrayList<Item>();
            for (UrlBean bean : beans) {
                items.add(new BeanItem<UrlBean>(bean));
            }
            return items;
        }

        @Override
        public void saveItems(final List<Item> arg0, final List<Item> arg1, final List<Item> arg2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return (int) numFound;
        }
    }

    public class UrlQueryFactory implements QueryFactory {

        private QueryDefinition queryDefinition;

        @Override
        public Query constructQuery(final Object[] sortPropertyIds, final boolean[] ascendingStates) {
            LOG.debug("query constructor called...");
            return new UrlQuery();
        }

        @Override
        public void setQueryDefinition(final QueryDefinition queryDefinition) {
            this.queryDefinition = queryDefinition;
        }
    }
}
