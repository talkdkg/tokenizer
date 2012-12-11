package org.tokenizer.crawler.db;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.Execution;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;

public class XmlRecords extends AbstractCollection<XmlRecord> implements
        Iterable<XmlRecord>, Iterator<XmlRecord> {

    private static Logger LOG = LoggerFactory.getLogger(XmlRecords.class);
    private final Execution<Rows<byte[], String>> query;
    private Rows<byte[], String> rows = null;
    private Iterator<Row<byte[], String>> iterator = null;
    private int count = 0;

    public XmlRecords(final int count,
            final Execution<Rows<byte[], String>> query) {
        this.query = query;
        this.count = count;
        nextPage();
    }

    @Override
    public Iterator<XmlRecord> iterator() {
        return this;
    }

    @Override
    public XmlRecord next() {
        Row<byte[], String> row = null;
        if (iterator.hasNext()) {
            row = iterator.next();
        } else
            throw new NoSuchElementException("No more elements!");
        ColumnList<String> columns = row.getColumns();
        byte[] digest = row.getKey();
        String host = columns.getStringValue("host", null);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted", null);
        byte[] content = columns.getByteArrayValue("content", null);
        int parseAttemptCounter = columns.getIntegerValue(
                "parseAttemptCounter", 0);
        XmlRecord xmlRecord = new XmlRecord(digest, host, hostInverted,
                content, parseAttemptCounter);
        return xmlRecord;
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
