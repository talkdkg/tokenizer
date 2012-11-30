package com.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
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
import com.netflix.astyanax.model.Composite;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.BigIntegerSerializer;
import com.netflix.astyanax.serializers.CompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.astyanax.util.RangeBuilder;
import com.netflix.astyanax.util.TimeUUIDUtils;

public class CassandraUtils {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(CassandraUtils.class);
    private static final String TEST_CLUSTER_NAME = "TestCluster";
    private static final String TEST_KEYSPACE_NAME = "TestSessionMessages";
    public static final String SEEDS = "localhost:9160";
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

    private static Keyspace keyspace;
    private static AstyanaxContext<Keyspace> keyspaceContext;

    // private static EmbeddedCassandra cassandra;
    // private static AstyanaxContext<Cluster> clusterContext;

    // @BeforeClass
    public static void setup() throws ConnectionException, InterruptedException {

        // clusterContext = new AstyanaxContext.Builder()
        // .forCluster(TEST_CLUSTER_NAME)
        // .withAstyanaxConfiguration(
        // new AstyanaxConfigurationImpl()
        // .setDiscoveryType(NodeDiscoveryType.NONE))
        // .withConnectionPoolConfiguration(
        // new ConnectionPoolConfigurationImpl(TEST_CLUSTER_NAME)
        // .setMaxConnsPerHost(1).setSeeds(SEEDS))
        // .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
        // .buildCluster(ThriftFamilyFactory.getInstance());
        // clusterContext.start();

        // cassandra = new EmbeddedCassandra();
        // cassandra.start();

        createKeyspace();

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
        getKeyspaceDefinition();
    }

    public static void getKeyspaceDefinition() throws ConnectionException {
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

    public static void insert(final Message message) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        Composite cc = new Composite(message.getUuid(),
                message.getSequenceNumber(), message.getSenderId());
        keyspace.prepareColumnMutation(CF_SESSION_MESSAGES,
                message.getChatSessionUUID(), cc) //
                .putValue(message.getText(), null).execute();
    }

