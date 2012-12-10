package org.tokenizer.crawler.db;

import java.util.AbstractCollection;
import java.util.Date;
import java.util.Iterator;

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

    int hasNextCount = 0;

    @Override
    public boolean hasNext() {
        LOG.trace("hasNext() called... {}", ++hasNextCount);
        if (iterator.hasNext())
            return true;
        else {
            nextPage();
        }
        if (rows.isEmpty())
            return false;
        return true;
    }

    int nextCount = 0;

    @Override
    public UrlRecord next() {
        LOG.trace("next() called... {}", ++nextCount);
        Row<byte[], String> row = null;
        if (iterator.hasNext()) {
            row = iterator.next();
        } else {
            nextPage();
            if (rows.isEmpty())
                return null;
            row = iterator.next();
        }
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

    int nextPageCount = 0;

    private void nextPage() {
        LOG.trace("nextPage() called... {}", ++nextPageCount);
        try {
            this.rows = query.execute().getResult();
            this.iterator = rows.iterator();
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
    }
}
