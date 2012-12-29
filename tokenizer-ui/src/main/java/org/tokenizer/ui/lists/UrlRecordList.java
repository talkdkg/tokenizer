package org.tokenizer.ui.lists;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.ui.Table;

public class UrlRecordList extends Table {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(UrlRecordList.class);
    private final ArrayList<Object> visibleColumnIds = new ArrayList<Object>();
    private final ArrayList<String> visibleColumnLabels = new ArrayList<String>();
    private final MyVaadinApplication app;
    private final LazyQueryContainer lazyQueryContainer;

    public UrlRecordList(final MyVaadinApplication app) {
        this.app = app;
        setSizeFull();
        UrlQueryFactory urlQueryFactory = new UrlQueryFactory(app);
        lazyQueryContainer = new LazyQueryContainer(urlQueryFactory, false, 100);
        lazyQueryContainer.addContainerProperty(UrlQuery.URL, String.class, "",
                true, false);
        lazyQueryContainer.addContainerProperty(UrlQuery.DATE, Date.class,
                UrlQuery.DEFAULT_DATE, true, false);
        lazyQueryContainer.addContainerProperty(UrlQuery.HTTP_RESPONSE_CODE,
                Integer.class, 0, true, false);
        lazyQueryContainer.addContainerProperty(UrlQuery.URL_RECORD,
                UrlRecord.class, null);
        setContainerDataSource(lazyQueryContainer);
        visibleColumnIds.add(UrlQuery.URL);
        visibleColumnIds.add(UrlQuery.DATE);
        visibleColumnIds.add(UrlQuery.HTTP_RESPONSE_CODE);
        visibleColumnLabels.add(UrlQuery.URL);
        visibleColumnLabels.add(UrlQuery.DATE);
        visibleColumnLabels.add(UrlQuery.HTTP_RESPONSE_CODE);
        setVisibleColumns(visibleColumnIds.toArray());
        setColumnHeaders(visibleColumnLabels.toArray(new String[0]));
        setSelectable(true);
        setImmediate(true);
        addListener(app);
        setNullSelectionAllowed(false);
        setColumnCollapsingAllowed(true);
        setColumnReorderingAllowed(true);
    }

    public LazyQueryContainer getLazyQueryContainer() {
        return lazyQueryContainer;
    }
}