    public static void insertActiveChatSession(final ChatSession chatSession)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_CHAT_SESSIONS, chatSession.getChannelId()).putEmptyColumn(
                chatSession.getChatSessionUUID());
        m.execute();
    }

    public static void insertArchiveChatSession(final String channelId,
            final UUID uuid, Date date) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_CHAT_SESSIONS_ARCHIVE, channelId).putColumn(uuid, date);
        m.execute();
    }

    public static void deleteActiveChatSession(final String channelId,
            final UUID uuid) throws ConnectionException {
        keyspace.prepareColumnMutation(CF_CHAT_SESSIONS_ARCHIVE, channelId,
                uuid).deleteColumn().execute();
    }

    public static void updateActiveChatSession(final String channelId,
            final UUID uuid, Date date) throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_CHAT_SESSIONS, channelId).putColumn(uuid, date);
        m.execute();
    }

    public static void printSessionMessages(final ChatSession chatSession)
            throws ConnectionException {
        OperationResult<ColumnList<Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES) //
                .getKey(chatSession.getChatSessionUUID()) //
                .execute();
        ColumnList<Composite> columns = result.getResult();
        for (Column<Composite> column : columns) {
            Composite name = column.getName();
            Message message = new Message();
            UUID messageUUID = UUIDSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(0));
            message.setUuid(messageUUID);
            Integer sequenceNumber = BigIntegerSerializer.get()
                    .fromByteBuffer((java.nio.ByteBuffer) name.get(1))
                    .intValue();
            message.setSequenceNumber(sequenceNumber);
            String senderId = StringSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(2));
            message.setSenderId(senderId);
            String text = column.getStringValue();
            message.setText(text);
            System.out.println(message);
        }
    }

    public static List<UUID> getActiveChatSessionsUUIDs(String channelId)
            throws ConnectionException {
        OperationResult<ColumnList<UUID>> result = keyspace
                .prepareQuery(CF_CHAT_SESSIONS).getKey(channelId).execute();
        ColumnList<UUID> columns = result.getResult();
        List<UUID> list = new ArrayList<UUID>();
        for (Column<UUID> uuid : columns) {
            list.add(uuid.getName());
        }
        return list;
    }

    /**
     * Cassandra provides an API to count the number of columns in a reponse
     * without returning the query data. This is not a constant time operation
     * because Cassandra actually has to read the row and count the columns.
     * This will be optimized in a future version.
     * 
     * 
     * @param channelId
     * @return
     * @throws ConnectionException
     */
    public static int countActiveChatSessions(String channelId)
            throws ConnectionException {
        int count = keyspace.prepareQuery(CF_CHAT_SESSIONS).getKey(channelId)
                .getCount().execute().getResult();
        return count;
    }

    public static List<UUID> getActiveChatSessions(String channelId,
            Date start, Date end) throws ConnectionException {

        OperationResult<ColumnList<UUID>> result = keyspace
                .prepareQuery(CF_CHAT_SESSIONS)
                .getKey(channelId)
                .withColumnRange(
                        new RangeBuilder()

                                .setStart(
                                        TimeUUIDUtils.getTimeUUID(start
                                                .getTime()))
                                .setEnd(TimeUUIDUtils.getTimeUUID(end.getTime()))
                                .build()).execute();

        // .withColumnRange(range.build())

        ColumnList<UUID> columns = result.getResult();
        List<UUID> list = new ArrayList<UUID>();
        for (Column<UUID> uuid : columns) {
            list.add(uuid.getName());
        }
        return list;
    }

    public static List<UUID> getActiveChatSessions(String channelId, Date start)
            throws ConnectionException {

        OperationResult<ColumnList<UUID>> result = keyspace
                .prepareQuery(CF_CHAT_SESSIONS).getKey(channelId)
                .withColumnRange(new RangeBuilder()

                .setStart(TimeUUIDUtils.getTimeUUID(start.getTime())).build())
                .execute();

        // .withColumnRange(range.build())

        ColumnList<UUID> columns = result.getResult();
        List<UUID> list = new ArrayList<UUID>();
        for (Column<UUID> uuid : columns) {
            list.add(uuid.getName());
        }
        return list;
    }

    public static List<Message> getMessages(ChatSession chatSession)
            throws ConnectionException {

        List<Message> list = new ArrayList<Message>();

        OperationResult<ColumnList<Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES) //
                .getKey(chatSession.getChatSessionUUID()) //
                .execute();
        ColumnList<Composite> columns = result.getResult();
        for (Column<Composite> column : columns) {
            Composite name = column.getName();
            Message message = new Message();
            UUID messageUUID = UUIDSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(0));
            message.setUuid(messageUUID);
            Integer sequenceNumber = BigIntegerSerializer.get()
                    .fromByteBuffer((java.nio.ByteBuffer) name.get(1))
                    .intValue();
            message.setSequenceNumber(sequenceNumber);
            String senderId = StringSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(2));
            message.setSenderId(senderId);
            String text = column.getStringValue();
            message.setText(text);

            list.add(message);
        }
        return list;
    }

    public static List<Message> getMessageKeys(UUID channelId, String senderId,
            int start, int end) throws ConnectionException {

        CompositeSerializer cs = CompositeSerializer.get();
        ByteBuffer startBuffer = cs.toByteBuffer(new Composite(senderId, start,
                null));

        ByteBuffer endBuffer = cs.toByteBuffer(new Composite(senderId, end,
                null));

        OperationResult<ColumnList<Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES_SENDER_IDX)
                .getKey(channelId)
                .withColumnRange(startBuffer, endBuffer, false,
                        Integer.MAX_VALUE).execute();

        // .withColumnRange(range.build())

        List<Message> messageKeys = new ArrayList<Message>();

        ColumnList<Composite> columns = result.getResult();
        for (Column<Composite> column : columns) {
            Composite name = column.getName();

            Message message = new Message();
            UUID messageUUID = TimeUUIDSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(2));
            message.setUuid(messageUUID);
            Integer sequenceNumber = BigIntegerSerializer.get()
                    .fromByteBuffer((java.nio.ByteBuffer) name.get(1))
                    .intValue();
            message.setSequenceNumber(sequenceNumber);
            message.setSenderId(senderId);

            messageKeys.add(message);

        }
        return messageKeys;

    }

    public static List<Message> getMessages(Collection<UUID> sessions)
            throws ConnectionException {

        List<Message> list = new ArrayList<Message>();

        OperationResult<Rows<UUID, Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES) //
                .getKeySlice(sessions.toArray(new UUID[sessions.size()])) //
                .execute();

        for (Row<UUID, Composite> row : result.getResult()) {
            for (Column<Composite> column : row.getColumns()) {
                Composite name = column.getName();
                Message message = new Message();
                UUID messageUUID = UUIDSerializer.get().fromByteBuffer(
                        (java.nio.ByteBuffer) name.get(0));
                message.setUuid(messageUUID);
                Integer sequenceNumber = BigIntegerSerializer.get()
                        .fromByteBuffer((java.nio.ByteBuffer) name.get(1))
                        .intValue();
                message.setSequenceNumber(sequenceNumber);
                String senderId = StringSerializer.get().fromByteBuffer(
                        (java.nio.ByteBuffer) name.get(2));
                message.setSenderId(senderId);
                String text = column.getStringValue();
                message.setText(text);
                list.add(message);
            }
        }
        return list;
    }

    public static void updateMessageEvent(final Message message)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        Composite cc = new Composite(message.getSenderId(),
                message.getSequenceNumber(), message.getUuid());
        keyspace.prepareColumnMutation(CF_SESSION_MESSAGES_SENDER_IDX,
                message.getChatSessionUUID(), cc)
        //
                .putValue(message.getLastEvent().ordinal(), null).execute();
    }

    public static void updateMessageEventByUUID(UUID chatSessionUUID,
            final UUID uuid, MessageEvents event) throws ConnectionException {

        Composite start = new Composite(uuid, 0, "");

        Composite end = new Composite(uuid, Integer.MAX_VALUE, "");

        CompositeSerializer cs = CompositeSerializer.get();
        ByteBuffer startBuffer = cs.toByteBuffer(start);
        ByteBuffer endBuffer = cs.toByteBuffer(end);

        OperationResult<ColumnList<Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES)
                .getKey(chatSessionUUID)
                .withColumnRange(startBuffer, endBuffer, false,
                        Integer.MAX_VALUE).execute();

        ColumnList<Composite> cells = result.getResult();

        if (cells.size() > 1)
            throw new RuntimeException("Expected: 0 or 1 cells per UUID");

        Column<Composite> column = cells.iterator().next();

        Composite name = column.getName();
        UUID messageUUID = UUIDSerializer.get().fromByteBuffer(
                (java.nio.ByteBuffer) name.get(0));
        Integer sequenceNumber = BigIntegerSerializer.get()
                .fromByteBuffer((java.nio.ByteBuffer) name.get(1)).intValue();
        String senderId = StringSerializer.get().fromByteBuffer(
                (java.nio.ByteBuffer) name.get(2));

        MutationBatch m = keyspace.prepareMutationBatch();
        Composite cc = new Composite(senderId, sequenceNumber, messageUUID);
        keyspace.prepareColumnMutation(CF_SESSION_MESSAGES_SENDER_IDX,
                chatSessionUUID, cc).putValue(event.ordinal(), null).execute();

    }

    public static List<Message> getMessagesByKeys(UUID sessionUUID,
            Collection<Message> messageKeys) throws ConnectionException {

        List<Message> list = new ArrayList<Message>();

        Collection<Composite> columns = new ArrayList<Composite>();

        for (Message messageKey : messageKeys) {
            Composite column = new Composite(messageKey.getUuid(),
                    messageKey.getSequenceNumber(), messageKey.getSenderId());
            columns.add(column);

        }

        OperationResult<ColumnList<Composite>> result = keyspace
                .prepareQuery(CF_SESSION_MESSAGES) //
                .getKey(sessionUUID) //
                .withColumnSlice(columns).execute();

        ColumnList<Composite> cells = result.getResult();
        for (Column<Composite> column : cells) {
            Composite name = column.getName();
            Message message = new Message();
            UUID messageUUID = UUIDSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(0));
            message.setUuid(messageUUID);
            Integer sequenceNumber = BigIntegerSerializer.get()
                    .fromByteBuffer((java.nio.ByteBuffer) name.get(1))
                    .intValue();
            message.setSequenceNumber(sequenceNumber);
            String senderId = StringSerializer.get().fromByteBuffer(
                    (java.nio.ByteBuffer) name.get(2));
            message.setSenderId(senderId);
            String text = column.getStringValue();
            message.setText(text);

            list.add(message);

            // LOG.debug(message.toString());

        }
        return list;
    }

    public static void deleteChatSessionMessages(final UUID sessionUUID)
            throws ConnectionException {
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(CF_SESSION_MESSAGES, sessionUUID).delete();
        m.withRow(CF_SESSION_MESSAGES_SENDER_IDX, sessionUUID).delete();

        m.execute();
    }

}
