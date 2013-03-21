package org.tokenizer.crawler.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.nutch.net.URLFilter;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public interface CrawlerRepository {

    // public void insert(UrlRecord urlRecord) throws ConnectionException;
    void insertIfNotExists(UrlRecord urlRecord) throws ConnectionException;

    void update(UrlRecord urlRecord) throws ConnectionException;

    void delete(final UrlRecord urlRecord) throws ConnectionException;

    UrlRecord getUrlRecord(final byte[] digest) throws ConnectionException;

    void filter(final String host, final URLFilter urlFilter);

    List<UrlRecord> listUrlRecords(String host, int httpResponseCode,
            int maxResults) throws ConnectionException;

    List<UrlRecord> listUrlRecords(final byte[][] keys)
            throws ConnectionException;

    List<UrlRecord> listUrlRecordsByFetchAttemptCounter(final String host,
            final int httpResponseCode, final int maxResults)
            throws ConnectionException;

    List<UrlRecord> listUrlRecords(final String host,
            final int httpResponseCode, byte[] startRowkey, int count)
            throws ConnectionException;

    int countUrlRecords() throws ConnectionException;

    int countUrlRecords(final String host, final int httpResponseCode)
            throws ConnectionException;

    void insertIfNotExists(WebpageRecord webpageRecord)
            throws ConnectionException;

    void updateSplitAttemptCounterAndLinks(final WebpageRecord webpageRecord)
            throws ConnectionException;

    WebpageRecord getWebpageRecord(byte[] digest) throws ConnectionException;

    List<WebpageRecord> listWebpageRecords(final String host,
            final int splitAttemptCounter, final int maxResults)
            throws ConnectionException;

    int countWebpageRecords(final String host, final int splitAttemptCounter)
            throws ConnectionException;

    void insertIfNotExist(final XmlRecord xmlRecord) throws ConnectionException;

    void updateParseAttemptCounter(final XmlRecord xmlRecord)
            throws ConnectionException;

    List<XmlRecord> listXmlRecords(final String host,
            final int parseAttemptCounter, final int maxResults)
            throws ConnectionException;

    List<XmlRecord> listXmlRecords(final byte[][] keys)
            throws ConnectionException;

    List<XmlRecord> listXmlRecords(final ArrayList<byte[]> xmlLinks)
            throws ConnectionException;

    List<MessageRecord> listMessageRecords(final byte[][] keys)
            throws ConnectionException;

    List<MessageRecord> listMessageRecords(final ArrayList<byte[]> xmlLinks)
            throws ConnectionException;

    void insertIfNotExists(MessageRecord messageRecord)
            throws ConnectionException;

    int countXmlRecords(final String host, final int parseAttemptCounter)
            throws ConnectionException;

    List<byte[]> loadUrlRecordRowKeys(final String host,
            final int httpResponseCode) throws ConnectionException;

    void insert(final WeblogsRecord weblogsRecord) throws ConnectionException;

}
