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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.ArrayUtils;
import org.apache.nutch.net.URLFilter;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;
import org.tokenizer.core.solr.SolrUtils;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.JavaSerializationUtils;
import org.tokenizer.core.util.LanguageDetector;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.model.FetchedResultRecord;
import org.tokenizer.crawler.db.model.HostRecord;
import org.tokenizer.crawler.db.model.MessageRecord;
import org.tokenizer.crawler.db.model.TimestampUrlIDX;
import org.tokenizer.crawler.db.model.UrlHeadRecord;
import org.tokenizer.crawler.db.model.UrlRecord;
import org.tokenizer.crawler.db.model.UrlSitemapIDX;
import org.tokenizer.crawler.db.model.WeblogRecord;
import org.tokenizer.crawler.db.model.WeblogRecord.Weblog;
import org.tokenizer.crawler.db.model.WebpageRecord;
import org.tokenizer.crawler.db.model.XmlRecord;
import org.tokenizer.nlp.NlpTools;
import org.tokenizer.nlp.Text;
import org.tokenizer.nlp.TextImpl;

import com.cybozu.labs.langdetect.LangDetectException;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ExceptionCallback;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.RowCallback;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.FieldMetadata;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.Equality;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.recipes.ReverseIndexQuery;
import com.netflix.astyanax.recipes.Shards;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.AsciiSerializer;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.astyanax.util.RangeBuilder;

/**
 * http://anuff.com/2011/02/indexing-in-cassandra/
 * 
 * @author Fuad
 * 
 */
@Singleton
public class CrawlerRepositoryCassandraImpl implements CrawlerRepository {
    
    protected static Logger LOG = LoggerFactory.getLogger(CrawlerRepositoryCassandraImpl.class);
    protected final String clusterName = "web_crawl_cluster";
    protected final String keyspaceName = "web_crawl_keyspace";
    
    protected String seeds = TokenizerConfig.getString("tokenizer.cassandra.seeds", "127.0.0.1");
    protected int port = TokenizerConfig.getInt("tokenizer.cassandra.port", 9160);
    
    /**
     * This is classic table key:=URL and columns:= all the response headers
     * except webpage content Additional indexes will be created If we make it
     * "wide-row" per host... we still need additional indexes; however, our
     * first additional index will be <Host, Url> (for only sitemaps URL)...
     * Right now simplest solution... later can be refactored see TDE-20
     * 
     */
    private static final String URL_RECORDS = "url_records";
    protected final ColumnFamily<String,String> CF_URL_RECORDS = ColumnFamily.newColumnFamily(URL_RECORDS, AsciiSerializer.get(),
            AsciiSerializer.get());
    
    /**
     * Basic index for quickly find URL to be fetched: <Host, Time> -> URL
     * 
     * Sitemaps only. We prefer to run (possibly) two threads: 1) URLs found in
     * sitemaps only 2) URLs outlinked from site maps and following special
     * pattern (such as user reviews for product pages at Amazon)
     * 
     * see TDE-21
     */
    private final static String URL_SITEMAP_IDX = "url_sitemap_idx";
    protected final ColumnFamily<String,UrlSitemapIDX> CF_URL_SITEMAP_IDX = ColumnFamily.newColumnFamily(URL_SITEMAP_IDX,
            AsciiSerializer.get(), URL_SITEMAP_IDX_SERIALIZER);
    
    private static final int DEFAULT_BUFFER_SIZE = 16386;
    
    private static final String TIMESTAMP_URL_IDX = "timestamp_url_idx";
    protected final ColumnFamily<String,TimestampUrlIDX> CF_TIMESTAMP_URL_IDX = ColumnFamily.newColumnFamily(TIMESTAMP_URL_IDX,
            AsciiSerializer.get(), TIMESTAMP_URL_IDX_SERIALIZER);
    
    protected static AnnotatedCompositeSerializer<TimestampUrlIDX> TIMESTAMP_URL_IDX_SERIALIZER = new AnnotatedCompositeSerializer<TimestampUrlIDX>(
            TimestampUrlIDX.class);
    
    
    /**
     * Tablespace name for Message -> URL inverted index
     */
    private static final String MESSAGE_URL_IDX = "message_url_idx";

    /**
     * Columnfamily definition for Message -> URL inverted index
     */
    protected final ColumnFamily<byte[],String> CF_MESSAGE_URL_IDX = ColumnFamily.newColumnFamily(MESSAGE_URL_IDX,
    		BytesArraySerializer.get(), AsciiSerializer.get());
    
    
    
    protected static AnnotatedCompositeSerializer<Weblog> WEBLOG_SERIALIZER = new AnnotatedCompositeSerializer<Weblog>(Weblog.class);
    
    // TODO: buffer is set explicitly to 8192 due to current bug in Astyanax
    // https://github.com/Netflix/astyanax/pull/228#issuecomment-15250973
    protected static AnnotatedCompositeSerializer<FetchedResultRecord> FETCHED_RESULT_SERIALIZER = new AnnotatedCompositeSerializer<FetchedResultRecord>(
            FetchedResultRecord.class);
    
    protected static AnnotatedCompositeSerializer<UrlSitemapIDX> URL_SITEMAP_IDX_SERIALIZER = new AnnotatedCompositeSerializer<UrlSitemapIDX>(
            UrlSitemapIDX.class);
    
    private final static String WEBPAGE_RECORDS = "webpage_records";
    protected final ColumnFamily<byte[],String> CF_WEBPAGE_RECORDS = ColumnFamily.newColumnFamily(WEBPAGE_RECORDS,
            BytesArraySerializer.get(), StringSerializer.get());
    
    private final static String XML_RECORDS = "xml_records";
    protected final ColumnFamily<byte[],String> CF_XML_RECORDS = ColumnFamily.newColumnFamily(XML_RECORDS,
            BytesArraySerializer.get(), StringSerializer.get());
    
    private static final String MESSAGE_RECORDS = "message_records";
    
    protected final ColumnFamily<byte[],String> CF_MESSAGE_RECORDS = ColumnFamily.newColumnFamily(MESSAGE_RECORDS,
            BytesArraySerializer.get(), StringSerializer.get());
    
    private static final String WEBLOGS_RECORDS = "weblogs_records";
    protected static final ColumnFamily<Integer,Weblog> CF_WEBLOGS_RECORDS = ColumnFamily.newColumnFamily(WEBLOGS_RECORDS,
            IntegerSerializer.get(), WEBLOG_SERIALIZER);
    
    private static final String WEBLOGS_RECORDS_IDX0 = "weblogs_records_idx0";
    private static final String SHARD_ = "SHARD_";
    private static final String SHARD_0 = "SHARD_0";
    
    // Index to find last sequence number (Weblog counter)
    protected final ColumnFamily<String,Integer> CF_WEBLOGS_RECORDS_IDX0 = ColumnFamily.newColumnFamily(WEBLOGS_RECORDS_IDX0,
            StringSerializer.get(), IntegerSerializer.get());
    
    private final static String FETCHED_RESULT_RECORDS = "fetched_result_records";
    protected static final ColumnFamily<String,FetchedResultRecord> CF_FETCHED_RESULT_RECORDS = ColumnFamily.newColumnFamily(
            FETCHED_RESULT_RECORDS, StringSerializer.get(), FETCHED_RESULT_SERIALIZER);
    
    /**
     * This CF is "vertical" classic table to store HTTP HEAD response
     */
    protected final static String URL_HEAD_RECORDS = "url_head_records";
    protected final ColumnFamily<String,String> CF_URL_HEAD_RECORDS = ColumnFamily.newColumnFamily(URL_HEAD_RECORDS,
            AsciiSerializer.get(), AsciiSerializer.get());
    
    // TDE-13: Index for Inverted Hosts
    private static final String HOST_RECORDS = "host_records";
    protected final ColumnFamily<String,byte[]> CF_HOST_RECORDS = ColumnFamily.newColumnFamily(HOST_RECORDS,
            StringSerializer.get(), BytesArraySerializer.get());
    
    protected Keyspace keyspace;
    protected static AstyanaxContext<Keyspace> keyspaceContext;
    
    protected static final int MAX_ROWS = 1000;
    
    private NlpTools nlpTools;
    
