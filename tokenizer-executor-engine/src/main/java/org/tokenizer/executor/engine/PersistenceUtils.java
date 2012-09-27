/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.executor.engine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.joda.time.DateTime;
import org.lilyproject.repository.api.Blob;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordException;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.RecordNotFoundException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.SimpleHttpClient;
import org.tokenizer.core.lily.LilyUtils;
import org.tokenizer.core.util.SHA256;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.robots.BaseRobotRules;

public class PersistenceUtils {
  
  private static final Logger LOG = LoggerFactory
      .getLogger(PersistenceUtils.class);
  
  private static final String ROBOT_RULES_RESTRICTION = "ROBOT_RULES_RESTRICTION";
  
  private final static long SOLR_UPDATE_DELAY = 24 * 3600 * 1000L;
  
  private static final List<QName> EMPTY_LIST = new ArrayList<QName>();
  
  /**
   * Namespace "raw.http" reflects <br/>
   * 1. RAW <br/>
   * 2. CA Protocol is HTTP <br/>
   * 
   */
  public static final String RAW_HTTP_NAMESPACE = "raw.http";
  
  /**
   * Name reflects - "url" means traditional single-page-per-URL approach -
   * primary key is constructed upon URL (contr-sample: "ping")
   */
  public static final QName RAW_RECORD_TYPE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "url");
  
  public static final QName FETCHED_RESULT_TLD_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "tld");
  public static final QName FETCHED_RESULT_BASE_URL_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "baseUrl");
  public static final QName FETCHED_RESULT_FETCHED_URL_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "fetchedUrl");
  public static final QName FETCHED_RESULT_FETCH_TIME_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "timestamp");
  public static final QName FETCHED_RESULT_CONTENT_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "content");
  public static final QName FETCHED_RESULT_CONTENT_TYPE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "contentType");
  public static final QName FETCHED_RESULT_LANGUAGE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "language");
  public static final QName FETCHED_RESULT_CHARSET_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "charset");
  public static final QName FETCHED_RESULT_RESPONSE_CODE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "responseCode");
  public static final QName FETCHED_RESULT_RESPONSE_MESSAGE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "responseMessage");
  
  // for RSS feeds
  public static final QName RSS_TITLE_QNAME = new QName(RAW_HTTP_NAMESPACE,
      "rssTitle");
  public static final QName RSS_DESCRIPTION_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "rssDescription");
  public static final QName RSS_PUBLISHED_DATE_QNAME = new QName(
      RAW_HTTP_NAMESPACE, "rssPublishedDate");
  public static final QName RSS_AUTHOR_QNAME = new QName(RAW_HTTP_NAMESPACE,
      "rssAuthor");
  public static final QName RSS_CATEGORY_QNAME = new QName(RAW_HTTP_NAMESPACE,
      "rssCategory");
  
  /**
   * Solr is required to store URL and Timestamp fields, URL is "stored" and
   * Timestamp is "indexed", for better performance and failover (we might have
   * URL in Solr, and empty Lily server) <br/>
   * 
   * use case - we don't have record in Lily, but we have it in Solr
   * 
   * @param doc
   * @param repository
   * @param rules
   * @param metricsCache
   * @param simpleHttpClient
   * @throws InterruptedException
   */
  public static FetchedResult fetch(SolrDocument doc, Repository repository,
      BaseRobotRules baseRobotRules, MetricsCache metricsCache,
      SimpleHttpClient simpleHttpClient) throws InterruptedException {
    
    metricsCache.increment(MetricsCache.URL_TOTAL_KEY);
    
    Record record = refreshRecord(doc, repository);
    if (record == null) {
      metricsCache.increment(MetricsCache.OTHER_ERRORS);
      return null;
    }
    
    // to be safe, double check that you are not recrawling page not yet updated
    // in SOLR; use default 24 hours
    // (in worst case scenario Solr will be updated in 24 hours)
    Long timestamp = (Long) record.getField(FETCHED_RESULT_FETCH_TIME_QNAME);
    if (timestamp + SOLR_UPDATE_DELAY > System.currentTimeMillis()) {
      metricsCache.increment(MetricsCache.NOT_INDEXED_YET);
      return null;
    }
    
    if (!checkRobotRules(record, repository, baseRobotRules, metricsCache)) return null;
    
    FetchedResult fetchedResult = fetch(record, repository, metricsCache,
        simpleHttpClient);
    if (fetchedResult == null) return null;
    
    update(fetchedResult, repository, metricsCache);
    
    return fetchedResult;
    
  }
  
  public static void update(FetchedResult fetchedResult, Repository repository,
      MetricsCache metricsCache) throws InterruptedException {
    
    long start = System.currentTimeMillis();
    Record record = createRecord(fetchedResult, repository, false);
    if (record == null) return;
    
    try {
      record = repository.update(record);
      metricsCache.increment(MetricsCache.LILY_UPDATES_COUNT);
      metricsCache.increment(MetricsCache.LILY_UPDATES_TIME,
          System.currentTimeMillis() - start);
    } catch (RepositoryException e) {
      LOG.error("", e);
    }
    
  }
  
  public static boolean checkRobotRules(Record record, Repository repository,
      BaseRobotRules baseRobotRules, MetricsCache metricsCache)
      throws InterruptedException {
    String url = (String) record.getField(FETCHED_RESULT_BASE_URL_QNAME);
    if (!baseRobotRules.isAllowed(url)) {
      record.setField(FETCHED_RESULT_FETCH_TIME_QNAME,
          System.currentTimeMillis());
      record.setField(FETCHED_RESULT_RESPONSE_CODE_QNAME, -1);
      record.setField(FETCHED_RESULT_RESPONSE_MESSAGE_QNAME,
          ROBOT_RULES_RESTRICTION);
      try {
        record = repository.update(record);
        metricsCache.increment(MetricsCache.URL_ROBOTS_KEY);
      } catch (RepositoryException e) {
        LOG.error("", e);
      }
      return false;
    }
    return true;
  }
  
  public static FetchedResult fetch(Record record, Repository repository,
      MetricsCache metricsCache, SimpleHttpClient simpleHttpClient)
      throws InterruptedException {
    
    String url = (String) record.getField(FETCHED_RESULT_BASE_URL_QNAME);
    FetchedResult fetchedResult = null;
    long start = System.currentTimeMillis();
    
    // TODO: something missing here during refactoring, need to verify...
    try {
      fetchedResult = simpleHttpClient.get(url);
    } catch (HttpFetchException e) {
      record.setField(LilyUtils.FETCHED_RESULT_RESPONSE_CODE_QNAME,
          ((HttpFetchException) e).getHttpStatus());
    } catch (BaseFetchException e) {
      // TODO: [CY] - I have no clue what this is about... need to debug
      if (e.getMessage().contains("Aborted due to INTERRUPTED")) {
        throw new InterruptedException("Aborted...");
      }
      
      record.setField(LilyUtils.FETCHED_RESULT_RESPONSE_CODE_QNAME, -1);
      record.setField(LilyUtils.FETCHED_RESULT_RESPONSE_MESSAGE_QNAME,
          e.getMessage());
      record.setField(LilyUtils.FETCHED_RESULT_FETCH_TIME_QNAME,
          System.currentTimeMillis());
      
      metricsCache.increment(MetricsCache.URL_ERROR_KEY);
    }
    
    if (fetchedResult.getHttpStatus() >= 200
        && fetchedResult.getHttpStatus() < 300) {
      metricsCache.increment(MetricsCache.TOTAL_RESPONSE_TIME_KEY,
          System.currentTimeMillis() - start);
      metricsCache.increment(MetricsCache.URL_OK_KEY);
    } else {
      metricsCache.increment(MetricsCache.OTHER_ERRORS);
    }
    return fetchedResult;
    
  }
  
  public static void injectIfNotExists(String url, Repository repository,
      MetricsCache metricsCache) throws InterruptedException {
    Record record = createRecord(url, repository, false);
    if (record == null) return;
    try {
      repository.read(record.getId(), EMPTY_LIST);
    } catch (RepositoryException e) {
      if (e instanceof RecordNotFoundException) {
        try {
          record = repository.create(record);
          metricsCache.increment(MetricsCache.LILY_INJECTS_COUNT);
        } catch (RecordException e1) {
          LOG.error("", e1);
        } catch (RepositoryException e1) {
          LOG.error("", e1);
        }
      } else {
        LOG.error("", e);
      }
    }
  }
  
  public static void injectIfNotExists(SyndEntry entry, Repository repository,
      MetricsCache metricsCache) throws InterruptedException {
    Record record = createRecord(entry, repository);
    if (record == null) return;
    try {
      record = repository.read(record.getId(), new ArrayList<QName>());
    } catch (RepositoryException e) {
      if (e instanceof RecordNotFoundException) {
        try {
          record = repository.create(record);
          metricsCache.increment(MetricsCache.LILY_INJECTS_COUNT);
        } catch (RepositoryException e1) {
          LOG.error("", e);
        }
      } else {
        LOG.error("", e);
      }
    }
  }
  
  private static Record createRecord(SyndEntry entry, Repository repository)
      throws InterruptedException {
    String url = entry.getLink().trim();
    Record record = createRecord(url, repository, false);
    if (record == null) return null;
    record.setField(RSS_TITLE_QNAME, entry.getTitle().trim());
    if (entry.getDescription() != null) record.setField(RSS_DESCRIPTION_QNAME,
        entry.getDescription().getValue().trim());
    record.setField(RSS_PUBLISHED_DATE_QNAME,
        new DateTime(entry.getPublishedDate()));
    record.setField(RSS_AUTHOR_QNAME, entry.getAuthor().trim());
    @SuppressWarnings("unchecked")
    List<SyndCategory> categories = entry.getCategories();
    List<String> cats = new ArrayList<String>();
    for (SyndCategory category : categories) {
      cats.add(category.getName().trim());
    }
    record.setField(RSS_CATEGORY_QNAME, cats);
    return record;
  }
  
  private static Record createRecord(FetchedResult fetchedResult,
      Repository repository, boolean appendTimestamp)
      throws InterruptedException {
    
    Record record = createRecord(fetchedResult.getBaseUrl(), repository,
        appendTimestamp);
    if (record == null) return null;
    
    if (fetchedResult.getTargetUri() != null) record.setField(
        FETCHED_RESULT_FETCHED_URL_QNAME, fetchedResult.getTargetUri()
            .toString());
    
    record
        .setField(FETCHED_RESULT_FETCH_TIME_QNAME, System.currentTimeMillis());
    record.setField(FETCHED_RESULT_RESPONSE_CODE_QNAME,
        fetchedResult.getHttpStatus());
    record.setField(FETCHED_RESULT_RESPONSE_MESSAGE_QNAME,
        fetchedResult.getReasonPhrase());
    record.setField(FETCHED_RESULT_CONTENT_TYPE_QNAME,
        fetchedResult.getContentType());
    
    record.setField(FETCHED_RESULT_LANGUAGE_QNAME, fetchedResult.getLanguage());
    record.setField(FETCHED_RESULT_CHARSET_QNAME, fetchedResult.getCharset());
    
    if (fetchedResult.getContent() != null) {
      Blob blob = new Blob(fetchedResult.getContentType(),
          (long) fetchedResult.getContent().length, null);
      OutputStream os = null;
      try {
        os = repository.getOutputStream(blob);
        os.write(fetchedResult.getContent());
        record.setField(FETCHED_RESULT_CONTENT_QNAME, blob);
      } catch (RepositoryException e) {
        LOG.error("", e);
        return null;
      } catch (IOException e) {
        LOG.error("", e);
        return null;
      } finally {
        if (os != null) try {
          os.close();
        } catch (IOException e) {
          LOG.error("", e);
          return null;
        }
      }
      
    }
    return record;
  }
  
  private static Record createRecord(String url, Repository repository,
      boolean appendTimestamp) throws InterruptedException {
    Record record = null;
    RecordId recordId = repository.getIdGenerator().newRecordId(
        SHA256.SHA256(appendTimestamp ? url : url + System.nanoTime()));
    try {
      String host = (new URL(url)).getHost();
      record = repository.newRecord(recordId);
      record.setRecordType(RAW_RECORD_TYPE_QNAME, null);
      record.setField(FETCHED_RESULT_TLD_QNAME, host);
      record.setField(FETCHED_RESULT_BASE_URL_QNAME, url);
      record.setField(FETCHED_RESULT_FETCH_TIME_QNAME, 0L);
    } catch (RecordException e) {
      LOG.error("", e);
      return null;
    } catch (MalformedURLException e) {
      LOG.error("", e);
      return null;
    }
    return record;
  }
  
  private static Record refreshRecord(SolrDocument doc, Repository repository)
      throws InterruptedException {
    
    String solrLilyId = (String) doc.getFieldValue(SolrFields.LILY_ID);
    String solrTld = (String) doc.getFieldValue(SolrFields.TLD);
    String solrBaseUrl = (String) doc.getFieldValue(SolrFields.BASE_URL);
    Long solrTimestamp = (Long) doc.getFieldValue(SolrFields.TIMESTAMP);
    
    LOG.debug("refreshing url:{} timestamp:{}", solrBaseUrl, solrTimestamp);
    
    RecordId recordId = repository.getIdGenerator().fromString(solrLilyId);
    
    List<QName> fieldNames = new ArrayList<QName>();
    fieldNames.add(FETCHED_RESULT_TLD_QNAME);
    fieldNames.add(FETCHED_RESULT_BASE_URL_QNAME);
    fieldNames.add(FETCHED_RESULT_FETCH_TIME_QNAME);
    
    Record record = null;
    
    try {
      repository.read(recordId, fieldNames);
    } catch (RepositoryException e) {
      if (e instanceof RecordNotFoundException) {
        LOG.warn("record exists in Solr but doesn't exist in Lily");
        try {
          record = repository.newRecord(recordId);
          record.setRecordType(RAW_RECORD_TYPE_QNAME, null);
          record.setField(FETCHED_RESULT_TLD_QNAME, solrTld);
          record.setField(FETCHED_RESULT_BASE_URL_QNAME, solrBaseUrl);
          record.setField(FETCHED_RESULT_FETCH_TIME_QNAME, solrTimestamp);
          record = repository.create(record);
        } catch (RecordException e1) {
          LOG.error("", e1);
        } catch (RepositoryException e1) {
          LOG.error("", e1);
        }
      } else {
        LOG.error("", e);
      }
    }
    
    return record;
    
  }
  
}
