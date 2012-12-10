package org.tokenizer.ui.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.crawler.db.UrlRecords;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.ui.MyVaadinApplication;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public class UrlContainer implements Container {

    private static final Logger LOG = LoggerFactory
            .getLogger(UrlContainer.class);
    private static final long serialVersionUID = 1L;
    private final MyVaadinApplication app;
    private final CrawlerRepository repository;
    private final ArrayList<Object> propertyIds = new ArrayList<Object>();
    private static final String URL = "URL";
    private static final String DATE = "Date";
    private static final String HTTP_RESPONSE_CODE = "HTTP Response Code";
    private UrlRecords urlRecords;

    public UrlContainer(final MyVaadinApplication app) {
        this.app = app;
        ExecutorModelListener listener = new MyExecutorModelListener();
        Collection<TaskInfoBean> tasks = MyVaadinApplication.getModel()
                .getTasks(listener);
        this.repository = MyVaadinApplication.getRepository();
        propertyIds.add(URL);
        propertyIds.add(DATE);
        propertyIds.add(HTTP_RESPONSE_CODE);
        try {
            urlRecords = repository.listUrlRecords(200, 100);
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
    }

    @Override
    public Item getItem(final Object itemId) {
        synchronized (app) {
            UrlRecord urlRecord = (UrlRecord) itemId;
            if (urlRecord == null)
                return null;
            PropertysetItem item = new PropertysetItem();
            item.addItemProperty(URL,
                    new ObjectProperty<String>(urlRecord.getUrl(),
                            String.class, true));
            item.addItemProperty(DATE,
                    new ObjectProperty<Date>(urlRecord.getTimestamp(),
                            Date.class, true));
            item.addItemProperty(HTTP_RESPONSE_CODE,
                    new ObjectProperty<Integer>(
                            urlRecord.getHttpResponseCode(), Integer.class,
                            true));
            return item;
        }
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return propertyIds;
    }

    @Override
    public Collection<?> getItemIds() {
        synchronized (app) {
            return this.urlRecords;
        }
    }

    @Override
    public Property getContainerProperty(final Object itemId,
            final Object propertyId) {
        synchronized (app) {
            UrlRecord urlRecord = (UrlRecord) itemId;
            if (propertyId.equals(URL))
                return new ObjectProperty<String>(urlRecord.getUrl(),
                        String.class, true);
            if (propertyId.equals(DATE))
                return new ObjectProperty<Date>(urlRecord.getTimestamp(),
                        Date.class, true);
            if (propertyId.equals(HTTP_RESPONSE_CODE))
                return new ObjectProperty<Integer>(
                        urlRecord.getHttpResponseCode(), Integer.class, true);
            return null;
        }
    }

    @Override
    public Class<?> getType(final Object propertyId) {
        synchronized (app) {
            if (propertyId.equals(URL))
                return String.class;
            if (propertyId.equals(DATE))
                return Date.class;
            if (propertyId.equals(HTTP_RESPONSE_CODE))
                return Integer.class;
            return null;
        }
    }

    @Override
    public int size() {
        synchronized (app) {
            return urlRecords.size();
        }
    }

    @Override
    public boolean containsId(final Object itemId) {
        return true;
    }

    @Override
    public Item addItem(final Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeItem(final Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean addContainerProperty(final Object propertyId,
            final Class<?> type, final Object defaultValue)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeContainerProperty(final Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    private class MyExecutorModelListener implements ExecutorModelListener {

        @Override
        public void process(final ExecutorModelEvent event) {
            synchronized (app) {
                try {
                    UrlRecords urlRecords2 = repository
                            .listUrlRecords(200, 100);
                    urlRecords = urlRecords2;
                } catch (ConnectionException e) {
                    LOG.error("", e);
                }
            }
        }
    }
}
