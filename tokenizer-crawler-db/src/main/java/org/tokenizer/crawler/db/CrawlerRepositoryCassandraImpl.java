package org.tokenizer.crawler.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.nutch.net.URLFilter;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.solr.SolrUtils;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.weblog.FetchedResultRecord;
import org.tokenizer.crawler.db.weblog.HostRecord;
import org.tokenizer.crawler.db.weblog.UrlHeadRecord;
import org.tokenizer.crawler.db.weblog.WeblogRecord;
import org.tokenizer.crawler.db.weblog.WeblogRecord.Weblog;
import org.tokenizer.util.io.JavaSerializationUtils;

import com.drew.lang.annotations.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.kaypok.FeatureExtraction;
import com.kaypok.Tools;
import com.kaypok.model.Sentence;
import com.kaypok.model.Text;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ExceptionCallback;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.RowCallback;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.FieldMetadata;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.recipes.ReverseIndexQuery;
import com.netflix.astyanax.recipes.Shards;
import com.netflix.astyanax.recipes.reader.AllRowsReader;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
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
public class CrawlerRepositoryCassandraImpl implements CrawlerRepository {

    protected static Logger LOG = LoggerFactory
            .getLogger(CrawlerRepositoryCassandraImpl.class);
    protected final String clusterName = "WEB_CRAWL_CLUSTER";
    protected final String keyspaceName = "WEB_CRAWL_KEYSPACE";
    protected String seeds = "127.0.0.1";
    protected int port = 19160;
    protected final ColumnFamily<byte[], String> CF_URL_RECORDS = ColumnFamily
            .newColumnFamily("URL_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    protected final ColumnFamily<byte[], String> CF_WEBPAGE_RECORDS = ColumnFamily
            .newColumnFamily("WEBPAGE_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    protected final ColumnFamily<byte[], String> CF_XML_RECORDS = ColumnFamily
            .newColumnFamily("XML_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    protected final ColumnFamily<byte[], String> CF_MESSAGE_RECORDS = ColumnFamily
            .newColumnFamily("MESSAGE_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());

    protected static AnnotatedCompositeSerializer<Weblog> WEBLOG_SERIALIZER = new AnnotatedCompositeSerializer<Weblog>(
            Weblog.class, 8192, false);

    protected static final ColumnFamily<Integer, Weblog> CF_WEBLOGS_RECORDS = ColumnFamily
            .newColumnFamily("WEBLOGS_RECORDS", IntegerSerializer.get(),
                    WEBLOG_SERIALIZER);

    private static final String WEBLOGS_RECORDS_IDX0 = "WEBLOGS_RECORDS_IDX0";
    private static final String SHARD_ = "SHARD_";
    private static final String SHARD_0 = "SHARD_0";

    // Index to find last sequence number (Weblog counter)
    protected final ColumnFamily<String, Integer> CF_WEBLOGS_RECORDS_IDX0 = ColumnFamily
            .newColumnFamily(WEBLOGS_RECORDS_IDX0, StringSerializer.get(),
                    IntegerSerializer.get());

    // TODO: buffer is set explicitly to 8192 due to current bug in Astyanax
    // https://github.com/Netflix/astyanax/pull/228#issuecomment-15250973
    protected static AnnotatedCompositeSerializer<FetchedResultRecord> FETCHED_RESULT_SERIALIZER = new AnnotatedCompositeSerializer<FetchedResultRecord>(
            FetchedResultRecord.class, 8192, false);

    protected static final ColumnFamily<String, FetchedResultRecord> CF_FETCHED_RESULT_RECORDS = ColumnFamily
            .newColumnFamily("FETCHED_RESULT_RECORDS", StringSerializer.get(),
                    FETCHED_RESULT_SERIALIZER);

    /**
     * This CF is "vertical" classic table to store HTTP HEAD response
     */
    protected final static String URL_HEAD_RECORDS = "URL_HEAD_RECORDS";
    protected final ColumnFamily<String, String> CF_URL_HEAD_RECORDS = ColumnFamily
            .newColumnFamily(URL_HEAD_RECORDS, StringSerializer.get(),
                    StringSerializer.get());

    // TDE-13: Index for Inverted Hosts
    private static final String HOST_RECORDS = "HOST_RECORDS";
    protected final ColumnFamily<String, byte[]> CF_HOST_RECORDS = ColumnFamily
            .newColumnFamily(HOST_RECORDS, StringSerializer.get(),
                    BytesArraySerializer.get());

    protected Keyspace keyspace;
    protected static AstyanaxContext<Keyspace> keyspaceContext;

    protected static final int MAX_ROWS = 1000;

    public CrawlerRepositoryCassandraImpl() {
    }

    CrawlerRepositoryCassandraImpl(int port) {
        this.port = port;
    }

    @PostConstruct
    public void setup() throws ConnectionException, InterruptedException {

        keyspaceContext = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace(keyspaceName)
                .withAstyanaxConfiguration(
                        new AstyanaxConfigurationImpl()
                                // .registerPartitioner(
                                // org.apache.cassandra.dht.ByteOrderedPartitioner.class
                                // .getCanonicalName(),
                                // MyBOPPartitioner.get())
                                .setDiscoveryType(
                                        NodeDiscoveryType.RING_DESCRIBE)
                                .setConnectionPoolType(
                                        ConnectionPoolType.TOKEN_AWARE))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl(clusterName + "_"
                                + keyspaceName).setSocketTimeout(600000)
                                .setMaxTimeoutWhenExhausted(60000)
                                .setMaxConnsPerHost(128)
                                .setInitConnsPerHost(16).setSeeds(seeds)
                                .setPort(port))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());
        keyspaceContext.start();
        keyspace = keyspaceContext.getEntity();
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
            keyspace.createKeyspace(ImmutableMap
                    .<String, Object> builder()
                    .put("strategy_options",
                            ImmutableMap.<String, Object> builder()
                                    .put("replication_factor", "1").build())
                    .put("strategy_class", "SimpleStrategy").build());
            try {
                def = keyspace.describeKeyspace();
            } catch (BadRequestException e) {
            }
        }
        if (def.getColumnFamily("URL_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_URL_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "AsciiType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "ALL")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("url",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("hostInverted_fetchAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("hostInverted_httpResponseCode",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("timestamp",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "DateType")
                                                            .build())
                                            .put("webpageDigest",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        if (def.getColumnFamily(URL_HEAD_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_URL_HEAD_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            // .put("default_validation_class", "AsciiType")
                            .put("key_validation_class", "AsciiType")
                            // .put("Caching", "ALL")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("fetchedUrl",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("fetchTime",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "LongType")
                                                            .build())
                                            .put("contentType",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("headers",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("newBaseUrl",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("numRedirects",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())

                                            .put("hostAddress",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("httpStatus",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "IntegerType")
                                                            .build())
                                            .put("reasonPhrase",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())

                                            .build())

                            .build());
        }
        if (def.getColumnFamily("WEBPAGE_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_WEBPAGE_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "ALL")
                            .put("compression_options",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("sstable_compression",
                                                    "SnappyCompressor")
                                            .put("chunk_length_kb", "64")
                                            .build())
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("url",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("hostInverted_splitAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("timestamp",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "DateType")
                                                            .build())
                                            .put("charset",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("xmlLinks",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        // ////////////
        // XML Records
        // ////////////
        if (def.getColumnFamily("XML_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_XML_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "ALL")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("timestamp",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "DateType")
                                                            .build())
                                            .put("hostInverted_parseAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        // //////////
        // Message Records
        // //////////
        if (def.getColumnFamily("MESSAGE_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_MESSAGE_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "UTF8Type")
                            .put("key_validation_class", "BytesType")
                            .put("Caching", "ALL")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("host",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
                                            .put("hostInverted",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .put("index_name",
                                                                    "MESSAGE_hostInverted")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("topic",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("date",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("author",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("age",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("sex",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("title",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build())
                                            .put("userRating",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "UTF8Type")
                                                            .build()).build())
                            .build());
        }

        // WEBLOGS_RECORDS
        if (def.getColumnFamily("WEBLOGS_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_WEBLOGS_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "IntegerType")
                            .put("comparator_type",
                                    "CompositeType(DateType, UTF8Type, UTF8Type)")
                            .build());

        }

        // CF_WEBLOGS_RECORDS_IDX0
        if (def.getColumnFamily("WEBLOGS_RECORDS_IDX0") == null) {
            keyspace.createColumnFamily(
                    CF_WEBLOGS_RECORDS_IDX0,
                    ImmutableMap.<String, Object> builder()
                            .put("key_validation_class", "UTF8Type")
                            .put("comparator_type", "IntegerType").build());

        }

        // FETCHED_RESULT_RECORDS
        if (def.getColumnFamily("FETCHED_RESULT_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_FETCHED_RESULT_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "UTF8Type")
                            // host inverted
                            .put("comparator_type",
                                    "CompositeType(UTF8Type, DateType)") // URL
                                                                         // +
                                                                         // Timestamp
                            .build());

        }

        // HOST_RECORDS
        if (def.getColumnFamily(HOST_RECORDS) == null) {
            keyspace.createColumnFamily(
                    CF_HOST_RECORDS,
                    ImmutableMap.<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            // TODO: will store host-specific info
                            .put("key_validation_class", "UTF8Type")
                            .put("comparator_type", "BytesType").build());

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
                    //@formatter:off
                    keyspace
                            .prepareQuery(CF_URL_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<byte[], String>() {
                                @Override
                                public void success(final Rows<byte[], String> rows) {
                                    for (Row<byte[], String> row : rows) {
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
        Thread messageRecordsReindexThread = new Thread(
                "messageRecordsReindexThread") {

            @Override
            public void run() {
                final AtomicLong counter = new AtomicLong();
                try {
                    // ArrayList<String> columns = new ArrayList<String>();
                    // columns.add("url");
                    //@formatter:off
                    keyspace
                            .prepareQuery(CF_MESSAGE_RECORDS)
                            .getAllRows()
                            .setRowLimit(100)                           
                            .setRepeatLastToken(true)
                             .executeWithCallback(new RowCallback<byte[], String>() {
                                @Override
                                public void success(final Rows<byte[], String> rows) {
                                    for (Row<byte[], String> row : rows) {
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
            System.out.println(field.getName() + " = "
                    + def.getFieldValue(field.getName()) + " ("
                    + field.getType() + ")");
        }
        for (ColumnFamilyDefinition cfDef : def.getColumnFamilyList()) {
            LOG.info("----------");
            for (FieldMetadata field : cfDef.getFieldsMetadata()) {
                LOG.info(field.getName() + " = "
                        + cfDef.getFieldValue(field.getName()) + " ("
                        + field.getType() + ")");
            }
        }
    }

    @Override
    public void insertIfNotExists(final UrlRecord urlRecord)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_URL_RECORDS).getKey(urlRecord.getDigest())
                .execute();
        if (result.getResult().isEmpty()) {
            insert(urlRecord);
            submitToSolr(urlRecord);
        }
    }

    @Override
    public void delete(final UrlRecord urlRecord) throws ConnectionException {
        deleteUrlRecord(urlRecord.getDigest());
        deleteWebpageRecord(urlRecord.getDigest());
    }

    private void deleteUrlRecord(final byte[] digest)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_RECORDS, digest).delete();
        m.execute();
    }

    private void deleteWebpageRecord(final byte[] webpageDigest)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        if (webpageDigest != null && webpageDigest.length > 0) {
            m = keyspace.prepareMutationBatch();
            m.withRow(CF_WEBPAGE_RECORDS, webpageDigest).delete();
            m.execute();
        }
    }

    @Override
    public void update(final UrlRecord urlRecord) throws ConnectionException {
        insert(urlRecord);
    }

    protected void insert(final UrlRecord urlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_RECORDS, urlRecord.getDigest())
                .putColumn("url", urlRecord.getUrl(), null)
                .putColumn(
                        "hostInverted_fetchAttemptCounter",
                        ArrayUtils.addAll(urlRecord.getHostInverted(),
                                urlRecord.getFetchAttemptCounterBytes()), null)
                .putColumn(
                        "hostInverted_httpResponseCode",
                        ArrayUtils.addAll(urlRecord.getHostInverted(),
                                urlRecord.getHttpResponseCodeBytes()), null)
                .putColumn("timestamp", urlRecord.getTimestamp(), null)
                .putColumn("webpageDigest", urlRecord.getWebpageDigest(), null);
        m.execute();
        LOG.debug("urlRecord updated: {}", urlRecord);
    }

    @Override
    public List<byte[]> loadUrlRecordRowKeys(final String host,
            final int httpResponseCode) throws ConnectionException {
        final List<byte[]> rowKeys = new ArrayList<byte[]>(MAX_ROWS);
        double nanoStart = System.nanoTime();
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_httpResponseCode = ArrayUtils.addAll(hostInverted,
                HttpUtils.intToBytes(httpResponseCode));
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_URL_RECORDS).searchWithIndex()
                .setRowLimit(MAX_ROWS).autoPaginateRows(false).addExpression()
                .whereColumn("hostInverted_httpResponseCode").equals()
                .value(hostInverted_httpResponseCode).execute();
        for (Row<byte[], String> row : rows.getResult()) {
            rowKeys.add(row.getKey());
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + rowKeys.size()
                + " URL Records row keys is " + timeTaken + " seconds.");
        return rowKeys;
    }

    @Override
    public List<UrlRecord> listUrlRecords(final String host,
            final int httpResponseCode, final int maxResults)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_httpResponseCode = ArrayUtils.addAll(hostInverted,
                HttpUtils.intToBytes(httpResponseCode));
        //@formatter:off
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex()
                .setRowLimit(maxResults)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted_httpResponseCode")
                .equals()
                .value(hostInverted_httpResponseCode);
        //@formatter:on
        OperationResult<Rows<byte[], String>> result = query.execute();
        if (LOG.isDebugEnabled()) {
            double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
            LOG.debug("Time taken to fetch " + result.getResult().size()
                    + " URL records is " + timeTaken + " seconds, host: {}",
                    host);
        }
        return toUrlRecordList(result);
    }

    @Override
    public List<UrlRecord> listUrlRecords(final String host,
            final int httpResponseCode, final byte[] startRowkey,
            final int count) throws ConnectionException {
        double nanoStart = System.nanoTime();
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_httpResponseCode = ArrayUtils.addAll(hostInverted,
                HttpUtils.intToBytes(httpResponseCode));
        LOG.debug("host: {}", host);
        LOG.debug("httpResponseCode: {}", httpResponseCode);
        LOG.debug("startRowkey: {}", Arrays.toString(startRowkey));
        LOG.debug("count: {}", count);
        //@formatter:off
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex()
                .setStartKey(startRowkey)
                .setRowLimit(count)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted_httpResponseCode")
                .equals()
                .value(hostInverted_httpResponseCode);
        //@formatter:on
        OperationResult<Rows<byte[], String>> result = query.execute();
        if (LOG.isDebugEnabled()) {
            double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
            LOG.debug("Time taken to fetch " + result.getResult().size()
                    + " URL records is " + timeTaken + " seconds, host: {}",
                    host);
        }
        return toUrlRecordList(result);
    }

    @Override
    public List<UrlRecord> listUrlRecordsByFetchAttemptCounter(
            final String host, final int fetchAttemptCounter,
            final int maxResults) throws ConnectionException {
        double nanoStart = System.nanoTime();
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_fetchAttemptCounter = ArrayUtils.addAll(
                hostInverted, HttpUtils.intToBytes(fetchAttemptCounter));
        //@formatter:off
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex()
                .setRowLimit(maxResults)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted_fetchAttemptCounter")
                .equals()
                .value(hostInverted_fetchAttemptCounter);
        //@formatter:on
        OperationResult<Rows<byte[], String>> result = query.execute();
        if (LOG.isDebugEnabled()) {
            double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
            LOG.debug("Time taken to fetch " + result.getResult().size()
                    + " URL records is " + timeTaken + " seconds, host: {}",
                    host);
        }
        return toUrlRecordList(result);
    }

    @Override
    public List<UrlRecord> listUrlRecords(final byte[][] keys)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> result = keyspace
                .prepareQuery(CF_URL_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size()
                + " URL records is " + timeTaken + " seconds.");
        return toUrlRecordList(result);
    }

    @Override
    public void insertIfNotExists(final WebpageRecord webpageRecord)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS)
                .getKey(webpageRecord.getDigest()).execute();
        if (result.getResult().isEmpty()) {
            insert(webpageRecord);
        }
    }

    protected void insert(final WebpageRecord webpageRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest())
                .putColumn("url", webpageRecord.getUrl(), null)
                .putColumn(
                        "hostInverted_splitAttemptCounter",
                        ArrayUtils.addAll(webpageRecord.getHostInverted(),
                                HttpUtils.intToBytes(webpageRecord
                                        .getSplitAttemptCounter())), null)
                .putColumn("timestamp", webpageRecord.getTimestamp(), null)
                .putColumn("charset", webpageRecord.getCharset(), null)
                .putColumn("content", webpageRecord.getContent(), null)
                .putColumn(
                        "xmlLinks",
                        JavaSerializationUtils.serialize(webpageRecord
                                .getXmlLinks()), null);
        m.execute();
        LOG.debug("webpageRecord inserted: {}", webpageRecord);
    }

    @Override
    public void updateSplitAttemptCounterAndLinks(
            final WebpageRecord webpageRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest())
                .putColumn(
                        "hostInverted_splitAttemptCounter",
                        ArrayUtils.addAll(webpageRecord.getHostInverted(),
                                HttpUtils.intToBytes(webpageRecord
                                        .getSplitAttemptCounter())), null)
                .putColumn(
                        "xmlLinks",
                        JavaSerializationUtils.serialize(webpageRecord
                                .getXmlLinks()), null);
        m.execute();
    }

    @Override
    public void updateParseAttemptCounter(final XmlRecord xmlRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_XML_RECORDS, xmlRecord.getDigest()).putColumn(
                "hostInverted_parseAttemptCounter",
                ArrayUtils.addAll(xmlRecord.getHostInverted(), HttpUtils
                        .intToBytes(xmlRecord.getParseAttemptCounter())), null);
        m.execute();
    }

    @Override
    public UrlRecord getUrlRecord(final byte[] digest)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_URL_RECORDS).getKey(digest).execute();
        ColumnList<String> columns = result.getResult();
        return toUrlRecord(digest, columns);
    }

    @Override
    public WebpageRecord getWebpageRecord(final byte[] digest)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS).getKey(digest).execute();
        ColumnList<String> columns = result.getResult();
        return toWebpageRecord(digest, columns);
    }

    @Override
    public List<WebpageRecord> listWebpageRecords(final String host,
            final int splitAttemptCounter, final int maxResults)
            throws ConnectionException {
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_splitAttemptCounter = ArrayUtils.addAll(
                hostInverted, HttpUtils.intToBytes(splitAttemptCounter));
        LOG.debug(Arrays.toString(hostInverted_splitAttemptCounter));
        //@formatter:off
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex()
                .setRowLimit(maxResults)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted_splitAttemptCounter")
                .equals()
                .value(hostInverted_splitAttemptCounter);
        //@formatter:on
        OperationResult<Rows<byte[], String>> result = query.execute();
        return toWebpageRecordList(result);
    }

    @Override
    public void insertIfNotExist(final XmlRecord xmlRecord)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_XML_RECORDS).getKey(xmlRecord.getDigest())
                .execute();
        if (result.getResult().isEmpty()) {
            insert(xmlRecord);
        }
    }

    protected void insert(final XmlRecord xmlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_XML_RECORDS, xmlRecord.getDigest())
                .putColumn("timestamp", xmlRecord.getTimestamp(), null)
                .putColumn("content", xmlRecord.getContent(), null)
                .putColumn(
                        "hostInverted_parseAttemptCounter",
                        ArrayUtils.addAll(xmlRecord.getHostInverted(),
                                HttpUtils.intToBytes(xmlRecord
                                        .getParseAttemptCounter())), null);
        m.execute();
    }

    @Override
    public List<XmlRecord> listXmlRecords(final String host,
            final int parseAttemptCounter, final int maxResults)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        byte[] hostInverted = HttpUtils.getHostInverted(host);
        byte[] hostInverted_parseAttemptCounter = ArrayUtils.addAll(
                hostInverted, HttpUtils.intToBytes(parseAttemptCounter));
        //@formatter:off
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_XML_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                .searchWithIndex()
                .setRowLimit(maxResults)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted_parseAttemptCounter")
                .equals()
                .value(hostInverted_parseAttemptCounter);
        //@formatter:on
        OperationResult<Rows<byte[], String>> result = query.execute();
        if (LOG.isDebugEnabled()) {
            double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
            LOG.debug("Time taken to fetch " + result.getResult().size()
                    + " XML records is " + timeTaken + " seconds, host: {}",
                    host);
        }
        return toXmlRecordList(result);
    }

    @Override
    public List<XmlRecord> listXmlRecords(final byte[][] keys)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> result = keyspace
                .prepareQuery(CF_XML_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size()
                + " URL records is " + timeTaken + " seconds.");
        return toXmlRecordList(result);
    }

    @Override
    public List<XmlRecord> listXmlRecords(final ArrayList<byte[]> xmlLinks)
            throws ConnectionException {
        byte[][] keys = new byte[xmlLinks.size()][];
        return listXmlRecords(xmlLinks.toArray(keys));
    }

    @Override
    public List<MessageRecord> listMessageRecords(final byte[][] keys)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> result = keyspace
                .prepareQuery(CF_MESSAGE_RECORDS).getKeySlice(keys).execute();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + result.getResult().size()
                + " URL records is " + timeTaken + " seconds.");
        return toMessageRecordList(result);
    }

    @Override
    public List<MessageRecord> listMessageRecords(
            final ArrayList<byte[]> xmlLinks) throws ConnectionException {
        byte[][] keys = new byte[xmlLinks.size()][];
        return listMessageRecords(xmlLinks.toArray(keys));
    }

    @Override
    public void insertIfNotExists(final MessageRecord messageRecord)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_MESSAGE_RECORDS)
                .getKey(messageRecord.getDigest()).execute();
        if (result.getResult().isEmpty()) {
            insert(messageRecord);
        }
        submitToSolr(messageRecord);
    }

    protected void insert(final MessageRecord messageRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_MESSAGE_RECORDS, messageRecord.getDigest())
                .putColumn("host", messageRecord.getHost(), null)
                .putColumn("hostInverted", messageRecord.getHostInverted(),
                        null)
                .putColumn("topic", messageRecord.getTopic(), null)
                .putColumn("date", messageRecord.getDate(), null)
                .putColumn("author", messageRecord.getAuthor(), null)
                .putColumn("age", messageRecord.getAge(), null)
                .putColumn("sex", messageRecord.getSex(), null)
                .putColumn("title", messageRecord.getTitle(), null)
                .putColumn("content", messageRecord.getContent(), null)
                .putColumn("userRating", messageRecord.getUserRating(), null);
        m.execute();
    }