    @Inject
    public CrawlerRepositoryCassandraImpl(final NlpTools nlpTools) {
        this.nlpTools = nlpTools;
        
        LOG.error("Constructor called...");
        
        try {
            setup();
        } catch (ConnectionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    //CrawlerRepositoryCassandraImpl(final int port) {
    //    this.port = port;
    //}
    
    // @PostConstruct
    public void setup() throws ConnectionException, InterruptedException {
        
        LOG.error("setup called...");
        
        LOG.error("seeds: {}", seeds);
        
        keyspaceContext = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace(keyspaceName)
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl().setTargetCassandraVersion("1.2"))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl("MyConnectionPool").setPort(port)
                                .setSocketTimeout(120000)
                                .setConnectTimeout(300000)
                                .setTimeoutWindow(600000) // Shut down a host if
                                                          // it times out too
                                                          // many times within
                                                          // this window (?)
                                .setMaxConnsPerHost(16).setSeeds(seeds))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        keyspaceContext.start();
        
        keyspace = keyspaceContext.getClient();
        
        // try {
        // keyspace.dropKeyspace();
        // } catch (Exception e) {
        // }
        KeyspaceDefinition def = null;
        try {
            def = keyspace.describeKeyspace();
        } catch (Exception e) {
            LOG.warn(e.getMessage() + " - probablly keyspace doesn't exist yet");
        }
        if (def == null) {
            keyspace.createKeyspace(ImmutableMap.<String,Object> builder()
                    .put("strategy_options", ImmutableMap.<String,Object> builder().put("replication_factor", "1").build())
                    .put("strategy_class", "SimpleStrategy").build());
            try {
                def = keyspace.describeKeyspace();
            } catch (BadRequestException e) {}
        }
        
        // ///////////////
        // URL_RECORDS //
        // ///////////////
        // @formatter:off
        if (def.getColumnFamily(URL_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_URL_RECORDS,
                    ImmutableMap.<String,Object> builder()
                            .put("default_validation_class", "AsciiType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "KEYS")
                            .put("column_metadata",
                                    ImmutableMap.<String,Object> builder()
                                            .put("fetchedUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("fetchTime",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "LongType")
                                                            .build())
                                            .put("contentType",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("responseRate",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("headers",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("newBaseUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("numRedirects",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("hostAddress",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("httpStatus",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("reasonPhrase",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("host",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_URL_RECORDS_HOST")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("host_fetchAttemptCounter",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_URL_RECORDS_HOST_FETCH_ATTEMPT_COUNTER")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("webpageDigest",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        // @formatter:on
        
        // URL_SITEMAP_IDX
        if (def.getColumnFamily(URL_SITEMAP_IDX) == null) {
            keyspace.createColumnFamily(
                    CF_URL_SITEMAP_IDX,
                    ImmutableMap.<String,Object> builder().put("key_validation_class", "AsciiType")
                            .put("comparator_type", "CompositeType(UTF8Type)").build());
            
        }
        
        // MESSAGE_URL_IDX
        if (def.getColumnFamily(MESSAGE_URL_IDX) == null) {
            keyspace.createColumnFamily(
            		CF_MESSAGE_URL_IDX,
                    ImmutableMap.<String,Object> builder().put("key_validation_class", "BytesType")
                            .put("comparator_type", "AsciiType").build());
            
        }

        // CF_TIMESTAMP_URL_IDX
        if (def.getColumnFamily(TIMESTAMP_URL_IDX) == null) {
            keyspace.createColumnFamily(
                    CF_TIMESTAMP_URL_IDX,
                    ImmutableMap.<String,Object> builder().put("key_validation_class", "AsciiType")
                            .put("comparator_type", "CompositeType(LongType,UTF8Type)").build());
            
        }
        
        if (def.getColumnFamily(URL_HEAD_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_URL_HEAD_RECORDS,
                    ImmutableMap.<String,Object> builder()
                            // .put("default_validation_class",
                            // "AsciiType")
                            .put("key_validation_class", "AsciiType")
                            // .put("Caching", "ALL")
                            .put("column_metadata",
                                    ImmutableMap.<String,Object> builder()
                                            .put("fetchedUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("fetchTime",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "LongType")
                                                            .build())
                                            .put("contentType",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("headers",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("newBaseUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("numRedirects",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            
                                            .put("hostAddress",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("httpStatus",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("reasonPhrase",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            
                                            .build())
                            
                            .build());
        }
        
        // @formatter:off
        if (def.getColumnFamily(WEBPAGE_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_WEBPAGE_RECORDS,
                    ImmutableMap.<String,Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "KEYS")
                            .put("compression_options",
                                    ImmutableMap.<String,Object> builder()
                                            .put("sstable_compression", "SnappyCompressor")
                                            .put("chunk_length_kb", "64").build())
                            .put("column_metadata",
                                    ImmutableMap.<String,Object> builder()
                                            .put("host",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_WEBPAGE_RECORDS_HOST")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("baseUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("fetchedUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("fetchTime",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "LongType")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("contentType",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("responseRate",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("headers",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("newBaseUrl",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("numRedirects",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("hostAddress",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("httpStatus",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("reasonPhrase",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("host_extractOutlinksAttemptCounter",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_WEBPAGE_RECORDS_HOST_EXTRACT_OUTLINKS_ATTEMPT_COUNTER")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("host_splitAttemptCounter",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_WEBPAGE_RECORDS_HOST_SPLIT_ATTEMPT_COUNTER")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("xmlLinks",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        // @formatter:on
        
        // ////////////
        // XML Records
        // ////////////
        if (def.getColumnFamily(XML_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_XML_RECORDS,
                    ImmutableMap.<String,Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "KEYS")
                            .put("column_metadata",
                                    ImmutableMap.<String,Object> builder()
                                            .put("timestamp",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "DateType")
                                                            .build())
                                            .put("host",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("mainSubject",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                             .build())
                                            .put("content",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("host_parseAttemptCounter",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_XML_RECORDS_HOST_PARSE_ATTEMPT_COUNTER")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build()).build())
                            .build());
        }
        // //////////
        // Message Records
        // //////////
        if (def.getColumnFamily(MESSAGE_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_MESSAGE_RECORDS,
                    ImmutableMap.<String,Object> builder()
                            .put("default_validation_class", "UTF8Type")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "KEYS")
                            .put("column_metadata",
                                    ImmutableMap.<String,Object> builder()
                                            .put("host",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_name",
                                                                    "IDX_MESSAGE_RECORDS_HOST")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("topic",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("date",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("author",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("age",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("sex",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("title",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("userRating",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("location",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("mainSubject",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                             .build())
                                            .put("reviewText",
                                                    ImmutableMap.<String,Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType").build()).build())
                            .build());
        }
        
        // WEBLOGS_RECORDS
        if (def.getColumnFamily(WEBLOGS_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_WEBLOGS_RECORDS,
                    ImmutableMap.<String,Object> builder().put("default_validation_class", "BytesType")
                            .put("key_validation_class", "IntegerType")
                            .put("comparator_type", "CompositeType(DateType, UTF8Type, UTF8Type)").build());
            
        }
        
        // CF_WEBLOGS_RECORDS_IDX0
        if (def.getColumnFamily("WEBLOGS_RECORDS_IDX0") == null) {
            keyspace.createColumnFamily(
                    CF_WEBLOGS_RECORDS_IDX0,
                    ImmutableMap.<String,Object> builder().put("key_validation_class", "UTF8Type")
                            .put("comparator_type", "IntegerType").build());
            
        }
        
        // FETCHED_RESULT_RECORDS
        if (def.getColumnFamily("FETCHED_RESULT_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_FETCHED_RESULT_RECORDS,
                    ImmutableMap.<String,Object> builder().put("default_validation_class", "BytesType")
                            .put("key_validation_class", "UTF8Type")
                            // host inverted
                            .put("comparator_type", "CompositeType(UTF8Type, DateType)") // URL
                                                                                         // +
                                                                                         // Timestamp
                            .build());
            
        }
        
        // HOST_RECORDS
        if (def.getColumnFamily(HOST_RECORDS) == null) {
            keyspace.createColumnFamily(CF_HOST_RECORDS,
                    ImmutableMap.<String,Object> builder().put("default_validation_class", "BytesType")
                            // TODO: will store host-specific info
                            .put("key_validation_class", "UTF8Type").put("comparator_type", "BytesType").build());
            
        }
        
        KeyspaceDefinition ki2 = keyspaceContext.getEntity().describeKeyspace();
        System.out.println("Describe Keyspace: " + ki2.getName());
        getKeyspaceDefinition();
        
        // reindex();
        // REINDEX SOLR URLs
        Thread filterThread = new Thread("SOLR-URL_Records-Reindex") {
            
            @Override
            public void run() {
                final AtomicLong counter = new AtomicLong();
                try {
                    ArrayList<String> columns = new ArrayList<String>();
                    columns.add("url");
                    // @formatter:off
                    keyspace.prepareQuery(CF_URL_RECORDS).getAllRows().setRowLimit(100).setRepeatLastToken(true)
                            .executeWithCallback(new RowCallback<String,String>() {
                                @Override
                                public void success(final Rows<String,String> rows) {
                                    for (Row<String,String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        UrlRecord urlRecord = toUrlRecord(row.getKey(), columns);
                                        submitToSolr(urlRecord);
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} records reindexed...", counter.get());
                                        }
                                        
                                    }
                                }
                                
                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
                } catch (Exception e) {
                    LOG.error("", e);
                }
                LOG.error("Total {} records reindexed... ", counter.get());
            }
        };
        filterThread.setDaemon(true);
        // filterThread.start();
        
        // reindex();
        // REINDEX SOLR Message Records
        Thread messageRecordsReindexThread = new Thread("messageRecordsReindexThread") {
            
            @Override
            public void run() {
                final AtomicLong counter = new AtomicLong();
                try {
                    // ArrayList<String> columns = new ArrayList<String>();
                    // columns.add("url");
                    // @formatter:off
                    keyspace.prepareQuery(CF_MESSAGE_RECORDS).getAllRows().setRowLimit(100).setRepeatLastToken(true)
                            .executeWithCallback(new RowCallback<byte[],String>() {
                                @Override
                                public void success(final Rows<byte[],String> rows) {
                                    for (Row<byte[],String> row : rows) {
                                        ColumnList<String> columns = row.getColumns();
                                        MessageRecord record = toMessageRecord(row.getKey(), columns);
                                        submitToSolr(record);
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{} message records reindexed...", counter.get());
                                        }
                                        
                                    }
                                }
                                
                                @Override
                                public boolean failure(final ConnectionException e) {
                                    LOG.error(e.getMessage(), e);
                                    return false;
                                }
                            });
                    // @formatter:on
                } catch (Exception e) {
                    LOG.error("", e);
                }
                LOG.error("Total {} records reindexed... ", counter.get());
            }
        };
        messageRecordsReindexThread.setDaemon(true);
        // messageRecordsReindexThread.start();
        
    }
    
    public void getKeyspaceDefinition() throws ConnectionException {
        KeyspaceDefinition def = keyspaceContext.getEntity().describeKeyspace();
        Collection<String> fieldNames = def.getFieldNames();
        LOG.info("Getting field names");
        for (String field : fieldNames) {
            LOG.info(field);
        }
        LOG.info(fieldNames.toString());
        System.out.println(fieldNames.toString());
        for (FieldMetadata field : def.getFieldsMetadata()) {
            System.out.println(field.getName() + " = " + def.getFieldValue(field.getName()) + " (" + field.getType() + ")");
        }
        for (ColumnFamilyDefinition cfDef : def.getColumnFamilyList()) {
            LOG.info("----------");
            for (FieldMetadata field : cfDef.getFieldsMetadata()) {
                LOG.info(field.getName() + " = " + cfDef.getFieldValue(field.getName()) + " (" + field.getType() + ")");
            }
        }
    }
    
    // URL Records:
    @Override
    public UrlRecord retrieveUrlRecord(final String url) throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_URL_RECORDS).getKey(url).execute();
        ColumnList<String> columns = result.getResult();
        return toUrlRecord(url, columns);
    }
    
    @Override
    public void delete(final UrlRecord urlRecord) throws ConnectionException {
        LOG.warn("deleting record {}", urlRecord);
        deleteUrlRecord(urlRecord.getBaseUrl());
        // deleteWebpageRecord(urlRecord.getWebpageDigest());
    }
    
    private void deleteUrlRecord(final String key) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_RECORDS, key).delete();
        m.execute();
    }
    
    @Override
    public void deleteWebpageRecord(final byte[] digest) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, digest).delete();
        m.execute();
    }
    
    @Override
    public void update(final UrlRecord urlRecord) throws ConnectionException {
        insert(urlRecord);
    }
    
    @Override
    public void insert(final UrlRecord urlRecord) throws ConnectionException {
        
        LOG.debug("persisting urlRecord: {}", urlRecord);
        
        // @formatter:off
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_RECORDS, urlRecord.getBaseUrl())
                .putColumn("fetchedUrl", urlRecord.getFetchedUrl(), null)
                .putColumn("fetchTime", urlRecord.getFetchTime(), null)
                .putColumn("contentType", urlRecord.getContentType(), null)
                .putColumn("responseRate", urlRecord.getResponseRate(), null)
                .putColumn("headers", JavaSerializationUtils.serialize(urlRecord.getHeaders()), null)
                .putColumn("newBaseUrl", urlRecord.getNewBaseUrl(), null)
                .putColumn("numRedirects", urlRecord.getNumRedirects(), null)
                .putColumn("hostAddress", urlRecord.getHostAddress(), null)
                .putColumn("httpStatus", urlRecord.getHttpStatus(), null)
                .putColumn("reasonPhrase", urlRecord.getReasonPhrase(), null)
                .putColumn("host", urlRecord.getHost(), null)
                .putColumn("host_fetchAttemptCounter",
                        urlRecord.getHost() + String.valueOf(urlRecord.getFetchAttemptCounter()), null)
                .putColumn("webpageDigest", urlRecord.getWebpageDigest(), null);
        m.execute();
        // @formatter:on
        
        LOG.debug("urlRecord inserted: {}", urlRecord);
        
        submitToSolr(urlRecord);
        
    }
    
    @Override
    public List<byte[]> loadUrlRecordRowKeys(final String host, final int httpResponseCode) throws ConnectionException {
        
        // TODO:
        throw new RuntimeException("not implemented");
        
        /*
         * final List<byte[]> rowKeys = new ArrayList<byte[]>(MAX_ROWS); double
         * nanoStart = System.nanoTime(); byte[] hostInverted =
         * HttpUtils.getHostInverted(host); byte[] hostInverted_httpResponseCode
         * = ArrayUtils.addAll(hostInverted,
         * HttpUtils.intToBytes(httpResponseCode)); OperationResult<Rows<byte[],
         * String>> rows = keyspace
         * .prepareQuery(CF_URL_RECORDS).searchWithIndex()
         * .setRowLimit(MAX_ROWS).autoPaginateRows(false).addExpression()
         * .whereColumn("hostInverted_httpResponseCode").equals()
         * .value(hostInverted_httpResponseCode).execute(); for (Row<byte[],
         * String> row : rows.getResult()) { rowKeys.add(row.getKey()); } double
         * timeTaken = (System.nanoTime() - nanoStart) / (1e9);
         * LOG.debug("Time taken to fetch " + rowKeys.size() +
         * " URL Records row keys is " + timeTaken + " seconds."); return
         * rowKeys;
         */
    }
    
    @Override
    public List<UrlRecord> listUrlRecords(final String host, final int httpResponseCode, final int maxResults)
            throws ConnectionException {
        
        // TODO:
        throw new RuntimeException("not implemented");
        
        /*
         * double nanoStart = System.nanoTime(); byte[] hostInverted =
         * HttpUtils.getHostInverted(host); byte[] hostInverted_httpResponseCode
         * = ArrayUtils.addAll(hostInverted,
         * HttpUtils.intToBytes(httpResponseCode)); //@formatter:off
         * IndexQuery<byte[], String> query = keyspace
         * .prepareQuery(CF_URL_RECORDS)
         * .setConsistencyLevel(ConsistencyLevel.CL_ONE) .searchWithIndex()
         * .setRowLimit(maxResults) .autoPaginateRows(false) .addExpression()
         * .whereColumn("hostInverted_httpResponseCode") .equals()
         * .value(hostInverted_httpResponseCode); //@formatter:on
         * OperationResult<Rows<byte[], String>> result = query.execute(); if
         * (LOG.isDebugEnabled()) { double timeTaken = (System.nanoTime() -
         * nanoStart) / (1e9); LOG.debug("Time taken to fetch " +
         * result.getResult().size() + " URL records is " + timeTaken +
         * " seconds, host: {}", host); } return toUrlRecordList(result);
         */
    }
    
    @Override
    public List<UrlRecord> listUrlRecords(final String host, final int httpResponseCode, final byte[] startRowkey, final int count)
            throws ConnectionException {
        
        // TODO:
        throw new RuntimeException("not implemented");
        /*
         * double nanoStart = System.nanoTime(); byte[] hostInverted =
         * HttpUtils.getHostInverted(host); byte[] hostInverted_httpResponseCode
         * = ArrayUtils.addAll(hostInverted,
         * HttpUtils.intToBytes(httpResponseCode)); LOG.debug("host: {}", host);
         * LOG.debug("httpResponseCode: {}", httpResponseCode);
         * LOG.debug("startRowkey: {}", Arrays.toString(startRowkey));
         * LOG.debug("count: {}", count); //@formatter:off IndexQuery<byte[],
         * String> query = keyspace .prepareQuery(CF_URL_RECORDS)
         * .setConsistencyLevel(ConsistencyLevel.CL_ONE) .searchWithIndex()
         * .setStartKey(startRowkey) .setRowLimit(count)
         * .autoPaginateRows(false) .addExpression()
         * .whereColumn("hostInverted_httpResponseCode") .equals()
         * .value(hostInverted_httpResponseCode); //@formatter:on
         * OperationResult<Rows<byte[], String>> result = query.execute(); if
         * (LOG.isDebugEnabled()) { double timeTaken = (System.nanoTime() -
         * nanoStart) / (1e9); LOG.debug("Time taken to fetch " +
         * result.getResult().size() + " URL records is " + timeTaken +
         * " seconds, host: {}", host); } return toUrlRecordList(result);
         */
    }
    
    @Override
    public List<UrlRecord> listUrlRecordsByFetchAttemptCounter(final String host, final int fetchAttemptCounter,
            final int maxResults) throws ConnectionException {
        
        // TODO:
        throw new RuntimeException("not implemented");
        
        /*
         * 
         * double nanoStart = System.nanoTime(); byte[] hostInverted =
         * HttpUtils.getHostInverted(host); byte[]
         * hostInverted_fetchAttemptCounter = ArrayUtils.addAll( hostInverted,
         * HttpUtils.intToBytes(fetchAttemptCounter)); //@formatter:off
         * IndexQuery<byte[], String> query = keyspace
         * .prepareQuery(CF_URL_RECORDS)
         * .setConsistencyLevel(ConsistencyLevel.CL_ONE) .searchWithIndex()
         * .setRowLimit(maxResults) .autoPaginateRows(false) .addExpression()
         * .whereColumn("hostInverted_fetchAttemptCounter") .equals()
         * .value(hostInverted_fetchAttemptCounter); //@formatter:on
         * OperationResult<Rows<byte[], String>> result = query.execute(); if
         * (LOG.isDebugEnabled()) { double timeTaken = (System.nanoTime() -
         * nanoStart) / (1e9); LOG.debug("Time taken to fetch " +
         * result.getResult().size() + " URL records is " + timeTaken +
         * " seconds, host: {}", host); } return toUrlRecordList(result);
         */
        
    }
    
    @Override
    public List<UrlRecord> listUrlRecords(final String[] keys) throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<String,String>> result = keyspace.prepareQuery(CF_URL_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size() + " URL records is " + timeTaken + " seconds.");
        return toUrlRecordList(result);
    }
    
    @Override
    public void insertIfNotExists(final WebpageRecord webpageRecord) throws ConnectionException {
        try {
            OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_WEBPAGE_RECORDS)
                    .getKey(webpageRecord.getDigest()).execute();
            if (result.getResult().isEmpty()) {
                insert(webpageRecord);
            }
        } catch (Throwable t) {
            insert(webpageRecord);
        }
        
    }
    
    // @formatter:off
    protected void insert(final WebpageRecord webpageRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest())
                .putColumn("host", webpageRecord.getHost(), null)
                .putColumn("baseUrl", webpageRecord.getBaseUrl(), null)
                .putColumn("fetchedUrl", webpageRecord.getFetchedUrl(), null)
                .putColumn("fetchTime", webpageRecord.getFetchTime(), null)
                .putColumn("content", webpageRecord.getContent(), null)
                .putColumn("contentType", webpageRecord.getContentType(), null)
                .putColumn("responseRate", webpageRecord.getResponseRate(), null)
                .putColumn("headers", JavaSerializationUtils.serialize(webpageRecord.getHeaders()), null)
                .putColumn("newBaseUrl", webpageRecord.getNewBaseUrl(), null)
                .putColumn("numRedirects", webpageRecord.getNumRedirects(), null)
                .putColumn("hostAddress", webpageRecord.getHostAddress(), null)
                .putColumn("httpStatus", webpageRecord.getHttpStatus(), null)
                .putColumn("reasonPhrase", webpageRecord.getReasonPhrase(), null)
                .putColumn("host_extractOutlinksAttemptCounter", webpageRecord.getHostExtractOutlinksAttemptCounter(), null)
                .putColumn("host_splitAttemptCounter", webpageRecord.getHostSplitAttemptCounter(), null)
                .putColumn("xmlLinks", JavaSerializationUtils.serialize(webpageRecord.getXmlLinks()), null);
        m.execute();
        LOG.debug("webpageRecord inserted: {}", webpageRecord);
    }
    
    // @formatter:on
    
    @Override
    public void incrementExtractOutlinksAttemptCounter(final WebpageRecord webpageRecord) throws ConnectionException {
        webpageRecord.incrementExtractOutlinksAttemptCounter();
        updateExtractOutlinksAttemptCounter(webpageRecord);
    }
    
    public void updateExtractOutlinksAttemptCounter(WebpageRecord webpageRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest()).putColumn("host_extractOutlinksAttemptCounter",
                webpageRecord.getHostExtractOutlinksAttemptCounter(), null);
        m.execute();
    }
    
    @Override
    public void updateSplitAttemptCounterAndLinks(final WebpageRecord webpageRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest()).putColumn("host_splitAttemptCounter",
                webpageRecord.getHostSplitAttemptCounter(), null);
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest()).putColumn("xmlLinks",
                JavaSerializationUtils.serialize(webpageRecord.getXmlLinks()), null);
        m.execute();
    }
    
    @Override
    public void updateParseAttemptCounter(final XmlRecord xmlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_XML_RECORDS, xmlRecord.getDigest()).putColumn("host_parseAttemptCounter",
                xmlRecord.getHostParseAttemptCounter(), null);
        m.execute();
    }
    
    @Override
    public WebpageRecord retrieveWebpageRecord(final byte[] digest) throws ConnectionException {
        if (digest == null || digest.length == 0) return null;
        OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_WEBPAGE_RECORDS).getKey(digest).execute();
        ColumnList<String> columns = result.getResult();
        return toWebpageRecord(digest, columns);
    }
    
    @Override
    public MessageRecord retrieveMessageRecord(final byte[] digest) throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_MESSAGE_RECORDS).getKey(digest).execute();
        ColumnList<String> columns = result.getResult();
        return toMessageRecord(digest, columns);
    }

    
    @Override
    @Deprecated
    public List<WebpageRecord> listWebpageRecords(final String host, final int splitAttemptCounter, final int maxResults)
            throws ConnectionException {
        // @formatter:off
        IndexQuery<byte[],String> query = keyspace.prepareQuery(CF_WEBPAGE_RECORDS).setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex().setRowLimit(maxResults).autoPaginateRows(false).addExpression()
                .whereColumn("host_splitAttemptCounter").equals().value(host + String.valueOf(splitAttemptCounter));
        // @formatter:on
        OperationResult<Rows<byte[],String>> result = query.execute();
        return toWebpageRecordList(result);
    }
    
    @Override
    public List<WebpageRecord> listWebpageRecordsByExtractOutlinksAttemptCounter(final String host,
            final int extractOutlinksAttemptCounter, final int maxResults) throws ConnectionException {
        // @formatter:off
        IndexQuery<byte[],String> query = keyspace.prepareQuery(CF_WEBPAGE_RECORDS).setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex().setRowLimit(maxResults).autoPaginateRows(false).addExpression()
                .whereColumn("host_extractOutlinksAttemptCounter").equals()
                .value(host + String.valueOf(extractOutlinksAttemptCounter));
        // @formatter:on
        OperationResult<Rows<byte[],String>> result = query.execute();
        return toWebpageRecordList(result);
    }
    
    @Override
    public void insertIfNotExist(final XmlRecord xmlRecord) throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_XML_RECORDS).getKey(xmlRecord.getDigest()).execute();
        if (result.getResult().isEmpty()) {
            insert(xmlRecord);
        }
    }
    
    protected void insert(final XmlRecord xmlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_XML_RECORDS, xmlRecord.getDigest()).putColumn("host", xmlRecord.getHost(), null)
        .putColumn("mainSubject", xmlRecord.getMainSubject(), null)
                .putColumn("timestamp", xmlRecord.getTimestamp(), null).putColumn("content", xmlRecord.getContent(), null)
                .putColumn("host_parseAttemptCounter", xmlRecord.getHostParseAttemptCounter(), null);
        m.execute();
    }
    
    @Override
    public List<XmlRecord> listXmlRecords(final String host, final int parseAttemptCounter, final int maxResults)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        // @formatter:off
        IndexQuery<byte[],String> query = keyspace.prepareQuery(CF_XML_RECORDS).setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex().setRowLimit(maxResults).autoPaginateRows(false).addExpression()
                .whereColumn("host_parseAttemptCounter").equals().value(host + String.valueOf(parseAttemptCounter));
        // @formatter:on
        OperationResult<Rows<byte[],String>> result = query.execute();
        if (LOG.isDebugEnabled()) {
            double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
            LOG.debug("Time taken to fetch " + result.getResult().size() + " XML records is " + timeTaken + " seconds, host: {}",
                    host);
        }
        return toXmlRecordList(result);
    }
    
    @Override
    public List<XmlRecord> listXmlRecords(final byte[][] keys) throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[],String>> result = keyspace.prepareQuery(CF_XML_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size() + " URL records is " + timeTaken + " seconds.");
        return toXmlRecordList(result);
    }
    
    @Override
    public List<XmlRecord> listXmlRecords(final ArrayList<byte[]> xmlLinks) throws ConnectionException {
        byte[][] keys = new byte[xmlLinks.size()][];
        return listXmlRecords(xmlLinks.toArray(keys));
    }
    
    @Override
    public List<MessageRecord> listMessageRecords(final byte[][] keys) throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[],String>> result = keyspace.prepareQuery(CF_MESSAGE_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size() + " URL records is " + timeTaken + " seconds.");
        return toMessageRecordList(result);
    }
    
    @Override
    public List<MessageRecord> listMessageRecords(final ArrayList<byte[]> xmlLinks) throws ConnectionException {
        byte[][] keys = new byte[xmlLinks.size()][];
        return listMessageRecords(xmlLinks.toArray(keys));
    }
    
    @Override
    public void insertIfNotExists(final MessageRecord messageRecord) throws ConnectionException {
        try {
            OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_MESSAGE_RECORDS)
                    .getKey(messageRecord.getDigest()).execute();
            if (result.getResult().isEmpty()) {
                insert(messageRecord);
            }
        } catch (Throwable t) {
            insert(messageRecord);
        }
        submitToSolr(messageRecord);
    }
    
    protected void insert(final MessageRecord messageRecord) throws ConnectionException {
        
        Text reviewText = nlpTools.process(messageRecord.getContent());
        messageRecord.setReviewText((TextImpl)reviewText);
        
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_MESSAGE_RECORDS, messageRecord.getDigest())
                .putColumn("host", messageRecord.getHost(), null)
                .putColumn("topic", messageRecord.getTopic(), null)
                .putColumn("date", messageRecord.getDate(), null)
                .putColumn("author", messageRecord.getAuthor(), null)
                .putColumn("age", messageRecord.getAge(), null)
                .putColumn("sex", messageRecord.getSex(), null)
                .putColumn("title", messageRecord.getTitle(), null)
                .putColumn("content", messageRecord.getContent(), null)
                .putColumn("userRating", messageRecord.getUserRating(), null)
                .putColumn("location", messageRecord.getLocation(), null)
                .putColumn("mainSubject", messageRecord.getMainSubject(), null)
                .putColumn("reviewText", JavaSerializationUtils.serialize(messageRecord.getReviewText()), null);
        m.execute();
    }
    
    @Override
    public int countUrlRecords() throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<String,String>> rows = keyspace.prepareQuery(CF_URL_RECORDS).getAllRows().setRowLimit(10000)
                .setExceptionCallback(new ExceptionCallback() {
                    
                    @Override
                    public boolean onException(final ConnectionException e) {
                        e.printStackTrace();
                        return true;
                    }
                }).execute();
        int counter = 0;
        for (Row<String,String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " rows is " + timeTaken + " seconds.");
        return counter;
    }
    
    @Override
    public int countUrlRecords(final String host, final int httpResponseCode) throws ConnectionException {
        
        // TODO:
        throw new RuntimeException("not implemented");
        
        /*
         * 
         * double nanoStart = System.nanoTime(); OperationResult<Rows<byte[],
         * String>> rows = keyspace .prepareQuery(CF_URL_RECORDS)
         * .searchWithIndex() .setRowLimit(MAX_ROWS) .autoPaginateRows(false)
         * .addExpression() .whereColumn("hostInverted") .equals()
         * .value(HttpUtils.getHostInverted(host)) .addExpression()
         * .whereColumn("httpResponseCode") .equals() .value(httpResponseCode)
         * .withColumnRange( new
         * RangeBuilder().setStart("").setLimit(0).build()) .execute(); int
         * counter = 0; for (Row<byte[], String> row : rows.getResult()) {
         * counter++; } double timeTaken = (System.nanoTime() - nanoStart) /
         * (1e9); LOG.debug("Time taken to count " + counter + " rows is " +
         * timeTaken + " seconds."); return counter;
         */
    }
    
    /**
     * This is super fast possibly because it is multithreaded. Unfortunately,
     * iimplemented for all records (no index support).
     * 
     * @return
     * @throws Exception
     */
    public long countUrlRecords2() throws Exception {
        
        // TODO:
        throw new RuntimeException("not implemented");
        
        /*
         * final AtomicLong counter = new AtomicLong(0); double nanoStart =
         * System.nanoTime(); boolean result = new AllRowsReader.Builder<byte[],
         * String>(keyspace, CF_URL_RECORDS) .forEachRow(new
         * Function<Row<byte[], String>, Boolean>() {
         * 
         * @Override public Boolean apply(@Nullable final Row<byte[], String>
         * row) { counter.incrementAndGet(); return true; } }).build().call();
         * double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
         * LOG.debug("Time taken to count " + counter + " rows is " + timeTaken
         * + " seconds."); return counter.get();
         */
    }
    
    @Override
    public int countXmlRecords(final String host, final int parseAttemptCounter) throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[],String>> rows = keyspace.prepareQuery(CF_XML_RECORDS).searchWithIndex().setRowLimit(10000)
                .autoPaginateRows(true).addExpression().whereColumn("hostInverted").equals()
                .value(HttpUtils.getHostInverted(host)).addExpression().whereColumn("parseAttemptCounter").equals()
                .value(parseAttemptCounter).execute();
        int counter = 0;
        for (Row<byte[],String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " XML records is " + timeTaken + " seconds.");
        return counter;
    }
    
    public void setSeeds(final String seeds) {
        this.seeds = seeds;
    }
    
    protected static UrlRecord toUrlRecord(final Row<String,String> row) {
        String key = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toUrlRecord(key, columns);
    }
    
    protected static UrlRecord toUrlRecord(final String baseUrl, final ColumnList<String> columns) {
        if (columns.isEmpty()) {
            return null;
        }
        
        String fetchedUrl = columns.getStringValue("fetchedUrl", DefaultValues.EMPTY_STRING);
        long fetchTime = columns.getLongValue("fetchTime", 0L);
        String contentType = columns.getStringValue("contentType", DefaultValues.EMPTY_STRING);
        int responseRate = columns.getIntegerValue("responseRate", 0);
        
        byte[] headersSerialized = columns.getByteArrayValue("headers", null);
        Metadata headers = (headersSerialized == null ? DefaultValues.EMPTY_METADATA : (Metadata) JavaSerializationUtils
                .deserialize(headersSerialized));
        
        String newBaseUrl = columns.getStringValue("newBaseUrl", DefaultValues.EMPTY_STRING);
        int numRedirects = columns.getIntegerValue("numRedirects", 0);
        
        String hostAddress = columns.getStringValue("hostAddress", DefaultValues.EMPTY_STRING);
        int httpStatus = columns.getIntegerValue("httpStatus", 0);
        String reasonPhrase = columns.getStringValue("reasonPhrase", DefaultValues.EMPTY_STRING);
        
        // calculated
        String host = columns.getStringValue("host", DefaultValues.EMPTY_STRING);
        
        String host_fetchAttemptCounter = columns.getStringValue("host_fetchAttemptCounter", DefaultValues.EMPTY_STRING);
        
        String fetchAttemptCounterString = host_fetchAttemptCounter.substring(host.length());
        
        int fetchAttemptCounter = Integer.parseInt(fetchAttemptCounterString);
        
        byte[] webpageDigest = columns.getByteArrayValue("webpageDigest", DefaultValues.EMPTY_ARRAY);
        
        // @formatter:off
        UrlRecord urlRecord = new UrlRecord(baseUrl, fetchedUrl, fetchTime, contentType, responseRate, headers, newBaseUrl,
                numRedirects, hostAddress, httpStatus, reasonPhrase, host, fetchAttemptCounter, webpageDigest);
        // @formatter:on
        
        LOG.debug("urlRecord loaded: {}", urlRecord);
        
        return urlRecord;
        
    }
    
    protected static List<UrlRecord> toUrlRecordList(final OperationResult<Rows<String,String>> result) {
        List<UrlRecord> list = new ArrayList<UrlRecord>();
        for (Row<String,String> row : result.getResult()) {
            list.add(toUrlRecord(row));
        }
        return list;
    }
    
    protected static WebpageRecord toWebpageRecord(final Row<byte[],String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toWebpageRecord(digest, columns);
    }
    
    protected static WebpageRecord toWebpageRecord(byte[] digest, final ColumnList<String> columns) {
        
        if (columns.isEmpty()) {
            return null;
        }
        
        final String host = columns.getStringValue("host", null);
        final String baseUrl = columns.getStringValue("baseUrl", null);
        final String fetchedUrl = columns.getStringValue("fetchedUrl", null);
        final long fetchTime = columns.getLongValue("fetchTime", 0L);
        final byte[] content = columns.getByteArrayValue("content", null);
        final String contentType = columns.getStringValue("contentType", null);
        final int responseRate = columns.getIntegerValue("responseRate", 0);
        final Metadata headers = (Metadata) JavaSerializationUtils.deserialize(columns.getByteArrayValue("headers", null));
        final String newBaseUrl = columns.getStringValue("newBaseUrl", null);
        final int numRedirects = columns.getIntegerValue("numRedirects", 0);
        final String hostAddress = columns.getStringValue("hostAddress", null);
        final int httpStatus = columns.getIntegerValue("httpStatus", 0);
        final String reasonPhrase = columns.getStringValue("reasonPhrase", null);
        
        String host_extractOutlinksAttemptCounter = columns.getStringValue("host_extractOutlinksAttemptCounter", null);
        
        String extractOutlinksAttemptCounterString = host_extractOutlinksAttemptCounter.substring(host.length());
        
        int extractOutlinksAttemptCounter = Integer.parseInt(extractOutlinksAttemptCounterString);
        
        String host_splitAttemptCounter = columns.getStringValue("host_splitAttemptCounter", null);
        
        String splitAttemptCounterString = host_splitAttemptCounter.substring(host.length());
        
        int splitAttemptCounter = Integer.parseInt(splitAttemptCounterString);
        
        WebpageRecord webpageRecord = new WebpageRecord(digest, host, baseUrl, fetchedUrl, fetchTime, content, contentType,
                responseRate, headers, newBaseUrl, numRedirects, hostAddress, httpStatus, reasonPhrase,
                extractOutlinksAttemptCounter, splitAttemptCounter);
        final ArrayList<byte[]> xmlLinks = (ArrayList<byte[]>) JavaSerializationUtils.deserialize(columns.getByteArrayValue(
                "xmlLinks", null));
        
        webpageRecord.setXmlLinks(xmlLinks);
        
        return webpageRecord;
        
    }
    
    private static List<WebpageRecord> toWebpageRecordList(final OperationResult<Rows<byte[],String>> result) {
        List<WebpageRecord> list = new ArrayList<WebpageRecord>();
        for (Row<byte[],String> row : result.getResult()) {
            list.add(toWebpageRecord(row));
        }
        return list;
    }
    
    protected static XmlRecord toXmlRecord(final byte[] digest, final ColumnList<String> columns) {
        if (columns.isEmpty()) {
            LOG.error("Columns are empty for XML record with digest " + Arrays.toString(digest));
            return null;
        }
        Date timestamp = columns.getDateValue("timestamp", null);
        String host = columns.getStringValue("host", null);
        String mainSubject = columns.getStringValue("mainSubject", null);
        byte[] content = columns.getByteArrayValue("content", null);
        
        String host_parseAttemptCounter = columns.getStringValue("host_parseAttemptCounter", null);
        String parseAttemptCounterString = host_parseAttemptCounter.substring(host.length());
        int parseAttemptCounter = Integer.parseInt(parseAttemptCounterString);
        
        XmlRecord xmlRecord = new XmlRecord(digest, timestamp, host, mainSubject, content, parseAttemptCounter);
        
        LOG.trace("xmlRecord: {}", xmlRecord);
        
        return xmlRecord;
        
    }
    
    protected static XmlRecord toXmlRecord(final Row<byte[],String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toXmlRecord(digest, columns);
    }
    
    protected static List<XmlRecord> toXmlRecordList(final OperationResult<Rows<byte[],String>> result) {
        List<XmlRecord> list = new ArrayList<XmlRecord>();
        for (Row<byte[],String> row : result.getResult()) {
            list.add(toXmlRecord(row));
        }
        return list;
    }
    
    // /
    protected static MessageRecord toMessageRecord(final byte[] digest, final ColumnList<String> columns) {
        if (columns.isEmpty()) {
            LOG.error("Columns are empty for Message record with digest " + Arrays.toString(digest));
            return null;
        }
        String host = columns.getStringValue("host", null);
        String topic = columns.getStringValue("topic", null);
        String date = columns.getStringValue("date", null);
        String author = columns.getStringValue("author", null);
        String age = columns.getStringValue("age", null);
        String sex = columns.getStringValue("sex", null);
        String title = columns.getStringValue("title", null);
        String content = columns.getStringValue("content", null);
        String userRating = columns.getStringValue("userRating", null);
        String location = columns.getStringValue("location", null);
        String mainSubject = columns.getStringValue("mainSubject", null);
        final TextImpl reviewText = (TextImpl) JavaSerializationUtils.deserialize(columns.getByteArrayValue(
                "reviewText", null));

        MessageRecord messageRecord = new MessageRecord(digest, host, topic, date, author, age, sex, title, content, userRating,
                location, mainSubject);
        
        messageRecord.setReviewText(reviewText);
        
        return messageRecord;
    }
    
    protected static MessageRecord toMessageRecord(final Row<byte[],String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toMessageRecord(digest, columns);
    }
    
    protected static List<MessageRecord> toMessageRecordList(final OperationResult<Rows<byte[],String>> result) {
        List<MessageRecord> list = new ArrayList<MessageRecord>();
        for (Row<byte[],String> row : result.getResult()) {
            list.add(toMessageRecord(row));
        }
        return list;
    }
    
    protected void reindex() {
        final AtomicLong counter = new AtomicLong();
        try {
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("url");
            // @formatter:off
            keyspace.prepareQuery(CF_WEBPAGE_RECORDS).getAllRows().setRowLimit(100).setRepeatLastToken(true)
                    .withColumnSlice(columns).executeWithCallback(new RowCallback<byte[],String>() {
                        @Override
                        public void success(final Rows<byte[],String> rows) {
                            for (Row<byte[],String> row : rows) {
                                String url = row.getColumns().getStringValue("url", null);
                                String host = HttpUtils.getHost(url);
                                byte[] hostInverted = HttpUtils.getHostInverted(host);
                                
                                byte[] hostInverted_splitAttemptCounter = ArrayUtils.addAll(hostInverted, new byte[] {0, 0,
                                        0, 0});
                                MutationBatch m = keyspace.prepareMutationBatch();
                                m.withRow(CF_WEBPAGE_RECORDS, row.getKey()).putColumn("hostInverted_splitAttemptCounter",
                                        hostInverted_splitAttemptCounter, null);
                                try {
                                    m.execute();
                                    counter.incrementAndGet();
                                    if (counter.get() % 1000 == 0) {
                                        LOG.warn("{} records reindexed...", counter.get());
                                    }
                                } catch (ConnectionException e) {
                                    LOG.error("", e);
                                }
                                
                            }
                        }
                        
                        @Override
                        public boolean failure(final ConnectionException e) {
                            LOG.error(e.getMessage(), e);
                            return false;
                        }
                    });
            // @formatter:on
        } catch (Exception e) {
            LOG.error("", e);
        }
        LOG.error("Total {} records reindexed... ", counter.get());
    }
    
    @Override
    public void filter(final String host, final URLFilter urlFilter) {
        final AtomicLong counter = new AtomicLong();
        try {
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("url");
            columns.add("webpageDigest");
            MutationBatch m = keyspace.prepareMutationBatch();
            // @formatter:off
            keyspace.prepareQuery(CF_URL_RECORDS).getAllRows().setRowLimit(100).setRepeatLastToken(true).withColumnSlice(columns)
                    .executeWithCallback(new RowCallback<String,String>() {
                        @Override
                        public void success(final Rows<String,String> rows) {
                            for (Row<String,String> row : rows) {
                                String url = row.getColumns().getStringValue("url", null);
                                if (!host.equals(HttpUtils.getHost(url))) {
                                    continue;
                                }
                                byte[] webpageDigest = row.getColumns().getByteArrayValue("webpageDigest", null);
                                if (!urlFilter.accept(url)) {
                                    LOG.debug("Filtering URL: {}", url);
                                    try {
                                        deleteUrlRecord(row.getKey());
                                        deleteWebpageRecord(webpageDigest);
                                        counter.incrementAndGet();
                                        if (counter.get() % 1000 == 0) {
                                            LOG.warn("{}: {} URL records deleted...", host, counter.get());
                                        }
                                    } catch (ConnectionException e) {
                                        LOG.error("", e);
                                    }
                                }
                            }
                        }
                        
                        @Override
                        public boolean failure(final ConnectionException e) {
                            LOG.error(e.getMessage(), e);
                            return false;
                        }
                    });
            // @formatter:on
        } catch (Exception e) {
            LOG.error("", e);
        }
        LOG.warn("{}: total {} filtered records deleted... ", host, counter.get());
    }
    
    private void submitToSolr(final UrlRecord urlRecord) {
        LOG.debug("adding to solr: {}", urlRecord);
        SolrServer solrServer = SolrUtils.getSolrServer();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", MD5.MD5(urlRecord.getBaseUrl()));
        // doc.addField("id", urlRecord.getBaseUrl());
        doc.addField("host", urlRecord.getHost());
        doc.addField("url", urlRecord.getBaseUrl());
        doc.addField("httpStatus", urlRecord.getHttpStatus());
        try {
            solrServer.add(doc);
        } catch (SolrServerException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        // try {
        // solrServer.commit();
        // } catch (SolrServerException | IOException e) {
        // e.printStackTrace();
        // }
    }
    
    private void submitToSolr(final MessageRecord messageRecord) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", MD5.toHexString(messageRecord.getDigest()));
        doc.addField("host_s", messageRecord.getHost());
        doc.addField("content_en", messageRecord.getContent());
        doc.addField("age_ti", messageRecord.getAge().equals(DefaultValues.EMPTY_STRING) ? 0 : messageRecord.getAge());
        doc.addField("author_en", messageRecord.getAuthor());
        doc.addField("date_tdt", messageRecord.getISO8601Date());
        doc.addField("sex_s", messageRecord.getSex());
        doc.addField("title_en", messageRecord.getTitle());
        doc.addField("topic_en", messageRecord.getTopic());
        doc.addField("userRating_s", messageRecord.getUserRating());
        doc.addField("location_s", messageRecord.getLocation());
        doc.addField("mainSubject_en", messageRecord.getMainSubject());
        
        
        try
		{
			String language = LanguageDetector.detect(messageRecord.getContent());
			doc.addField("language_s", language);
		}
		catch (LangDetectException e1)
		{
			LOG.debug("", e1);
		}
        
        
        Text text = messageRecord.getReviewText();
        
        Set<String> features = text.getFeatures();
        
        doc.addField("feature_ss", features);
        
        int sentiment = text.getSentiment();
        
        if (sentiment > 0) {
            doc.addField("sentiment_s", "Positive");
        } else if (sentiment < 0) {
            doc.addField("sentiment_s", "Negative");
        } else {
            doc.addField("sentiment_s", "Neutral");
        }
        
        try {
            SolrUtils.getSolrServerForMessages().add(doc);
        } catch (SolrServerException e) {
            LOG.error(messageRecord.toString(), e);
        } catch (IOException e) {
            LOG.error(messageRecord.toString(), e);
        } catch (Exception e) {
            LOG.error(messageRecord.toString(), e);
        }
        // solrServer.commit();
    }
    
    @Override
    public void insert(final WeblogRecord weblogsRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        for (Weblog weblog : weblogsRecord.getWeblogs()) {
            m.withRow(CF_WEBLOGS_RECORDS, weblogsRecord.getCount()).putEmptyColumn(weblog);
        }
        
        m.withRow(CF_WEBLOGS_RECORDS_IDX0, SHARD_0).putEmptyColumn(weblogsRecord.getCount());
        
        m.execute();
    }
    
    public WeblogRecord getLastWeblogRecordSample() throws ConnectionException {
        
        final WeblogRecord weblogRecord = new WeblogRecord();
        final List<WeblogRecord.Weblog> weblogs = new ArrayList<WeblogRecord.Weblog>();
        weblogRecord.setWeblogs(weblogs);
        
        ReverseIndexQuery.newQuery(keyspace, CF_WEBLOGS_RECORDS, WEBLOGS_RECORDS_IDX0, IntegerSerializer.get())
                .withIndexShards(new Shards.StringShardBuilder().setPrefix(SHARD_).setShardCount(1).build())
                .withConsistencyLevel(ConsistencyLevel.CL_ONE).fromIndexValue(0).toIndexValue(Integer.MAX_VALUE)
                
                .forEach(new Function<Row<Integer,WeblogRecord.Weblog>,Void>() {
                    
                    @Override
                    public Void apply(final Row<Integer,WeblogRecord.Weblog> row) {
                        
                        weblogRecord.setCountInverted(row.getKey());
                        for (Column<WeblogRecord.Weblog> weblogColumn : row.getColumns()) {
                            WeblogRecord.Weblog weblog = weblogColumn.getName();
                            weblogs.add(weblog);
                        }
                        return null;
                    }
                }).execute();
        
        return weblogRecord;
        
    }
    
    @Override
    public WeblogRecord getLastWeblogRecord() throws ConnectionException {
        
        final WeblogRecord weblogRecord = new WeblogRecord();
        final List<WeblogRecord.Weblog> weblogs = new ArrayList<WeblogRecord.Weblog>();
        weblogRecord.setWeblogs(weblogs);
        
        int count = getLastWeblogCounter();
        weblogRecord.setCount(count);
        
        OperationResult<ColumnList<WeblogRecord.Weblog>> result = keyspace.prepareQuery(CF_WEBLOGS_RECORDS).getKey(count)
                .autoPaginate(true).execute();
        
        ColumnList<WeblogRecord.Weblog> columns = result.getResult();
        for (Column<WeblogRecord.Weblog> weblogColumn : columns) {
            WeblogRecord.Weblog weblog = weblogColumn.getName();
            weblogs.add(weblog);
        }
        
        return weblogRecord;
        
    }
    
    public int getLastWeblogCounter() throws ConnectionException {
        
        OperationResult<ColumnList<Integer>> result = keyspace.prepareQuery(CF_WEBLOGS_RECORDS_IDX0).getKey(SHARD_0)
                .autoPaginate(false).withColumnRange(new RangeBuilder().setReversed(true).setLimit(1).build()).execute();
        
        return result.getResult().getColumnNames().iterator().next();
        
    }
    
    @Override
    public void insert(final FetchedResultRecord fetchedResultRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_FETCHED_RESULT_RECORDS, fetchedResultRecord.getHost()).putColumn(fetchedResultRecord,
                JavaSerializationUtils.serialize(fetchedResultRecord));
        m.execute();
        
        insertIfNotExists(new HostRecord(fetchedResultRecord.getHost()));
        
    }
    
