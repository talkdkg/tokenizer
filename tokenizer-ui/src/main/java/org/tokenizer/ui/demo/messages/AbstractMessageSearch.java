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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.common.util.NamedList;
import org.tokenizer.core.solr.SolrUtils;
import org.tokenizer.ui.v7.view.AbstractScreen;
import org.tokenizer.ui.v7.view.MessageSearchComponent;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractMessageSearch extends AbstractScreen {

    @Override
    protected Component get() {

        buildMainLayout();
        return mainLayout;

    }

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageSearchComponent.class);
    private static final int COMMON_FIELD_WIDTH = 48;
    protected static SolrServer solrServer = SolrUtils.getSolrServerForMessages();

    private VerticalLayout mainLayout;
    MyQuery myQuery;
    QueryResponse queryResponse;
    List<MessageBean> beans;

    MessageBean currentBean;
    Component searchResultsComponent;
    LazyQueryContainer lazyQueryContainer;
    Component crawledContentTabSheet;
    FacetField hostFacetField;
    RangeFacet.Date dateRangeFacet;
    NamedList<List<PivotField>> facetPivot;

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
        myQuery = new MyQuery();
        BeanItem<MyQuery> myQueryItem = new BeanItem<MyQuery>(myQuery);
        fieldGroup.setItemDataSource(myQueryItem);
        fieldGroup.setBuffered(false);
        return componentContainer;
    }

    private Component buildFormControls() {

        ComponentContainer formControls = new HorizontalLayout();

        Button search = new Button("Search", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                LOG.debug(myQuery.toString());
                initialQuerySolr(myQuery);
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

    public class MyQuery implements Serializable {

        private static final long serialVersionUID = 1L;

        private String query = "";

        public String getQuery() {
            return query;
        }

        public void setQuery(final String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return "MyQuery [query=" + query + "]";
        }

    }

    public static class MessageBean implements Serializable {

        private static final long serialVersionUID = 1L;

        @Field
        String id;

        @Field("host_s")
        String host;

        @Field("content_en")
        String content;

        @Field("age_ti")
        Integer age;

        @Field("author_en")
        String author;

        @Field("date_tdt")
        Date date;

        @Field("sex_s")
        String sex;

        @Field("title_en")
        String title;

        @Field("topic_en")
        String topic;

        @Field("userRating_s")
        String userRating;

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

        public String getContent() {
            return content;
        }

        public void setContent(final String content) {
            this.content = content;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(final Integer age) {
            this.age = age;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(final String author) {
            this.author = author;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(final Date date) {
            this.date = date;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(final String sex) {
            this.sex = sex;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(final String topic) {
            this.topic = topic;
        }

        public String getUserRating() {
            return userRating;
        }

        public void setUserRating(final String userRating) {
            this.userRating = userRating;
        }

        public String getHighlightSnippet() {
            return highlightSnippet;
        }

        public void setHighlightSnippet(final String highlightSnippet) {
            this.highlightSnippet = highlightSnippet;
        }

        @Override
        public String toString() {
            return "MessageBean [id=" + id + ", host=" + host + ", content=" + content + ", age=" + age + ", author="
                + author + ", date=" + date + ", sex=" + sex + ", title=" + title + ", topic=" + topic
                + ", userRating=" + userRating + ", highlightSnippet=" + highlightSnippet + "]";
        }

    }

    private Component buildSearchResults() {
        VerticalLayout layout = new VerticalLayout();
        final Table table = new Table(null);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        MyLazyQueryFactory myLazyQueryFactory = new MyLazyQueryFactory();
        lazyQueryContainer = new LazyQueryContainer(myLazyQueryFactory, false, 100);
        // lazyQueryContainer.addContainerProperty("id", String.class, "", true,
        // false);
        lazyQueryContainer.addContainerProperty("host", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("date", Date.class, new Date(0), true, false);
        lazyQueryContainer.addContainerProperty("topic", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("author", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("title", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("highlightSnippet", String.class, "", true, false);
        table.setContainerDataSource(lazyQueryContainer);
        table.setVisibleColumns(new String[] { "host", "date", "topic", "author", "title", "highlightSnippet" });
        table.setColumnHeaders(new String[] { "Host", "Date", "Topic", "Author", "Title", "Content" });
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object itemId = table.getValue();
                BeanItem<MessageBean> o = (BeanItem<MessageBean>) table.getItem(itemId);
                currentBean = o.getBean();
                LOG.warn(currentBean.toString());
                /*
                 * Component newCrawledContentTabSheet = buildCrawledContentTabSheet();
                 * if (crawledContentTabSheet == null) {
                 * mainLayout.addComponent(newCrawledContentTabSheet);
                 * } else {
                 * mainLayout.replaceComponent(crawledContentTabSheet,
                 * newCrawledContentTabSheet);
                 * }
                 * crawledContentTabSheet = newCrawledContentTabSheet;
                 */
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

        Component facets = buildFacets();
        if (facets != null) {
            layout.addComponent(buildFacets());
        }

        return layout;
    }

    public class MyLazyQueryFactory implements QueryFactory {

        private QueryDefinition queryDefinition;

        @Override
        public Query constructQuery(final Object[] sortPropertyIds, final boolean[] ascendingStates) {
            LOG.debug("query constructor called...");
            return new MyLazyQuery();
        }

        @Override
        public void setQueryDefinition(final QueryDefinition queryDefinition) {
            this.queryDefinition = queryDefinition;
        }
    }

    public class MyLazyQuery implements Query {

        long numFound = 0;

        public MyLazyQuery() {
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(myQuery.getQuery());
            // solrQuery.setFilterQueries("httpResponseCode:"
            // + myQuery.getHttpResponseCode());
            solrQuery.setStart(0);
            solrQuery.setRows(0);
            solrQuery.setParam("df", "content_en");
            solrQuery.setParam("q.op", "AND");
            solrQuery.setSortField("_docid_", ORDER.asc);
            try {
                LOG.debug("Querying Solr... {}", solrQuery);
                queryResponse = solrServer.query(solrQuery);
                numFound = queryResponse.getResults().getNumFound();
            }
            catch (SolrServerException e) {
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
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(myQuery.getQuery());
            // solrQuery.setFilterQueries("httpResponseCode:"
            // + myQuery.getHttpResponseCode());
            // solrQuery.setFacet(true);
            // solrQuery.setFacetMinCount(10);
            // solrQuery.addFacetField("host");
            solrQuery.setHighlight(true);
            solrQuery.setHighlightSnippets(1);
            solrQuery.setHighlightFragsize(4096);
            solrQuery.setParam("hl.fl", "content_en");
            solrQuery.setStart(startIndex);
            solrQuery.setRows(count);
            solrQuery.setParam("df", "content_en");
            solrQuery.setParam("q.op", "AND");
            // for better performance:
            solrQuery.setSortField("_docid_", ORDER.asc);
            try {
                LOG.debug("Querying Solr... {}", solrQuery);
                queryResponse = solrServer.query(solrQuery);
            }
            catch (SolrServerException e) {
                LOG.error(e.getMessage());
                beans = new ArrayList<MessageBean>();
            }
            LOG.debug(queryResponse.toString());
            beans = queryResponse.getBeans(MessageBean.class);
            for (MessageBean bean : beans) {
                String id = bean.getId();
                if (queryResponse.getHighlighting().get(id) != null) {
                    List<String> highlightSnippets = queryResponse.getHighlighting().get(id).get("content_en");
                    bean.setHighlightSnippet(highlightSnippets.get(0));
                }
                LOG.debug(bean.toString());
            }
            List<Item> items = new ArrayList<Item>();
            for (MessageBean bean : beans) {
                items.add(new BeanItem<MessageBean>(bean));
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

    abstract void initialQuerySolr(MyQuery myQuery);

    abstract Component buildFacets();

}
