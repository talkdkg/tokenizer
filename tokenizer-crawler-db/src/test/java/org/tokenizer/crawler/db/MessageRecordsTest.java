package org.tokenizer.crawler.db;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.cassandra.thrift.Cassandra;
import org.junit.*;
import static org.junit.Assert.*;
import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.Execution;
import com.netflix.astyanax.KeyspaceTracerFactory;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.connectionpool.ConnectionFactory;
import com.netflix.astyanax.connectionpool.ConnectionPool;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.ConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.BagOfConnectionsConnectionPoolImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.retry.RetryPolicy;
import com.netflix.astyanax.retry.RunOnce;
import com.netflix.astyanax.serializers.AsciiSerializer;
import com.netflix.astyanax.shallows.EmptyKeyspaceTracerFactory;
import com.netflix.astyanax.test.TestConnectionFactory;
import com.netflix.astyanax.thrift.ThriftAllRowsQueryImpl;
import com.netflix.astyanax.thrift.ThriftColumnFamilyQueryImpl;
import com.netflix.astyanax.thrift.ThriftKeyspaceImpl;

/**
 * The class <code>MessageRecordsTest</code> contains tests for the class <code>{@link MessageRecords}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class MessageRecordsTest {
    /**
     * Run the MessageRecords(int,Execution<Rows<byte[],String>>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecords_1()
        throws Exception {
        int count = 1;
        Execution<Rows<byte[], String>> query = new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce()));

        MessageRecords result = new MessageRecords(count, query);

        // add additional test code here
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the boolean hasNext() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testHasNext_1()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        boolean result = fixture.hasNext();

        // add additional test code here
        assertEquals(false, result);
    }

    /**
     * Run the boolean hasNext() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testHasNext_2()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        boolean result = fixture.hasNext();

        // add additional test code here
        assertEquals(false, result);
    }

    /**
     * Run the boolean hasNext() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testHasNext_3()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        boolean result = fixture.hasNext();

        // add additional test code here
        assertEquals(false, result);
    }

    /**
     * Run the boolean hasNext() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testHasNext_4()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        boolean result = fixture.hasNext();

        // add additional test code here
        assertEquals(false, result);
    }

    /**
     * Run the Iterator<MessageRecord> iterator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testIterator_1()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        Iterator<MessageRecord> result = fixture.iterator();

        // add additional test code here
        assertNotNull(result);
        assertEquals(false, result.hasNext());
    }


    /**
     * Run the void remove() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testRemove_1()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        fixture.remove();

        // add additional test code here
    }

    /**
     * Run the int size() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSize_1()
        throws Exception {
        MessageRecords fixture = new MessageRecords(1, new ThriftAllRowsQueryImpl(new ThriftColumnFamilyQueryImpl(new ScheduledThreadPoolExecutor(1), EmptyKeyspaceTracerFactory.getInstance(), new ThriftKeyspaceImpl("", new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new AstyanaxConfigurationImpl(), EmptyKeyspaceTracerFactory.getInstance()), new BagOfConnectionsConnectionPoolImpl(new ConnectionPoolConfigurationImpl(""), new TestConnectionFactory(new ConnectionPoolConfigurationImpl(""), new CountingConnectionPoolMonitor()), new CountingConnectionPoolMonitor()), new ColumnFamily("", new AsciiSerializer(), new AsciiSerializer()), ConsistencyLevel.CL_ALL, new RunOnce())));

        int result = fixture.size();

        // add additional test code here
        assertEquals(1, result);
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception
     *         if the initialization fails for some reason
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Before
    public void setUp()
        throws Exception {
        // add additional set up code here
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception
     *         if the clean-up fails for some reason
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @After
    public void tearDown()
        throws Exception {
        // Add additional tear down code here
    }

    /**
     * Launch the test.
     *
     * @param args the command line arguments
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    public static void main(String[] args) {
        new org.junit.runner.JUnitCore().run(MessageRecordsTest.class);
    }
}