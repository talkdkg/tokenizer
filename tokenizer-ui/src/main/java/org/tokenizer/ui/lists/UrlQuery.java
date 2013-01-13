package org.tokenizer.ui.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.StringPool;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.executor.engine.MetricsCache;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
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
    // ASSUMING that "count" is always 100; algo is oprimized for 100s frames
    private int startIndex;
    private final int PAGE_SIZE = 100;
    // array to store each 100s RowKey:
    private final List<byte[]> rowKeys = new ArrayList<byte[]>(1024);
    // very last RowKey on a last page:
    private byte[] lastRowKey;
    private final MyVaadinApplication app;

    public UrlQuery(final MyVaadinApplication app) {
        this.app = app;
        this.crawlerRepository = MyVaadinApplication.getRepository();
        // loadRowKeys(app.getSelectedHost(),
        // app.getSelectedHttpResponseCode());
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
        List<Item> items = new ArrayList<Item>();
        try {
            LOG.debug("startIndex: {}, count: {}", startIndex, count);
            int page = startIndex / PAGE_SIZE;
            // if we have "start" key in a cache:
            if (page < rowKeys.size()) {
                List<UrlRecord> urlRecords = crawlerRepository.listUrlRecords(
                        app.getSelectedHost(),
                        app.getSelectedHttpResponseCode(), rowKeys.get(page),
                        count);
                for (UrlRecord urlRecord : urlRecords) {
                    items.add(toItem(urlRecord));
                }
                return items;
            } else if (page == 0 && page == rowKeys.size()) {
                List<UrlRecord> urlRecords = crawlerRepository.listUrlRecords(
                        app.getSelectedHost(),
                        app.getSelectedHttpResponseCode(), count);
                for (int i = 0; i < urlRecords.size(); i++) {
                    if (i == 0) {
                        rowKeys.add(urlRecords.get(i).getDigest());
                    }
                    if (i == count - 1) {
                        lastRowKey = urlRecords.get(i).getDigest();
                    }
                    items.add(toItem(urlRecords.get(i)));
                }
                return items;
            } else if (page != 0 && page == rowKeys.size()) {
                List<UrlRecord> urlRecords = crawlerRepository.listUrlRecords(
                        app.getSelectedHost(),
                        app.getSelectedHttpResponseCode(), lastRowKey,
                        count + 1);
                for (int i = 0; i < urlRecords.size(); i++) {
                    if (i == 0) {
                        continue;
                    }
                    if (i == 1) {
                        rowKeys.add(urlRecords.get(i).getDigest());
                    }
                    if (i == count) {
                        lastRowKey = urlRecords.get(i).getDigest();
                    }
                    items.add(toItem(urlRecords.get(i)));
                }
                return items;
            } else {
                // count can be less than PAGE_SIZE if it is last page
                while (startIndex > PAGE_SIZE * rowKeys.size()) {
                    LOG.debug("Recursive call.................");
                    items = loadItems(PAGE_SIZE * rowKeys.size(), PAGE_SIZE);
                }
                return items;
            }
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
        return items;
    }

    @Override
    public void saveItems(final List<Item> arg0, final List<Item> arg1,
            final List<Item> arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        Collection<TaskInfoBean> tasks = MyVaadinApplication.getModel()
                .getTasks();
        String selectedHost = app.getSelectedHost();
        int size = 0;
        if (selectedHost == null)
            return 0;
        for (TaskInfoBean task : tasks) {
            if (task.getTaskConfiguration()
                    .getImplementationName()
                    .equals(ClassicRobotTaskConfiguration.class.getSimpleName())) {
                String host = ((ClassicRobotTaskConfiguration) task
                        .getTaskConfiguration()).getHost();
                if (selectedHost.equals(host)) {
                    Long o = task.getCounters().get(MetricsCache.URL_OK_KEY);
                    if (o != null) {
                        size = o.intValue();
                    }
                }
            }
        }
        LOG.debug("size: {}", size);
        return size;
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
