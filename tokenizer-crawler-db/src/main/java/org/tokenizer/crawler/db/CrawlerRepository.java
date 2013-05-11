package org.tokenizer.crawler.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.nutch.net.URLFilter;
import org.tokenizer.crawler.db.model.FetchedResultRecord;
import org.tokenizer.crawler.db.model.HostRecord;
import org.tokenizer.crawler.db.model.UrlHeadRecord;
import org.tokenizer.crawler.db.model.UrlSitemapIDX;
import org.tokenizer.crawler.db.model.WeblogRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;

public interface CrawlerRepository {

    // public void insert(UrlRecord urlRecord) throws ConnectionException;
    void insertIfNotExists(UrlRecord urlRecord) throws ConnectionException;

    void update(UrlRecord urlRecord) throws ConnectionException;

    void delete(final UrlRecord urlRecord) throws ConnectionException;

    void filter(final String host, final URLFilter urlFilter);

    List<UrlRecord> listUrlRecords(String host, int httpResponseCode, int maxResults) throws ConnectionException;

    List<UrlRecord> listUrlRecords(final String[] keys) throws ConnectionException;

    List<UrlRecord> listUrlRecordsByFetchAttemptCounter(final String host, final int httpResponseCode,
            final int maxResults) throws ConnectionException;

    List<UrlRecord> listUrlRecords(final String host, final int httpResponseCode, byte[] startRowkey, int count)
            throws ConnectionException;

    int countUrlRecords() throws ConnectionException;

    int countUrlRecords(final String host, final int httpResponseCode) throws ConnectionException;

    void insertIfNotExists(WebpageRecord webpageRecord) throws ConnectionException;

    void updateSplitAttemptCounterAndLinks(final WebpageRecord webpageRecord) throws ConnectionException;

    WebpageRecord getWebpageRecord(byte[] digest) throws ConnectionException;

    List<WebpageRecord> listWebpageRecords(final String host, final int splitAttemptCounter, final int maxResults)
            throws ConnectionException;

    int countWebpageRecords(final String host, final int splitAttemptCounter) throws ConnectionException;

    void insertIfNotExist(final XmlRecord xmlRecord) throws ConnectionException;

    void updateParseAttemptCounter(final XmlRecord xmlRecord) throws ConnectionException;

    List<XmlRecord> listXmlRecords(final String host, final int parseAttemptCounter, final int maxResults)
            throws ConnectionException;

    List<XmlRecord> listXmlRecords(final byte[][] keys) throws ConnectionException;

    List<XmlRecord> listXmlRecords(final ArrayList<byte[]> xmlLinks) throws ConnectionException;

    List<MessageRecord> listMessageRecords(final byte[][] keys) throws ConnectionException;

    List<MessageRecord> listMessageRecords(final ArrayList<byte[]> xmlLinks) throws ConnectionException;

    void insertIfNotExists(MessageRecord messageRecord) throws ConnectionException;

    int countXmlRecords(final String host, final int parseAttemptCounter) throws ConnectionException;

    List<byte[]> loadUrlRecordRowKeys(final String host, final int httpResponseCode) throws ConnectionException;

    void insert(final WeblogRecord weblogsRecord) throws ConnectionException;

    WeblogRecord getLastWeblogRecord() throws ConnectionException;

    void insert(final FetchedResultRecord fetchedResultRecord) throws ConnectionException;

    void insert(final HostRecord hostRecord) throws ConnectionException;

    void insertIfNotExists(final HostRecord hostRecord) throws ConnectionException;

    Collection<String> listTLDs() throws ConnectionException;

    int countHosts(String tld);

    List<HostRecord> listHostRecords(String tld, final int startIndex, final int count);

    List<FetchedResultRecord> listFetchedResultRecords(String host);

    void insertIfNotExists(final UrlHeadRecord urlHeadRecord) throws ConnectionException;

    // /////////////////////////////////////////////////////////
    // IMPROTANT
    // Since 2013.05.11 we decided to use only single-table CRUD at "service" layer; index updates will happen directly
    // at "engine" layer, possibly will be refactored later. Let's stick with simple design.
    // /////////////////////////////////////////////////////////

    // URL Records
    UrlRecord getUrlRecord(final String key) throws ConnectionException;

    // CF_URL_SITEMAP_IDX
    UrlSitemapIDX load(UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    void insert(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    void delete(UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    void insertIfNotExists(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

}
