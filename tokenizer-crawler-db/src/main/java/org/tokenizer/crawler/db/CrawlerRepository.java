package org.tokenizer.crawler.db;

import java.util.List;

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

    public WebpageRecord getWebpageRecord(byte[] digest)
            throws ConnectionException;

    public List<WebpageRecord> listWebpageRecords(String host, int start,
            int rows, boolean splitterProcessFinished)
            throws ConnectionException;

    public void insertIfNotExist(List<XmlRecord> xmlRecords)
            throws ConnectionException;

    public List<XmlRecord> listXmlRecords(String host, int start, int rows,
            boolean parserProcessFinished) throws ConnectionException;

    public void insertIfNotExists(MessageRecord messageRecord)
            throws ConnectionException;
}
