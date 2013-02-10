package org.tokenizer.ui.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.tokenizer.core.solr.SolrUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UrlSearchComponent extends CustomComponent {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(UrlSearchComponent.class);
    private static final int COMMON_FIELD_WIDTH = 48;
    private VerticalLayout mainLayout;
    MyQuery myQuery;
    List<UrlBean> beans;
    QueryResponse queryResponse;
    UrlBean currentBean;
    Component searchResultsComponent;

    public UrlSearchComponent() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
        setCaption("Personal details");
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
                } else {
                    mainLayout.replaceComponent(searchResultsComponent,
                            newSearchResultsComponent);
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
        solrQuery.setRows(100);
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
                List<String> highlightSnippets = queryResponse
                        .getHighlighting().get(id).get("url");
                bean.setHighlightSnippet(highlightSnippets.get(0));
            }
            LOG.debug(bean.toString());
        }
    }

    private Component buildSearchResults() {
        final Table table = new Table(null);
        setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        BeanItemContainer<UrlBean> container = new BeanItemContainer<UrlBean>(
                UrlBean.class, beans);
        table.setContainerDataSource(container);
        table.setVisibleColumns(new String[] { "id", "host", "highlightSnippet" });
        table.setColumnHeaders(new String[] { "ID", "Host", "URL" });
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                currentBean = (UrlBean) table.getValue();
                // setReadOnly(true);
            }
        });
        table.addGeneratedColumn("highlightSnippet", new ColumnGenerator() {

            @Override
            public Object generateCell(final Table source, final Object itemId,
                    final Object columnId) {
                final Item item = source.getItem(itemId);
                final Property prop = item.getItemProperty(columnId);
                final String text = (String) prop.getValue();
                final Label label = new Label(text, ContentMode.HTML);
                return label;
            }
        });
        return table;
    }

    public class MyQuery implements Serializable {

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
            return "UrlBean [id=" + id + ", host=" + host + ", url=" + url
                    + ", highlightSnippet=" + highlightSnippet + "]";
        }
    }
}
