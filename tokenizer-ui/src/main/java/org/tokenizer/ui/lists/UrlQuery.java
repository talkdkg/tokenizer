package org.tokenizer.ui.lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.StringPool;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.ui.MyVaadinApplication;
import org.vaadin.addons.lazyquerycontainer.Query;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

public class UrlQuery implements Query {

    private static final Logger LOG = LoggerFactory.getLogger(UrlQuery.class);
    public static final String URL = "URL";
    public static final String DATE = "Date";
    public static final String HTTP_RESPONSE_CODE = "HTTP Response Code";
    public static final String URL_RECORD = "URL_RECORD";
    public static final Date DEFAULT_DATE = new Date(0);
    private final CrawlerRepository crawlerRepository;
    private byte[] rowKeyBytes;
    private final MyVaadinApplication app;

    public UrlQuery(final MyVaadinApplication app) {
        this.app = app;
        this.crawlerRepository = MyVaadinApplication.getRepository();
        loadRowKeys(app.getSelectedHost(), app.getSelectedHttpResponseCode());
        // loadRowKeys("www.amazon.com", 200);
    }

    @Override
    public Item constructItem() {
        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(URL, new ObjectProperty<String>(StringPool.EMPTY,
                String.class, true));
        item.addItemProperty(DATE, new ObjectProperty<Date>(DEFAULT_DATE,
                Date.class, true));
        item.addItemProperty(HTTP_RESPONSE_CODE, new ObjectProperty<Integer>(0,
                Integer.class, true));
        return item;
    }

    @Override
    public boolean deleteAllItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * Load batch of items.
     * 
     * @param startIndex
     *            Starting index of the item list.
     * @param count
     *            Count of the items to be retrieved.
     * @return List of items.
     */
    @Override
    public List<Item> loadItems(final int startIndex, final int count) {
        LOG.debug("loadItems() called... startIndex: {}, count: {}",
                startIndex, count);
        synchronized (app) {
            byte[][] rowKeys = new byte[count][];
            for (int i = 0; i < count; i++) {
                // rowKeys[i] = new byte[16];
                rowKeys[i] = Arrays.copyOfRange(rowKeyBytes,
                        (startIndex + i) * 16, (startIndex + i) * 16 + 16);
            }
            List<Item> items = new ArrayList<Item>();
            List<UrlRecord> urlRecords;
            try {
                urlRecords = crawlerRepository.listUrlRecords(rowKeys);
            } catch (ConnectionException e) {
                LOG.error("", e);
                return items;
            }
            for (UrlRecord urlRecord : urlRecords) {
                items.add(toItem(urlRecord));
            }
            LOG.debug("{} items found", items.size());
            return items;
        }
    }

    @Override
    public void saveItems(final List<Item> arg0, final List<Item> arg1,
            final List<Item> arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        synchronized (app) {
            return rowKeyBytes.length / 16;
        }
    }

    private void loadRowKeys(final String host, final int httpResponseCode) {
        LOG.debug("Loading raw keys...");
        List<byte[]> rowKeyList;
        try {
            rowKeyList = crawlerRepository.loadUrlRecordRowKeys(host,
                    httpResponseCode);
        } catch (Exception e) {
            LOG.error("", e);
            return;
        }
        byte[] b = new byte[16 * rowKeyList.size()];
        int i = 0;
        for (byte[] rowKey : rowKeyList) {
            if (rowKey.length > 16)
                throw new RuntimeException(
                        "not implemented: row key size is higher than 16 bytes");
            for (int offset = 0; offset < 16; offset++) {
                b[i * 16 + offset] = rowKey[offset];
            }
            i++;
        }
        rowKeyBytes = b;
    }

    private Item toItem(final UrlRecord urlRecord) {
        PropertysetItem item = new PropertysetItem();
        ObjectProperty<UrlRecord> urlRecordProperty = new ObjectProperty<UrlRecord>(
                urlRecord);
        item.addItemProperty(URL_RECORD, urlRecordProperty);
        item.addItemProperty(URL, new ObjectProperty<String>(
                urlRecord.getUrl(), String.class, true));
        item.addItemProperty(DATE,
                new ObjectProperty<Date>(urlRecord.getTimestamp(), Date.class,
                        true));
        item.addItemProperty(HTTP_RESPONSE_CODE, new ObjectProperty<Integer>(
                urlRecord.getHttpResponseCode(), Integer.class, true));
        return item;
    }
}
