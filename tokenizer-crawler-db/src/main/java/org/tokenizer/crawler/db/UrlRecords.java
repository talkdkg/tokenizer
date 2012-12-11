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

public class UrlRecords extends AbstractCollection<UrlRecord> implements
        Iterable<UrlRecord>, Iterator<UrlRecord> {

    private static Logger LOG = LoggerFactory.getLogger(UrlRecords.class);
    private final Execution<Rows<byte[], String>> query;
    private Rows<byte[], String> rows = null;
    private Iterator<Row<byte[], String>> iterator = null;
    private int count = 0;
    private final boolean emptyPage = false;

    public UrlRecords(final int count,
            final Execution<Rows<byte[], String>> query) {
        this.query = query;
        this.count = count;
        nextPage();
    }

    @Override
    public Iterator<UrlRecord> iterator() {
        return this;
    }

    @Override
    public UrlRecord next() {
        Row<byte[], String> row = null;
        if (hasNext()) {
            row = iterator.next();
        } else
            throw new NoSuchElementException("No more elements!");
        ColumnList<String> columns = row.getColumns();
        byte[] digest = row.getKey();
        String url = columns.getStringValue("url", null);
        String host = columns.getStringValue("host", null);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted", null);
        Date timestamp = columns.getDateValue("timestamp", null);
        int fetchAttemptCounter = columns.getIntegerValue(
                "fetchAttemptCounter", null);
        int httpResponseCode = columns
                .getIntegerValue("httpResponseCode", null);
        byte[] webpageDigest = columns.getByteArrayValue("webpageDigest", null);
        UrlRecord urlRecord = new UrlRecord(digest, url, host, hostInverted,
                timestamp, fetchAttemptCounter, httpResponseCode, webpageDigest);
        return urlRecord;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean hasNext() {
        if (!rows.isEmpty() && iterator.hasNext())
            return true;
        else
            return nextPage();
    }

    private boolean nextPage() {
        try {
            this.rows = query.execute().getResult();
        } catch (ConnectionException e) {
            LOG.warn("", e);
            return false;
        }
        this.iterator = rows.iterator();
        return (!rows.isEmpty());
    }
}
