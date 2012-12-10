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

public class MessageRecords extends AbstractCollection<MessageRecord> implements
        Iterable<MessageRecord>, Iterator<MessageRecord> {

    private static Logger LOG = LoggerFactory.getLogger(MessageRecords.class);
    private final Execution<Rows<byte[], String>> query;
    private Rows<byte[], String> rows = null;
    private Iterator<Row<byte[], String>> iterator = null;
    private int count = 0;

    public MessageRecords(final int count,
            final Execution<Rows<byte[], String>> query) {
        this.query = query;
        this.count = count;
        nextPage();
    }

    @Override
    public Iterator<MessageRecord> iterator() {
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
    public MessageRecord next() {
        Row<byte[], String> row = null;
        if (iterator.hasNext()) {
            row = iterator.next();
        } else
            throw new NoSuchElementException("No more elements!");
        ColumnList<String> columns = row.getColumns();
        byte[] digest = row.getKey();
        String host = columns
                .getStringValue("host", DefaultValues.EMPTY_STRING);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted",
                DefaultValues.EMPTY_ARRAY);
        String topic = columns.getStringValue("topic",
                DefaultValues.EMPTY_STRING);
        String date = columns
                .getStringValue("date", DefaultValues.EMPTY_STRING);
        String author = columns.getStringValue("author",
                DefaultValues.EMPTY_STRING);
        String age = columns.getStringValue("age", DefaultValues.EMPTY_STRING);
        String sex = columns.getStringValue("sex", DefaultValues.EMPTY_STRING);
        String title = columns.getStringValue("title",
                DefaultValues.EMPTY_STRING);
        String content = columns.getStringValue("content",
                DefaultValues.EMPTY_STRING);
        String userRating = columns.getStringValue("userRating",
                DefaultValues.EMPTY_STRING);
        MessageRecord messageRecord = new MessageRecord(digest, host,
                hostInverted, topic, date, author, age, sex, title, content,
                userRating);
        return messageRecord;
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
