package org.tokenizer.ui.hbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public class UrlContainer implements Container {
    private static final long serialVersionUID = 1L;
    private MyVaadinApplication app;
    private CrawlerHBaseRepository repository;
    private ArrayList<Object> propertyIds = new ArrayList<Object>();
    private static final String URL = "URL";
    private static final String DATE = "Date";
    private static final String HTTP_RESPONSE_CODE = "HTTP Response Code";
    private UrlCollection urlCollection;

    public UrlContainer(MyVaadinApplication app) {
        this.app = app;
        this.repository = app.getRepository();
        propertyIds.add(URL);
        propertyIds.add(DATE);
        propertyIds.add(HTTP_RESPONSE_CODE);
        urlCollection = new UrlCollection(app);
    }

    @Override
    public Item getItem(Object itemId) {
        synchronized (app) {
            String url = (String) itemId;
            UrlRecord urlRecord = repository.getUrlRecord(url);
            if (urlRecord == null)
                return null;
            PropertysetItem item = new PropertysetItem();
            item.addItemProperty(URL, new ObjectProperty<String>(url,
                    String.class, true));
            item.addItemProperty(DATE,
                    new ObjectProperty<Date>(urlRecord.getDate(), Date.class,
                            true));
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
            return this.urlCollection;
        }
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        synchronized (app) {
            String url = (String) itemId;
            if (propertyId.equals(URL))
                return new ObjectProperty<String>(url, String.class, true);
            UrlRecord urlRecord = repository.getUrlRecord(url);
            if (urlRecord == null)
                return null;
            if (propertyId.equals(DATE))
                return new ObjectProperty<Date>(urlRecord.getDate(),
                        Date.class, true);
            if (propertyId.equals(HTTP_RESPONSE_CODE))
                return new ObjectProperty<Integer>(
                        urlRecord.getHttpResponseCode(), Integer.class, true);
            return null;
        }
    }

    @Override
    public Class<?> getType(Object propertyId) {
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
            return urlCollection.size();
        }
    }

    @Override
    public boolean containsId(Object itemId) {
        synchronized (app) {
            String url = (String) itemId;
            return repository.containsUrlRecord(url);
        }
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not implemented");
    }
}
