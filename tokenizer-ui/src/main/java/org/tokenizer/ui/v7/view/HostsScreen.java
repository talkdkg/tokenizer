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

import java.util.ArrayList;
import java.util.List;

import org.tokenizer.core.StringPool;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.model.HostRecord;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import com.google.inject.Inject;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class HostsScreen extends AbstractScreen {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HostsScreen.class);

    private VerticalLayout mainLayout;

    Component listOfHosts;
    String selectedTLD = StringPool.EMPTY;
    LazyQueryContainer lazyQueryContainer;
    CrawlerRepository repository;

    @Inject
    public HostsScreen(CrawlerRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Component get() {
        buildMainLayout();
        return mainLayout;
    }

    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        try {
            mainLayout.addComponent(buildHostSelector());
        }
        catch (UnsupportedOperationException e) {
            LOG.error("", e);
        }
        catch (ConnectionException e) {
            LOG.error("", e);
        }
        // mainLayout.addComponent(buildFormControls());
    }

    private Component buildHostSelector() throws UnsupportedOperationException, ConnectionException {
        ComboBox type = new ComboBox("Type");
        type.setNewItemsAllowed(false);
        type.setNullSelectionAllowed(false);
        type.setImmediate(true);
        type.addItem("");
        for (String host : repository.listTLDs()) {
            type.addItem(host);
        }
        type.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                selectedTLD = (String) event.getProperty().getValue();
                if (listOfHosts == null) {
                    listOfHosts = buildListOfHosts();
                    mainLayout.addComponent(listOfHosts);
                }
                else {
                    Component temp = buildListOfHosts();
                    mainLayout.replaceComponent(listOfHosts, temp);
                    listOfHosts = temp;
                }

            }
        });
        return type;
    }

    private Component buildListOfHosts() {
        Table table = new Table(null);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);

        MyQueryFactory myQueryFactory = new MyQueryFactory();
        lazyQueryContainer = new LazyQueryContainer(myQueryFactory, false, 100);
        // lazyQueryContainer.addContainerProperty("id", String.class, "", true,
        // false);
        lazyQueryContainer.addContainerProperty("host", String.class, "", true, false);
        table.setContainerDataSource(lazyQueryContainer);
        table.setVisibleColumns(new String[] { "host" });
        table.setColumnHeaders(new String[] { "Host" });

        return table;
    }

    public class MyQuery implements Query {

        long numFound = 0;

        public MyQuery() {
            numFound = repository.countHosts(selectedTLD);
        }

        @Override
        public Item constructItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteAllItems() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Item> loadItems(final int startIndex, final int count) {
            LOG.debug("loadItems() called... startIndex: {}, count: {}", startIndex, count);

            List<HostRecord> hostRecords = repository.listHostRecords(selectedTLD, startIndex, count);

            List<Item> items = new ArrayList<Item>();
            for (HostRecord hostRecord : hostRecords) {
                items.add(new BeanItem<HostRecord>(hostRecord));
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

    public class MyQueryFactory implements QueryFactory {

        private QueryDefinition queryDefinition;

        @Override
        public Query constructQuery(final Object[] sortPropertyIds, final boolean[] ascendingStates) {
            LOG.debug("query constructor called...");
            return new MyQuery();
        }

        @Override
        public void setQueryDefinition(final QueryDefinition queryDefinition) {
            this.queryDefinition = queryDefinition;
        }
    }

}