    @Override
    public int countUrlRecords() throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_URL_RECORDS).getAllRows().setRowLimit(10000)
                .setExceptionCallback(new ExceptionCallback() {

                    @Override
                    public boolean onException(final ConnectionException e) {
                        e.printStackTrace();
                        return true;
                    }
                }).execute();
        int counter = 0;
        for (Row<byte[], String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " rows is " + timeTaken
                + " seconds.");
        return counter;
    }

    @Override
    public int countUrlRecords(final String host, final int httpResponseCode)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_URL_RECORDS)
                .searchWithIndex()
                .setRowLimit(MAX_ROWS)
                .autoPaginateRows(false)
                .addExpression()
                .whereColumn("hostInverted")
                .equals()
                .value(HttpUtils.getHostInverted(host))
                .addExpression()
                .whereColumn("httpResponseCode")
                .equals()
                .value(httpResponseCode)
                .withColumnRange(
                        new RangeBuilder().setStart("").setLimit(0).build())
                .execute();
        int counter = 0;
        for (Row<byte[], String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " rows is " + timeTaken
                + " seconds.");
        return counter;
    }

    /**
     * This is super fast possibly because it is multithreaded. Unfortunately,
     * iimplemented for all records (no index support).
     * 
     * @return
     * @throws Exception
     */
    public long countUrlRecords2() throws Exception {
        final AtomicLong counter = new AtomicLong(0);
        double nanoStart = System.nanoTime();
        boolean result = new AllRowsReader.Builder<byte[], String>(keyspace,
                CF_URL_RECORDS)
                .forEachRow(new Function<Row<byte[], String>, Boolean>() {

                    @Override
                    public Boolean apply(@Nullable
                    final Row<byte[], String> row) {
                        counter.incrementAndGet();
                        return true;
                    }
                }).build().call();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " rows is " + timeTaken
                + " seconds.");
        return counter.get();
    }

    @Override
    public int countWebpageRecords(final String host,
            final int splitAttemptCounter) throws ConnectionException {
        LOG.trace(
                "countWebpageRecords called... host: {}, splitAttemptCounter: {}",
                host, splitAttemptCounter);
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS).searchWithIndex()
                .setRowLimit(10000).autoPaginateRows(true).addExpression()
                .whereColumn("hostInverted").equals()
                .value(HttpUtils.getHostInverted(host)).addExpression()
                .whereColumn("splitAttemptCounter").equals()
                .value(splitAttemptCounter).execute();
        int counter = 0;
        // for (Row<byte[], String> row : rows.getResult()) {
        // counter++;
        // }
        counter = rows.getResult().size();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " Webpage records is "
                + timeTaken + " seconds.");
        return counter;
    }

    @Override
    public int countXmlRecords(final String host, final int parseAttemptCounter)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_XML_RECORDS).searchWithIndex()
                .setRowLimit(10000).autoPaginateRows(true).addExpression()
                .whereColumn("hostInverted").equals()
                .value(HttpUtils.getHostInverted(host)).addExpression()
                .whereColumn("parseAttemptCounter").equals()
                .value(parseAttemptCounter).execute();
        int counter = 0;
        for (Row<byte[], String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to count " + counter + " XML records is "
                + timeTaken + " seconds.");
        return counter;
    }

    public void setSeeds(final String seeds) {
        this.seeds = seeds;
    }

    protected static UrlRecord toUrlRecord(final Row<byte[], String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toUrlRecord(digest, columns);
    }

    protected static UrlRecord toUrlRecord(final byte[] digest,
            final ColumnList<String> columns) {
        if (columns.isEmpty())
            return null;
        String url = columns.getStringValue("url", null);
        byte[] hostInverted_fetchAttemptCounter = columns.getByteArrayValue(
                "hostInverted_fetchAttemptCounter", null);
        byte[] hostInverted_httpResponseCode = columns.getByteArrayValue(
                "hostInverted_httpResponseCode", null);
        Date timestamp = columns.getDateValue("timestamp", null);
        byte[] webpageDigest = columns.getByteArrayValue("webpageDigest",
                new byte[0]);
        UrlRecord urlRecord = new UrlRecord(digest, url,
                hostInverted_fetchAttemptCounter,
                hostInverted_httpResponseCode, timestamp, webpageDigest);
        return urlRecord;
    }

    protected static List<UrlRecord> toUrlRecordList(
            final OperationResult<Rows<byte[], String>> result) {
        List<UrlRecord> list = new ArrayList<UrlRecord>();
        for (Row<byte[], String> row : result.getResult()) {
            list.add(toUrlRecord(row));
        }
        return list;
    }

    protected static WebpageRecord toWebpageRecord(final Row<byte[], String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toWebpageRecord(digest, columns);
    }

    protected static WebpageRecord toWebpageRecord(final byte[] digest,
            final ColumnList<String> columns) {
        if (columns.isEmpty())
            return null;
        String url = columns.getStringValue("url", null);
        byte[] hostInverted_splitAttemptCounter = columns.getByteArrayValue(
                "hostInverted_splitAttemptCounter", null);
        Date timestamp = columns.getDateValue("timestamp", null);
        String charset = columns.getStringValue("charset", null);
        byte[] content = columns.getByteArrayValue("content", null);
        byte[] xmlLinksSerialized = columns.getByteArrayValue("xmlLinks", null);
        ArrayList<byte[]> xmlLinks = (ArrayList<byte[]>) JavaSerializationUtils
                .deserialize(xmlLinksSerialized);
        if (hostInverted_splitAttemptCounter == null
                || hostInverted_splitAttemptCounter.length < 5) {
            LOG.error(
                    "Incorrect hostInverted_splitAttemptCounter with size less than 5; url: {}",
                    url);
            // trying to recover from previously non-indexed field:
            return new WebpageRecord(url, timestamp, charset, content, xmlLinks);
        }
        return new WebpageRecord(digest, url, hostInverted_splitAttemptCounter,
                timestamp, charset, content, xmlLinks);
    }

    private static List<WebpageRecord> toWebpageRecordList(
            final OperationResult<Rows<byte[], String>> result) {
        List<WebpageRecord> list = new ArrayList<WebpageRecord>();
        for (Row<byte[], String> row : result.getResult()) {
            list.add(toWebpageRecord(row));
        }
        return list;
    }

    protected static XmlRecord toXmlRecord(final byte[] digest,
            final ColumnList<String> columns) {
        if (columns.isEmpty()) {
            LOG.error("Columns are empty for XML record with digest "
                    + Arrays.toString(digest));
            return null;
        }
        Date timestamp = columns.getDateValue("timestamp", null);
        byte[] hostInverted_parseAttemptCounter = columns.getByteArrayValue(
                "hostInverted_parseAttemptCounter", null);
        byte[] content = columns.getByteArrayValue("content", null);
        XmlRecord xmlRecord = new XmlRecord(digest, timestamp,
                hostInverted_parseAttemptCounter, content);
        LOG.trace(xmlRecord.toString());
        return xmlRecord;
    }

    protected static XmlRecord toXmlRecord(final Row<byte[], String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toXmlRecord(digest, columns);
    }

    protected static List<XmlRecord> toXmlRecordList(
            final OperationResult<Rows<byte[], String>> result) {
        List<XmlRecord> list = new ArrayList<XmlRecord>();
        for (Row<byte[], String> row : result.getResult()) {
            list.add(toXmlRecord(row));
        }
        return list;
    }

    // /
    protected static MessageRecord toMessageRecord(final byte[] digest,
            final ColumnList<String> columns) {
        if (columns.isEmpty()) {
            LOG.error("Columns are empty for Message record with digest "
                    + Arrays.toString(digest));
            return null;
        }
        String host = columns.getStringValue("host", null);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted", null);
        String topic = columns.getStringValue("topic", null);
        String date = columns.getStringValue("date", null);
        String author = columns.getStringValue("author", null);
        String age = columns.getStringValue("age", null);
        String sex = columns.getStringValue("sex", null);
        String title = columns.getStringValue("title", null);
        String content = columns.getStringValue("content", null);
        String userRating = columns.getStringValue("userRating", null);
        MessageRecord messageRecord = new MessageRecord(digest, host,
                hostInverted, topic, date, author, age, sex, title, content,
                userRating);
        return messageRecord;
    }

    protected static MessageRecord toMessageRecord(final Row<byte[], String> row) {
        byte[] digest = row.getKey();
        ColumnList<String> columns = row.getColumns();
        return toMessageRecord(digest, columns);
    }

    protected static List<MessageRecord> toMessageRecordList(
            final OperationResult<Rows<byte[], String>> result) {
        List<MessageRecord> list = new ArrayList<MessageRecord>();
        for (Row<byte[], String> row : result.getResult()) {
            list.add(toMessageRecord(row));
        }
        return list;
    }

    protected void reindex() {
        final AtomicLong counter = new AtomicLong();
        try {
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("url");
            //@formatter:off
            keyspace
                    .prepareQuery(CF_WEBPAGE_RECORDS)
                    .getAllRows()
                    .setRowLimit(100)
                    .setRepeatLastToken(true)
                    .withColumnSlice(columns)
                    .executeWithCallback(new RowCallback<byte[], String>() {
                        @Override
                        public void success(final Rows<byte[], String> rows) {
                            for (Row<byte[], String> row : rows) {
                                String url = row.getColumns().getStringValue("url",null);
                                String host = HttpUtils.getHost(url);
                                byte[] hostInverted = HttpUtils.getHostInverted(host);

                                byte[] hostInverted_splitAttemptCounter =  ArrayUtils.addAll(
                                        hostInverted,
                                        new byte[] {0,0,0,0});
                                MutationBatch m = keyspace.prepareMutationBatch();
                                m.withRow(CF_WEBPAGE_RECORDS, row.getKey())
                                        .putColumn(
                                                "hostInverted_splitAttemptCounter",
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
            //@formatter:off
            keyspace
                    .prepareQuery(CF_URL_RECORDS)
                    .getAllRows()
                    .setRowLimit(100)
                    .setRepeatLastToken(true)
                    .withColumnSlice(columns)
                    .executeWithCallback(new RowCallback<byte[], String>() {
                        @Override
                        public void success(final Rows<byte[], String> rows) {
                            for (Row<byte[], String> row : rows) {
                                String url = row.getColumns().getStringValue("url",null);
                                if (!host.equals(HttpUtils.getHost(url))) {
                                    continue;
                                }
                                byte[] webpageDigest = row.getColumns().getByteArrayValue("webpageDigest",null);
                                if (urlFilter.filter(url) == null) {
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
        LOG.warn("{}: total {} filtered records deleted... ", host,
                counter.get());
    }

    private void submitToSolr(final UrlRecord urlRecord) {
        SolrServer solrServer = SolrUtils.getSolrServer();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", MD5.toHexString(urlRecord.getDigest()));
        doc.addField("host", urlRecord.getHost());
        doc.addField("url", urlRecord.getUrl());
        doc.addField("httpResponseCode", urlRecord.getHttpResponseCode());
        try {
            solrServer.add(doc);
        } catch (SolrServerException e) {
            LOG.error("", e);
        } catch (IOException e) {
            LOG.error("", e);
        }
        // solrServer.commit();
    }

    private void submitToSolr(final MessageRecord messageRecord) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", MD5.toHexString(messageRecord.getDigest()));
        doc.addField("host_s", messageRecord.getHost());
        doc.addField("content_en", messageRecord.getContent());
        doc.addField("age_ti",
                messageRecord.getAge().equals(DefaultValues.EMPTY_STRING) ? 0
                        : messageRecord.getAge());
        doc.addField("author_en", messageRecord.getAuthor());
        doc.addField("date_tdt", messageRecord.getISO8601Date());
        doc.addField("sex_s", messageRecord.getSex());
        doc.addField("title_en", messageRecord.getTitle());
        doc.addField("topic_en", messageRecord.getTopic());
        doc.addField("userRating_s", messageRecord.getUserRating());

        Text text = FeatureExtraction.process(messageRecord.getContent());

        Set<String> features = new HashSet<String>();

        for (Sentence s : text.getSentences()) {
            LOG.debug("sentence: {}", s);
            if (s.getFeatures() != null) {
                features.addAll(s.getFeatures());
            }
        }

        doc.addField("feature_ss", features);

        int sentiment = 0;
        for (Sentence s : text.getSentences()) {
            if (s.getFeatures() != null) {
                sentiment = sentiment
                        + Tools.prunTree(s.getSentence(), s.getFeatures(),
                                s.getTreebank(), s.getChunks());
            }
        }

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
    public void insert(final WeblogRecord weblogsRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        for (Weblog weblog : weblogsRecord.getWeblogs()) {
            m.withRow(CF_WEBLOGS_RECORDS, weblogsRecord.getCount())
                    .putEmptyColumn(weblog);
        }

        m.withRow(CF_WEBLOGS_RECORDS_IDX0, SHARD_0).putEmptyColumn(
                weblogsRecord.getCount());

        m.execute();
    }

    public WeblogRecord getLastWeblogRecordSample() throws ConnectionException {

        final WeblogRecord weblogRecord = new WeblogRecord();
        final List<WeblogRecord.Weblog> weblogs = new ArrayList<WeblogRecord.Weblog>();
        weblogRecord.setWeblogs(weblogs);

        ReverseIndexQuery
                .newQuery(keyspace, CF_WEBLOGS_RECORDS, WEBLOGS_RECORDS_IDX0,
                        IntegerSerializer.get())
                .withIndexShards(
                        new Shards.StringShardBuilder().setPrefix(SHARD_)
                                .setShardCount(1).build())
                .withConsistencyLevel(ConsistencyLevel.CL_ONE)
                .fromIndexValue(0)
                .toIndexValue(Integer.MAX_VALUE)

                .forEach(
                        new Function<Row<Integer, WeblogRecord.Weblog>, Void>() {

                            @Override
                            public Void apply(
                                    Row<Integer, WeblogRecord.Weblog> row) {

                                weblogRecord.setCountInverted(row.getKey());
                                for (Column<WeblogRecord.Weblog> weblogColumn : row
                                        .getColumns()) {
                                    WeblogRecord.Weblog weblog = weblogColumn
                                            .getName();
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

        OperationResult<ColumnList<WeblogRecord.Weblog>> result = keyspace
                .prepareQuery(CF_WEBLOGS_RECORDS).getKey(count)
                .autoPaginate(true).execute();

        ColumnList<WeblogRecord.Weblog> columns = result.getResult();
        for (Column<WeblogRecord.Weblog> weblogColumn : columns) {
            WeblogRecord.Weblog weblog = weblogColumn.getName();
            weblogs.add(weblog);
        }

        return weblogRecord;

    }

    public int getLastWeblogCounter() throws ConnectionException {

        OperationResult<ColumnList<Integer>> result = keyspace
                .prepareQuery(CF_WEBLOGS_RECORDS_IDX0)
                .getKey(SHARD_0)
                .autoPaginate(false)
                .withColumnRange(
                        new RangeBuilder().setReversed(true).setLimit(1)
                                .build()).execute();

        return result.getResult().getColumnNames().iterator().next();

    }

    @Override
    public void insert(final FetchedResultRecord fetchedResultRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_FETCHED_RESULT_RECORDS, fetchedResultRecord.getHost())
                .putColumn(fetchedResultRecord,
                        JavaSerializationUtils.serialize(fetchedResultRecord));
        m.execute();

        insertIfNotExists(new HostRecord(fetchedResultRecord.getHost()));

    }

    @Override
    public void insert(final HostRecord hostRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_HOST_RECORDS, hostRecord.getTld()).putColumn(
                hostRecord.getHostInverted(),
                JavaSerializationUtils.serialize(hostRecord));
        m.execute();
    }

    @Override
    public void insertIfNotExists(final HostRecord hostRecord)
            throws ConnectionException {
        OperationResult<ColumnList<byte[]>> result = keyspace
                .prepareQuery(CF_HOST_RECORDS).getKey(hostRecord.getTld())
                .withColumnSlice(hostRecord.getHostInverted()).execute();
        if (result.getResult().isEmpty()) {
            insert(hostRecord);
        }
    }

    @Override
    public Collection<String> listTLDs() throws ConnectionException {
        OperationResult<Rows<String, byte[]>> result = keyspace
                .prepareQuery(CF_HOST_RECORDS)
                .setConsistencyLevel(ConsistencyLevel.CL_ONE).getAllRows()
                .withColumnRange(new RangeBuilder().setLimit(0).build())
                .execute();
        Set list = new TreeSet<String>();
        for (Row<String, byte[]> row : result.getResult()) {
            list.add(row.getKey());
        }
        return list;
    }

    @Override
    public int countHosts(String tld) {
        OperationResult<ColumnList<byte[]>> result;
        try {
            result = keyspace.prepareQuery(CF_HOST_RECORDS)
                    .setConsistencyLevel(ConsistencyLevel.CL_ONE).getKey(tld)
                    .execute();
            int size = result.getResult().size();
            LOG.info("TLD: {}; Columns size: {}", tld, size);
            return size;
        } catch (ConnectionException e) {
            LOG.error("", e);
            return 0;
        }
    }

    @Override
    public List<HostRecord> listHostRecords(String tld, final int startIndex,
            final int count) {
        ColumnList<byte[]> columns;
        int pagesize = 10;
        int pointer = -1;
        int endIndex = startIndex + count;
        List<HostRecord> hostRecords = new ArrayList<HostRecord>();
        OperationResult<ColumnList<byte[]>> result;
        try {
            RowQuery<String, byte[]> query = keyspace
                    .prepareQuery(CF_HOST_RECORDS)
                    .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                    .getKey(tld)
                    .withColumnRange(
                            new RangeBuilder().setLimit(pagesize).build())
                    .autoPaginate(true);

            while (!(columns = query.execute().getResult()).isEmpty()) {
                LOG.info("paginated query: TLD: {}; columns size: {}", tld,
                        columns.size());
                for (Column<byte[]> c : columns) {
                    // LOG.info(Long.toString(c.getName()));
                    pointer++;
                    if (pointer >= startIndex && pointer < endIndex) {
                        HostRecord hostRecord = (HostRecord) JavaSerializationUtils
                                .deserialize(c.getByteArrayValue());
                        hostRecords.add(hostRecord);
                    }
                }
                // column = Iterables.getLast(columns).getName() + "\u0000";
                if (pointer >= endIndex)
                    break;
            }
            return hostRecords;
        } catch (ConnectionException e) {
            LOG.error("", e);
        }

        return null;

    }

    @Override
    public List<FetchedResultRecord> listFetchedResultRecords(String host) {
        ColumnList<FetchedResultRecord> columns;
        int pagesize = 10;
        List<FetchedResultRecord> fetchedResultRecords = new ArrayList<FetchedResultRecord>();
        OperationResult<ColumnList<byte[]>> result;
        try {
            RowQuery<String, FetchedResultRecord> query = keyspace
                    .prepareQuery(CF_FETCHED_RESULT_RECORDS)
                    .setConsistencyLevel(ConsistencyLevel.CL_ONE)
                    .getKey(host)
                    .withColumnRange(
                            new RangeBuilder().setLimit(pagesize).build())
                    .autoPaginate(true);
            while (!(columns = query.execute().getResult()).isEmpty()) {
                for (Column<FetchedResultRecord> c : columns) {
                    FetchedResultRecord fetchedResultRecord = (FetchedResultRecord) JavaSerializationUtils
                            .deserialize(c.getByteArrayValue());
                    fetchedResultRecords.add(fetchedResultRecord);
                }
            }
            return fetchedResultRecords;
        } catch (ConnectionException e) {
            LOG.error("", e);
        }

        return null;

    }

    protected void insert(final UrlHeadRecord urlHeadRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_HEAD_RECORDS, urlHeadRecord.getBaseUrl())

                .putColumn("fetchedUrl", urlHeadRecord.getFetchedUrl(), null)
                .putColumn("fetchTime", urlHeadRecord.getFetchTime(), null)
                .putColumn("contentType", urlHeadRecord.getContentType(), null)
                .putColumn(
                        "headers",
                        JavaSerializationUtils.serialize(urlHeadRecord
                                .getHeaders()), null)
                .putColumn("newBaseUrl", urlHeadRecord.getNewBaseUrl(), null)
                .putColumn("numRedirects", urlHeadRecord.getNumRedirects(),
                        null)
                .putColumn("hostAddress", urlHeadRecord.getHostAddress(), null)
                .putColumn("httpStatus", urlHeadRecord.getHttpStatus(), null)
                .putColumn("reasonPhrase", urlHeadRecord.getReasonPhrase(),
                        null);

        m.execute();
        LOG.debug("urlHeadRecord inserted: {}", urlHeadRecord);
    }

    @Override
    public void insertIfNotExists(final UrlHeadRecord urlHeadRecord)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_URL_HEAD_RECORDS)
                .getKey(urlHeadRecord.getBaseUrl()).execute();
        if (result.getResult().isEmpty()) {
            insert(urlHeadRecord);
        }
    }
    

    
}
