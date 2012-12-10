package org.tokenizer.crawler.db;

import java.util.AbstractCollection;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.Execution;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;

public class WebpageRecords extends AbstractCollection<WebpageRecord> implements
        Iterable<WebpageRecord>, Iterator<WebpageRecord> {

    private static Logger LOG = LoggerFactory.getLogger(WebpageRecords.class);
    private final Execution<Rows<byte[], String>> query;
    private Rows<byte[], String> rows = null;
    private Iterator<Row<byte[], String>> iterator = null;
    private int count = 0;

    public WebpageRecords(final int count,
            final Execution<Rows<byte[], String>> query) {
        this.query = query;
        this.count = count;
        nextPage();
    }

    @Override
    public Iterator<WebpageRecord> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        if (iterator.hasNext())
            return true;
        else {
            nextPage();
        }
        if (rows.isEmpty())
            return false;
        return true;
    }

    @Override
    public WebpageRecord next() {
        Row<byte[], String> row = null;
        if (iterator.hasNext()) {
            row = iterator.next();
        } else
            throw new NoSuchElementException("No more elements!");
        ColumnList<String> columns = row.getColumns();
        byte[] digest = row.getKey();
        String url = columns.getStringValue("url", null);
        String host = columns.getStringValue("host", null);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted", null);
        Date timestamp = columns.getDateValue("timestamp", null);
        String charset = columns.getStringValue("charset", null);
        byte[] content = columns.getByteArrayValue("content", null);
        int splitAttemptCounter = columns.getIntegerValue(
                "splitAttemptCounter", null);
        WebpageRecord webpageRecord = new WebpageRecord(digest, url, host,
                hostInverted, timestamp, charset, content, splitAttemptCounter);
        return webpageRecord;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int size() {
        return this.count;
    }

    private void nextPage() {
        try {
            this.rows = query.execute().getResult();
            this.iterator = rows.iterator();
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
    }
}
