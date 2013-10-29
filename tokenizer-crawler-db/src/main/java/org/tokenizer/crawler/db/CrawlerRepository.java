/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.crawler.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.nutch.net.URLFilter;
import org.tokenizer.crawler.db.model.FetchedResultRecord;
import org.tokenizer.crawler.db.model.HostRecord;
import org.tokenizer.crawler.db.model.MessageRecord;
import org.tokenizer.crawler.db.model.TimestampUrlIDX;
import org.tokenizer.crawler.db.model.UrlHeadRecord;
import org.tokenizer.crawler.db.model.UrlRecord;
import org.tokenizer.crawler.db.model.UrlSitemapIDX;
import org.tokenizer.crawler.db.model.WeblogRecord;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.crawler.db.model.XmlRecord;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public interface CrawlerRepository {

    void insert(final UrlRecord urlRecord) throws ConnectionException;

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
    UrlRecord retrieveUrlRecord(final String url) throws ConnectionException;

    // CF_URL_SITEMAP_IDX

    void insert(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    void delete(UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    void insertIfNotExists(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    UrlSitemapIDX load(UrlSitemapIDX urlSitemapIDX) throws ConnectionException;

    // CF_TIMESTAMP_URL_IDX
    void insert(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException;

    void delete(TimestampUrlIDX timestampUrlIDX) throws ConnectionException;

    void insertIfNotExists(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException;

    TimestampUrlIDX load(TimestampUrlIDX timestampUrlIDX) throws ConnectionException;

    List<TimestampUrlIDX> loadTimestampUrlIDX(String host) throws ConnectionException;

    // ////////////////
    // WebpageRecord //
    // ////////////////

    WebpageRecord retrieveWebpageRecord(final byte[] digest) throws ConnectionException;

    void incrementExtractOutlinksAttemptCounter(final WebpageRecord webpageRecord) throws ConnectionException;

    void deleteWebpageRecord(final byte[] digest) throws ConnectionException;

    List<WebpageRecord> listWebpageRecordsByExtractOutlinksAttemptCounter(final String host,
            final int extractOutlinksAttemptCounter, final int maxResults) throws ConnectionException;

    void insertIfNotExists(WebpageRecord webpageRecord) throws ConnectionException;

    void updateSplitAttemptCounterAndLinks(final WebpageRecord webpageRecord) throws ConnectionException;


    List<WebpageRecord> listWebpageRecords(final String host, final int splitAttemptCounter, final int maxResults)
            throws ConnectionException;

    // MessageRecord
    MessageRecord retrieveMessageRecord(final byte[] digest) throws ConnectionException;
    
    void insertMessageUrlIDX(final byte[] digest, final String url) throws ConnectionException;

    List<String> listUrlByMessageDigest(final byte[] digest)  throws ConnectionException;
}
