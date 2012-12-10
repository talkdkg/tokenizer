package org.tokenizer.crawler.db;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public interface CrawlerRepository {

    // public void insert(UrlRecord urlRecord) throws ConnectionException;
    public void insertIfNotExists(UrlRecord urlRecord)
            throws ConnectionException;

    public void update(UrlRecord urlRecord) throws ConnectionException;

    public UrlRecords listUrlRecords(int defaultPageSize)
            throws ConnectionException;

    public UrlRecords listUrlRecords(int httpResponseCode, int defaultPageSize)
            throws ConnectionException;

    public UrlRecords listUrlRecords(String host, int defaultPageSize)
            throws ConnectionException;

    public UrlRecords listUrlRecords(String host, int httpResponseCode,
            int defaultPageSize) throws ConnectionException;

    public int countUrlRecords() throws ConnectionException;

    public int countUrlRecords(int httpResponseCode) throws ConnectionException;

    public void insertIfNotExists(WebpageRecord webpageRecord)
            throws ConnectionException;

    public void updateSplitAttemptCounter(final WebpageRecord webpageRecord)
            throws ConnectionException;

    public WebpageRecord getWebpageRecord(byte[] digest)
            throws ConnectionException;

    public WebpageRecords listWebpageRecords(final String host,
            final int splitAttemptCounter, final int defaultPageSize)
            throws ConnectionException;

    public int countWebpageRecords(final String host,
            final int splitAttemptCounter) throws ConnectionException;

    public void insertIfNotExist(final XmlRecord xmlRecord)
            throws ConnectionException;

    public void updateParseAttemptCounter(final XmlRecord xmlRecord)
            throws ConnectionException;

    public XmlRecords listXmlRecords(final String host,
            final int parseAttemptCounter, final int defaultPageSize)
            throws ConnectionException;

    public void insertIfNotExists(MessageRecord messageRecord)
            throws ConnectionException;

    public int countXmlRecords(final String host, final int parseAttemptCounter)
            throws ConnectionException;
}