    @Override
    public void insert(final HostRecord hostRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_HOST_RECORDS, hostRecord.getTld()).putColumn(hostRecord.getHostInverted(),
                JavaSerializationUtils.serialize(hostRecord));
        m.execute();
    }
    
    @Override
    public void insertIfNotExists(final HostRecord hostRecord) throws ConnectionException {
        OperationResult<ColumnList<byte[]>> result = keyspace.prepareQuery(CF_HOST_RECORDS).getKey(hostRecord.getTld())
                .withColumnSlice(hostRecord.getHostInverted()).execute();
        if (result.getResult().isEmpty()) {
            insert(hostRecord);
        }
    }
    
    @Override
    public Collection<String> listTLDs() throws ConnectionException {
        OperationResult<Rows<String,byte[]>> result = keyspace.prepareQuery(CF_HOST_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE).getAllRows()
                .withColumnRange(new RangeBuilder().setLimit(0).build()).execute();
        Set list = new TreeSet<String>();
        for (Row<String,byte[]> row : result.getResult()) {
            list.add(row.getKey());
        }
        return list;
    }
    
    @Override
    public int countHosts(final String tld) {
        OperationResult<ColumnList<byte[]>> result;
        try {
            result = keyspace.prepareQuery(CF_HOST_RECORDS).setConsistencyLevel(ConsistencyLevel.CL_ONE).getKey(tld).execute();
            int size = result.getResult().size();
            LOG.info("TLD: {}; Columns size: {}", tld, size);
            return size;
        } catch (ConnectionException e) {
            LOG.error("", e);
            return 0;
        }
    }
    
    @Override
    public List<HostRecord> listHostRecords(final String tld, final int startIndex, final int count) {
        ColumnList<byte[]> columns;
        int pagesize = 10;
        int pointer = -1;
        int endIndex = startIndex + count;
        List<HostRecord> hostRecords = new ArrayList<HostRecord>();
        OperationResult<ColumnList<byte[]>> result;
        try {
            RowQuery<String,byte[]> query = keyspace.prepareQuery(CF_HOST_RECORDS).setConsistencyLevel(ConsistencyLevel.CL_ONE)
                    .getKey(tld).withColumnRange(new RangeBuilder().setLimit(pagesize).build()).autoPaginate(true);
            
            while (!(columns = query.execute().getResult()).isEmpty()) {
                LOG.info("paginated query: TLD: {}; columns size: {}", tld, columns.size());
                for (Column<byte[]> c : columns) {
                    // LOG.info(Long.toString(c.getName()));
                    pointer++;
                    if (pointer >= startIndex && pointer < endIndex) {
                        HostRecord hostRecord = (HostRecord) JavaSerializationUtils.deserialize(c.getByteArrayValue());
                        hostRecords.add(hostRecord);
                    }
                }
                // column = Iterables.getLast(columns).getName() + "\u0000";
                if (pointer >= endIndex) {
                    break;
                }
            }
            return hostRecords;
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
        
        return null;
        
    }
    
    @Override
    public List<FetchedResultRecord> listFetchedResultRecords(final String host) {
        ColumnList<FetchedResultRecord> columns;
        int pagesize = 10;
        List<FetchedResultRecord> fetchedResultRecords = new ArrayList<FetchedResultRecord>();
        OperationResult<ColumnList<byte[]>> result;
        try {
            RowQuery<String,FetchedResultRecord> query = keyspace.prepareQuery(CF_FETCHED_RESULT_RECORDS).getKey(host)
                    .withColumnRange(new RangeBuilder().setLimit(pagesize).build()).autoPaginate(true);
            while (!(columns = query.execute().getResult()).isEmpty()) {
                for (Column<FetchedResultRecord> c : columns) {
                    FetchedResultRecord fetchedResultRecord = (FetchedResultRecord) JavaSerializationUtils.deserialize(c
                            .getByteArrayValue());
                    fetchedResultRecords.add(fetchedResultRecord);
                }
            }
            return fetchedResultRecords;
        } catch (ConnectionException e) {
            LOG.error("", e);
        }
        
        return null;
        
    }
    
    protected void insert(final UrlHeadRecord urlHeadRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_HEAD_RECORDS, urlHeadRecord.getBaseUrl())
                
                .putColumn("fetchedUrl", urlHeadRecord.getFetchedUrl(), null)
                .putColumn("fetchTime", urlHeadRecord.getFetchTime(), null)
                .putColumn("contentType", urlHeadRecord.getContentType(), null)
                .putColumn("headers", JavaSerializationUtils.serialize(urlHeadRecord.getHeaders()), null)
                .putColumn("newBaseUrl", urlHeadRecord.getNewBaseUrl(), null)
                .putColumn("numRedirects", urlHeadRecord.getNumRedirects(), null)
                .putColumn("hostAddress", urlHeadRecord.getHostAddress(), null)
                .putColumn("httpStatus", urlHeadRecord.getHttpStatus(), null)
                .putColumn("reasonPhrase", urlHeadRecord.getReasonPhrase(), null);
        
        m.execute();
        LOG.debug("urlHeadRecord inserted: {}", urlHeadRecord);
    }
    
    @Override
    public void insertIfNotExists(final UrlHeadRecord urlHeadRecord) throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace.prepareQuery(CF_URL_HEAD_RECORDS).getKey(urlHeadRecord.getBaseUrl())
                .execute();
        if (result.getResult().isEmpty()) {
            insert(urlHeadRecord);
        }
    }
    
    // CF_URL_SITEMAP_IDX
    @Override
    public void insert(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_SITEMAP_IDX, urlSitemapIDX.getHost()).putEmptyColumn(urlSitemapIDX);
        m.execute();
    }
    
    @Override
    public void delete(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException {
        LOG.warn("deleting record {}", urlSitemapIDX);
        keyspace.prepareColumnMutation(CF_URL_SITEMAP_IDX, urlSitemapIDX.getHost(), urlSitemapIDX).deleteColumn().execute();
    }
    
    @Override
    public UrlSitemapIDX load(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException {
        LOG.debug("loading record {}", urlSitemapIDX);
        try {
            OperationResult<Column<UrlSitemapIDX>> result = keyspace.prepareQuery(CF_URL_SITEMAP_IDX)
                    .getKey(urlSitemapIDX.getHost()).getColumn(urlSitemapIDX).execute();
            return result.getResult().getName();
        } catch (NotFoundException e) {} catch (Exception e) {
            LOG.warn("error loading " + urlSitemapIDX, e);
        }
        return null;
    }
    
    @Override
    public void insertIfNotExists(final UrlSitemapIDX urlSitemapIDX) throws ConnectionException {
        if (load(urlSitemapIDX) == null) {
            insert(urlSitemapIDX);
        }
        
    }
    
    // CF_TIMESTAMP_URL_IDX
    @Override
    public void insert(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_TIMESTAMP_URL_IDX, timestampUrlIDX.getHost()) // .setTimestamp(System.currentTimeMillis())
                .putEmptyColumn(timestampUrlIDX);
        m.execute();
    }
    
    @Override
    public void delete(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException {
        LOG.warn("Deleting record {}", timestampUrlIDX);
        keyspace.prepareColumnMutation(CF_TIMESTAMP_URL_IDX, timestampUrlIDX.getHost(), timestampUrlIDX).deleteColumn().execute();
    }
    
    @Override
    public TimestampUrlIDX load(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException {
        try {
            OperationResult<Column<TimestampUrlIDX>> result = keyspace.prepareQuery(CF_TIMESTAMP_URL_IDX)
                    .getKey(timestampUrlIDX.getHost()).getColumn(timestampUrlIDX).execute();
            return result.getResult().getName();
        } catch (NotFoundException e) {}
        return null;
    }
    
    @Override
    public void insertIfNotExists(final TimestampUrlIDX timestampUrlIDX) throws ConnectionException {
        if (load(timestampUrlIDX) == null) {
            insert(timestampUrlIDX);
        }
    }
    
    @Override
    public List<TimestampUrlIDX> loadTimestampUrlIDX(final String host) throws ConnectionException {
        
        OperationResult<ColumnList<TimestampUrlIDX>> result = keyspace
                .prepareQuery(CF_TIMESTAMP_URL_IDX)
                .getKey(host)
                .withColumnRange(TIMESTAMP_URL_IDX_SERIALIZER.makeEndpoint(0L, Equality.LESS_THAN).toBytes(),
                        TIMESTAMP_URL_IDX_SERIALIZER.makeEndpoint(0L, Equality.GREATER_THAN).toBytes(), false, 100)
                .execute();
        
        List<TimestampUrlIDX> list = new ArrayList<TimestampUrlIDX>();
        
        for (Column<TimestampUrlIDX> o : result.getResult()) {
            TimestampUrlIDX t = o.getName();
            // LOG.warn("record loaded: {}", t);
            list.add(t);
        }
        
        return list;
        
    }
    
    // MESSAGE_URL_IDX
    @Override
    public void insertMessageUrlIDX(final byte[] digest, final String url) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_MESSAGE_URL_IDX, digest).putEmptyColumn(url);
        m.execute();
    }

	@Override
	public List<String> listUrlByMessageDigest(final byte[] digest)
			throws ConnectionException
	{
		ColumnList<String> columns;
		int pagesize = 100;
		List<String> urls = new ArrayList<String>();
		OperationResult<ColumnList<String>> result;
		RowQuery<byte[], String> query = keyspace
				.prepareQuery(CF_MESSAGE_URL_IDX).getKey(digest)
				.withColumnRange(new RangeBuilder().setLimit(pagesize).build())
				.autoPaginate(true);
		while (!(columns = query.execute().getResult()).isEmpty())
		{
			for (Column<String> c : columns)
			{
				String url = c.getName();
				urls.add(url);
			}
		}
		return urls;

	}

}
