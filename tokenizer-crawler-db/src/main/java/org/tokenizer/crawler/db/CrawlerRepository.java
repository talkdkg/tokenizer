package org.tokenizer.crawler.db;

import java.util.List;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public interface CrawlerRepository {

    // public void insert(UrlRecord urlRecord) throws ConnectionException;
    void insertIfNotExists(UrlRecord urlRecord) throws ConnectionException;

    void update(UrlRecord urlRecord) throws ConnectionException;

    UrlRecords listUrlRecords(int defaultPageSize) throws ConnectionException;

    UrlRecords listUrlRecords(int httpResponseCode, int defaultPageSize)
            throws ConnectionException;

    UrlRecords listUrlRecords(String host, int defaultPageSize)
            throws ConnectionException;

    UrlRecords listUrlRecords(String host, int httpResponseCode,
            int defaultPageSize) throws ConnectionException;

    List<UrlRecord> listUrlRecords(final byte[][] keys)
            throws ConnectionException;

    int countUrlRecords() throws ConnectionException;

    int countUrlRecords(final int httpResponseCode) throws ConnectionException;

    int countUrlRecords(final String host, final int httpResponseCode)
            throws ConnectionException;

    void insertIfNotExists(WebpageRecord webpageRecord)
            throws ConnectionException;

    void updateSplitAttemptCounter(final WebpageRecord webpageRecord)
            throws ConnectionException;

    WebpageRecord getWebpageRecord(byte[] digest) throws ConnectionException;

    WebpageRecords listWebpageRecords(final String host,
            final int splitAttemptCounter, final int defaultPageSize)
            throws ConnectionException;

    int countWebpageRecords(final String host, final int splitAttemptCounter)
            throws ConnectionException;

    void insertIfNotExist(final XmlRecord xmlRecord) throws ConnectionException;

    void updateParseAttemptCounter(final XmlRecord xmlRecord)
            throws ConnectionException;

    XmlRecords listXmlRecords(final String host, final int parseAttemptCounter,
            final int defaultPageSize) throws ConnectionException;

    void insertIfNotExists(MessageRecord messageRecord)
            throws ConnectionException;

    int countXmlRecords(final String host, final int parseAttemptCounter)
            throws ConnectionException;

    List<byte[]> loadUrlRecordRowKeys(final String host,
            final int httpResponseCode) throws ConnectionException;
}
