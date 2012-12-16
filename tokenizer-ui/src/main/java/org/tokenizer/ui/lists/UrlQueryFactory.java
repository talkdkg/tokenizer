package org.tokenizer.ui.lists;

import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.ui.MyVaadinApplication;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

public class UrlQueryFactory implements QueryFactory {

    private QueryDefinition queryDefinition;
    private final MyVaadinApplication app;
    private final CrawlerRepository crawlerRepository;
    private final String host;
    private final int httpResponseCode;

    public UrlQueryFactory(final MyVaadinApplication app, final String host,
            final int httpResponseCode) {
        this.app = app;
        this.crawlerRepository = app.getRepository();
        this.host = host;
        this.httpResponseCode = httpResponseCode;
    }

    @Override
    public Query constructQuery(final Object[] sortPropertyIds,
            final boolean[] ascendingStates) {
        return new UrlQuery(app, host, httpResponseCode);
    }

    @Override
    public void setQueryDefinition(final QueryDefinition queryDefinition) {
        this.queryDefinition = queryDefinition;
    }
}
