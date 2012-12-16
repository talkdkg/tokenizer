package org.tokenizer.crawler.db;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ExceptionCallback;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
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
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.AllRowsQuery;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.recipes.reader.AllRowsReader;
import com.netflix.astyanax.serializers.BytesArraySerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class CrawlerRepositoryCassandraImpl implements CrawlerRepository {

    private static Logger LOG = LoggerFactory
            .getLogger(CrawlerRepositoryCassandraImpl.class);
    private final String clusterName = "WEB_CRAWL_CLUSTER";
    private final String keyspaceName = "WEB_CRAWL_KEYSPACE";
    private String seeds = "localhost:9160";
    private static final ColumnFamily<byte[], String> CF_URL_RECORDS = ColumnFamily
            .newColumnFamily("URL_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    private static final ColumnFamily<byte[], String> CF_WEBPAGE_RECORDS = ColumnFamily
            .newColumnFamily("WEBPAGE_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    private static final ColumnFamily<byte[], String> CF_XML_RECORDS = ColumnFamily
            .newColumnFamily("XML_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    private static final ColumnFamily<byte[], String> CF_MESSAGE_RECORDS = ColumnFamily
            .newColumnFamily("MESSAGE_RECORDS", BytesArraySerializer.get(),
                    StringSerializer.get());
    private static Keyspace keyspace;
    private static AstyanaxContext<Keyspace> keyspaceContext;
    public static ColumnFamily<String, String> CF_STANDARD1 = ColumnFamily
            .newColumnFamily("Standard1", StringSerializer.get(),
                    StringSerializer.get());

    @PostConstruct
    public void setup() throws ConnectionException, InterruptedException {
        keyspaceContext = new AstyanaxContext.Builder()
                .forCluster(clusterName)
                .forKeyspace(keyspaceName)
                .withAstyanaxConfiguration(
                        new AstyanaxConfigurationImpl().setDiscoveryType(
                                NodeDiscoveryType.RING_DESCRIBE)
                                .setConnectionPoolType(
                                        ConnectionPoolType.TOKEN_AWARE))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl(clusterName + "_"
                                + keyspaceName).setSocketTimeout(30000)
                                .setMaxTimeoutWhenExhausted(2000)
                                .setMaxConnsPerHost(20).setInitConnsPerHost(10)
                                .setSeeds(seeds))
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
        } catch (BadRequestException e) {
        }
        if (def == null) {
            keyspace.createKeyspace(ImmutableMap
                    .<String, Object> builder()
                    .put("strategy_options",
                            ImmutableMap.<String, Object> builder()
                                    .put("replication_factor", "1").build())
                    .put("strategy_class", "SimpleStrategy").build());
        }
        if (def.getColumnFamily("URL_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_URL_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "AsciiType")
                            .put("key_validation_class", "BytesType")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("url",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
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
                                                                    "URL_hostInverted")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("timestamp",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "DateType")
                                                            .build())
                                            .put("fetchAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "Int32Type")
                                                            .put("index_name",
                                                                    "URL_fetchAttemptCounter")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("httpResponseCode",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "Int32Type")
                                                            .put("index_name",
                                                                    "URL_httpResponseCode")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("webpageDigest",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build()).build())
                            .build());
        }
        if (def.getColumnFamily("WEBPAGE_RECORDS") == null) {
            keyspace.createColumnFamily(
                    CF_WEBPAGE_RECORDS,
                    ImmutableMap
                            .<String, Object> builder()
                            .put("default_validation_class", "BytesType")
                            .put("key_validation_class", "BytesType")
                            .put("compression_options",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("sstable_compression",
                                                    "SnappyCompressor")
                                            .put("chunk_length_kb", "64")
                                            .build())
                            // "{sstable_compression:SnappyCompressor, chunk_length_kb:64}")
                            .put("column_metadata",
                                    ImmutableMap
                                            .<String, Object> builder()
                                            .put("url",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "AsciiType")
                                                            .build())
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
                                                                    "WEBPAGE_hostInverted")
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
                                            .put("splitAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "Int32Type")
                                                            .put("index_name",
                                                                    "WEBPAGE_splitAttemptCounter")
                                                            .put("index_type",
                                                                    "KEYS")
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
                                                                    "XML_hostInverted")
                                                            .put("index_type",
                                                                    "KEYS")
                                                            .build())
                                            .put("content",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "BytesType")
                                                            .build())
                                            .put("parseAttemptCounter",
                                                    ImmutableMap
                                                            .<String, Object> builder()
                                                            .put("validation_class",
                                                                    "Int32Type")
                                                            .put("index_name",
                                                                    "XML_parseAttemptCounter")
                                                            .put("index_type",
                                                                    "KEYS")
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
        KeyspaceDefinition ki2 = keyspaceContext.getEntity().describeKeyspace();
        System.out.println("Describe Keyspace: " + ki2.getName());
        getKeyspaceDefinition();
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
            update(urlRecord);
        }
    }

    @Override
    public void update(final UrlRecord urlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_URL_RECORDS, urlRecord.getDigest())
                .putColumn("url", urlRecord.getUrl(), null)
                .putColumn("host", urlRecord.getHost(), null)
                .putColumn("hostInverted", urlRecord.getHostInverted(), null)
                .putColumn("timestamp", urlRecord.getTimestamp(), null)
                .putColumn("fetchAttemptCounter",
                        urlRecord.getFetchAttemptCounter(), null)
                .putColumn("httpResponseCode", urlRecord.getHttpResponseCode(),
                        null)
                .putColumn("webpageDigest", urlRecord.getWebpageDigest(), null);
        m.execute();
    }

    @Override
    public UrlRecords listUrlRecords(final int defaultPageSize)
            throws ConnectionException {
        AllRowsQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS).getAllRows()
                .setRowLimit(defaultPageSize);
        return new UrlRecords(countUrlRecords(), query);
    }

    @Override
    public UrlRecords listUrlRecords(final String host,
            final int defaultPageSize) throws ConnectionException {
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS).searchWithIndex()
                .setRowLimit(defaultPageSize).autoPaginateRows(true)
                .addExpression().whereColumn("host").equals().value(host);
        return new UrlRecords(countUrlRecords(), query);
    }

    @Override
    public UrlRecords listUrlRecords(final String host,
            final int httpResponseCode, final int defaultPageSize)
            throws ConnectionException {
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS).searchWithIndex()
                .setRowLimit(defaultPageSize).autoPaginateRows(true)
                .addExpression().whereColumn("host").equals().value(host)
                .addExpression().whereColumn("httpResponseCode").equals()
                .value(httpResponseCode);
        return new UrlRecords(countUrlRecords(), query);
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

    private void insert(final WebpageRecord webpageRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest())
                .putColumn("url", webpageRecord.getUrl(), null)
                .putColumn("host", webpageRecord.getHost(), null)
                .putColumn("hostInverted", webpageRecord.getHostInverted(),
                        null)
                .putColumn("timestamp", webpageRecord.getTimestamp(), null)
                .putColumn("charset", webpageRecord.getCharset(), null)
                .putColumn("content", webpageRecord.getContent(), null)
                .putColumn("splitAttemptCounter",
                        webpageRecord.getSplitAttemptCounter(), null);
        m.execute();
    }

    @Override
    public void updateSplitAttemptCounter(final WebpageRecord webpageRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_WEBPAGE_RECORDS, webpageRecord.getDigest()).putColumn(
                "splitAttemptCounter", webpageRecord.getSplitAttemptCounter(),
                null);
        m.execute();
    }

    @Override
    public void updateParseAttemptCounter(final XmlRecord xmlRecord)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_XML_RECORDS, xmlRecord.getDigest())
                .putColumn("parseAttemptCounter",
                        xmlRecord.getParseAttemptCounter(), null);
        m.execute();
    }

    @Override
    public WebpageRecord getWebpageRecord(final byte[] digest)
            throws ConnectionException {
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS).getKey(digest).execute();
        ColumnList<String> columns = result.getResult();
        if (columns.isEmpty())
            return null;
        String url = columns.getStringValue("url", null);
        String host = columns.getStringValue("host", null);
        byte[] hostInverted = columns.getByteArrayValue("hostInverted", null);
        Date timestamp = columns.getDateValue("timestamp", null);
        String charset = columns.getStringValue("charset", null);
        byte[] content = columns.getByteArrayValue("content", null);
        // /
        int splitAttemptCounter = columns.getIntegerValue(
                "splitAttemptCounter", 0);
        // /
        WebpageRecord webpageRecord = new WebpageRecord(digest, url, host,
                hostInverted, timestamp, charset, content, splitAttemptCounter);
        return webpageRecord;
    }

    @Override
    public WebpageRecords listWebpageRecords(final String host,
            final int splitAttemptCounter, final int defaultPageSize)
            throws ConnectionException {
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_WEBPAGE_RECORDS).searchWithIndex()
                .setRowLimit(defaultPageSize).autoPaginateRows(true)
                .addExpression().whereColumn("hostInverted").equals()
                .value(HttpUtils.getHostInverted(host)).addExpression()
                .whereColumn("splitAttemptCounter").equals()
                .value(splitAttemptCounter);
        return new WebpageRecords(
                countWebpageRecords(host, splitAttemptCounter), query);
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

    private void insert(final XmlRecord xmlRecord) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_MESSAGE_RECORDS, xmlRecord.getDigest())
                .putColumn("host", xmlRecord.getHost(), null)
                .putColumn("hostInverted", xmlRecord.getHostInverted(), null)
                .putColumn("content", xmlRecord.getContent(), null)
                .putColumn("parseAttemptCounter",
                        xmlRecord.getParseAttemptCounter(), null);
        m.execute();
    }

    @Override
    public XmlRecords listXmlRecords(final String host,
            final int parseAttemptCounter, final int defaultPageSize)
            throws ConnectionException {
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_XML_RECORDS).searchWithIndex()
                .setRowLimit(defaultPageSize).autoPaginateRows(true)
                .addExpression().whereColumn("hostInverted").equals()
                .value(HttpUtils.getHostInverted(host)).addExpression()
                .whereColumn("parseAttemptCounter").equals()
                .value(parseAttemptCounter);
        return new XmlRecords(countXmlRecords(host, parseAttemptCounter), query);
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
    }

    private void insert(final MessageRecord messageRecord)
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
        LOG.debug("Time taken to fetch " + counter + " rows is " + ""
                + timeTaken + " seconds.");
        return counter;
    }

    @Override
    public int countUrlRecords(final int httpResponseCode)
            throws ConnectionException {
        double nanoStart = System.nanoTime();
        OperationResult<Rows<byte[], String>> rows = keyspace
                .prepareQuery(CF_URL_RECORDS).searchWithIndex()
                .setRowLimit(10000).autoPaginateRows(true).addExpression()
                .whereColumn("httpResponseCode").equals()
                .value(httpResponseCode).execute();
        int counter = 0;
        for (Row<byte[], String> row : rows.getResult()) {
            counter++;
        }
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + counter + " rows is " + ""
                + timeTaken + " seconds.");
        return counter;
    }

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
                        // LOG.info("Got a row: " + row.getKey().toString());
                        return true;
                    }
                }).build().call();
        double timeTaken = (System.nanoTime() - nanoStart) / (1e9);
        LOG.debug("Time taken to fetch " + counter + " rows is " + ""
                + timeTaken + " seconds.");
        return counter.get();
    }

    public static void main(final String[] args) throws ConnectionException,
            UnsupportedEncodingException, InterruptedException {
        CrawlerRepositoryCassandraImpl o = new CrawlerRepositoryCassandraImpl();
        o.setup();
        UrlRecord home = new UrlRecord("http://www.tokenizer.ca");
        o.insertIfNotExists(home);
        OperationResult<ColumnList<String>> result = keyspace
                .prepareQuery(CF_URL_RECORDS)
                .getKey(MD5.digest("http://www.tokenizer.ca"
                        .getBytes("US-ASCII"))).execute();
        ColumnList<String> columns = result.getResult();
        String url = columns.getStringValue("url", null);
        OperationResult<ColumnList<String>> result2 = keyspace
                .prepareQuery(CF_URL_RECORDS).getKey(home.getDigest())
                .execute();
        ColumnList<String> columns2 = result2.getResult();
        String url2 = columns2.getStringValue("url", null);
        System.out.println(url + " " + url2 + " " + o.countUrlRecords());
    }

    @Override
    public UrlRecords listUrlRecords(final int httpResponseCode,
            final int defaultPageSize) throws ConnectionException {
        IndexQuery<byte[], String> query = keyspace
                .prepareQuery(CF_URL_RECORDS).searchWithIndex()
                .setRowLimit(defaultPageSize).autoPaginateRows(true)
                .addExpression().whereColumn("httpResponseCode").equals()
                .value(httpResponseCode);
        return new UrlRecords(countUrlRecords(httpResponseCode), query);
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
        LOG.debug("Time taken to fetch " + counter + " Webpage records is "
                + "" + timeTaken + " seconds.");
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
        LOG.debug("Time taken to fetch " + counter + " XML records is " + ""
                + timeTaken + " seconds.");
        return counter;
    }

    public void setSeeds(final String seeds) {
        this.seeds = seeds;
    }
}
