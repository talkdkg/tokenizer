package org.tokenizer.ui.lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinUI;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

public class UrlQueryFactory implements QueryFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(UrlQueryFactory.class);
    private QueryDefinition queryDefinition;
    private final MyVaadinUI app;

    public UrlQueryFactory(final MyVaadinUI app) {
        LOG.debug("constructor called...");
        this.app = app;
    }

    @Override
    public Query constructQuery(final Object[] sortPropertyIds,
            final boolean[] ascendingStates) {
        LOG.debug("query constructor called...");
        return new UrlQuery(app);
    }

    @Override
    public void setQueryDefinition(final QueryDefinition queryDefinition) {
        this.queryDefinition = queryDefinition;
    }
}
