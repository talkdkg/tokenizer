package com.test;

import java.util.Collection;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.ddl.ColumnFamilyDefinition;
import com.netflix.astyanax.ddl.FieldMetadata;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.Composite;
import com.netflix.astyanax.serializers.CompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import com.netflix.astyanax.test.EmbeddedCassandra;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Logger LOG = LoggerFactory.getLogger(AppTest.class);

    private static Keyspace keyspace;
    private static AstyanaxContext<Keyspace> keyspaceContext;
    private static EmbeddedCassandra cassandra;

    private static String TEST_CLUSTER_NAME = "bbm_sandbox";
    private static String TEST_KEYSPACE_NAME = "BbmChannelsUnitTests";
    private static String SCHEDULER_NAME_CF_NAME = "SchedulerQueue";

    private static final String SEEDS = "localhost:9160";

    private static final long CASSANDRA_WAIT_TIME = 3000;
    private static final int TTL = 20;
    private static final int TIMEOUT = 10;

    private static ColumnFamily<String, UUID> CF_CHAT_SESSIONS = ColumnFamily
            .newColumnFamily("CHANNEL_CHAT_SESSIONS", StringSerializer.get(),
                    TimeUUIDSerializer.get());
    private static ColumnFamily<String, UUID> CF_CHAT_SESSIONS_ARCHIVE = ColumnFamily
            .newColumnFamily("CF_CHAT_SESSIONS_ARCHIVE",
                    StringSerializer.get(), TimeUUIDSerializer.get());
    private static ColumnFamily<UUID, Composite> CF_SESSION_MESSAGES = new ColumnFamily<UUID, Composite>(
            "CHANNEL_CHAT_SESSION_MESSAGES", TimeUUIDSerializer.get(),
            CompositeSerializer.get());
    private static ColumnFamily<UUID, Composite> CF_SESSION_MESSAGES_SENDER_IDX = new ColumnFamily<UUID, Composite>(
            "CHANNEL_CHAT_SESSION_MESSAGES_SENDER_IDX",
            TimeUUIDSerializer.get(), CompositeSerializer.get());

    @BeforeClass
    public static void setup() throws Exception {
        System.out.println("TESTING THRIFT KEYSPACE");

        cassandra = new EmbeddedCassandra();
        cassandra.start();

        Thread.sleep(CASSANDRA_WAIT_TIME);

        createKeyspace();
    }

    @AfterClass
    public static void teardown() {
        if (keyspaceContext != null)
            keyspaceContext.shutdown();

        if (cassandra != null)
            cassandra.stop();
    }

    public static void createKeyspace() throws ConnectionException {

        keyspaceContext = new AstyanaxContext.Builder()
                .forCluster(TEST_CLUSTER_NAME)
                .forKeyspace(TEST_KEYSPACE_NAME)
                .withAstyanaxConfiguration(
                        new AstyanaxConfigurationImpl().setDiscoveryType(
                                NodeDiscoveryType.RING_DESCRIBE)
                                .setConnectionPoolType(
                                        ConnectionPoolType.TOKEN_AWARE))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl(TEST_CLUSTER_NAME
                                + "_" + TEST_KEYSPACE_NAME)
                                .setSocketTimeout(30000)
                                .setMaxTimeoutWhenExhausted(2000)
                                .setMaxConnsPerHost(20).setInitConnsPerHost(10)
                                .setSeeds(SEEDS))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        keyspaceContext.start();

        keyspace = keyspaceContext.getEntity();

        try {
            keyspace.dropKeyspace();
        } catch (Exception e) {

        }

        keyspace.createKeyspace(ImmutableMap
                .<String, Object> builder()
                .put("strategy_options",
                        ImmutableMap.<String, Object> builder()
                                .put("replication_factor", "1").build())
                .put("strategy_class", "SimpleStrategy").build());

        keyspace.createColumnFamily(CF_CHAT_SESSIONS, null);
        keyspace.createColumnFamily(CF_CHAT_SESSIONS_ARCHIVE, null);

        keyspace.createColumnFamily(
                CF_SESSION_MESSAGES,
                ImmutableMap
                        .<String, Object> builder()
                        .put("default_validation_class", "UTF8Type")
                        .put("key_validation_class", "TimeUUIDType")
                        .put("comparator_type",
                                "CompositeType(TimeUUIDType, IntegerType, UTF8Type)")
                        .build());

        keyspace.createColumnFamily(
                CF_SESSION_MESSAGES_SENDER_IDX,
                ImmutableMap
                        .<String, Object> builder()
                        .put("default_validation_class", "IntegerType")
                        .put("key_validation_class", "TimeUUIDType")
                        .put("comparator_type",
                                "CompositeType(UTF8Type, IntegerType, TimeUUIDType)")
                        .build());

        KeyspaceDefinition ki = keyspaceContext.getEntity().describeKeyspace();
        System.out.println("Describe Keyspace: " + ki.getName());
    }

    @Test
    public void getKeyspaceDefinition() throws Exception {
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

}
