package org.tokenizer.crawler.db;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.connectionpool.TokenRange;
import com.netflix.astyanax.partitioner.Partitioner;
import com.netflix.astyanax.serializers.AsciiSerializer;

/**
 * The class <code>MyBOPPartitionerTest</code> contains tests for the class <code>{@link MyBOPPartitioner}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class MyBOPPartitionerTest {
    /**
     * Run the MyBOPPartitioner() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMyBOPPartitioner_1()
        throws Exception {

        MyBOPPartitioner result = new MyBOPPartitioner();

        // add additional test code here
        assertNotNull(result);
        assertEquals("0", result.getMinToken());
        assertEquals("170141183460469231731687303715884105727", result.getMaxToken());
    }

    /**
     * Run the Partitioner get() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGet_1()
        throws Exception {

        Partitioner result = MyBOPPartitioner.get();

        // add additional test code here
        assertNotNull(result);
        assertEquals("0", result.getMinToken());
        assertEquals("170141183460469231731687303715884105727", result.getMaxToken());
    }

    /**
     * Run the String getMaxToken() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetMaxToken_1()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();

        String result = fixture.getMaxToken();

        // add additional test code here
        assertEquals("170141183460469231731687303715884105727", result);
    }

    /**
     * Run the String getMinToken() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetMinToken_1()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();

        String result = fixture.getMinToken();

        // add additional test code here
        assertEquals("0", result);
    }

    /**
     * Run the String getTokenForKey(ByteBuffer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTokenForKey_1()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();
        ByteBuffer key = ByteBuffer.allocate(0);

        String result = fixture.getTokenForKey(key);

        // add additional test code here
        assertEquals("Token(bytes[])", result);
    }

 


    /**
     * Run the List<String> splitRange(BigInteger,BigInteger,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSplitRange_1()
        throws Exception {
        BigInteger first = BigInteger.valueOf(1L);
        BigInteger last = BigInteger.valueOf(1L);
        int count = 1;

        List<String> result = MyBOPPartitioner.splitRange(first, last, count);

        // add additional test code here
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("1"));
    }

    /**
     * Run the List<String> splitRange(BigInteger,BigInteger,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSplitRange_2()
        throws Exception {
        BigInteger first = BigInteger.valueOf(1L);
        BigInteger last = BigInteger.valueOf(1L);
        int count = 1;

        List<String> result = MyBOPPartitioner.splitRange(first, last, count);

        // add additional test code here
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("1"));
    }

    /**
     * Run the List<TokenRange> splitTokenRange(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSplitTokenRange_1()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();
        int count = 1;

        List<TokenRange> result = fixture.splitTokenRange(count);

        // add additional test code here
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the List<TokenRange> splitTokenRange(String,String,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSplitTokenRange_2()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();
        String first = "";
        String last = "";
        int count = 1;

        List<TokenRange> result = fixture.splitTokenRange(first, last, count);

        // add additional test code here
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Run the List<TokenRange> splitTokenRange(String,String,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSplitTokenRange_3()
        throws Exception {
        MyBOPPartitioner fixture = new MyBOPPartitioner();
        String first = "";
        String last = "";
        int count = 1;

        List<TokenRange> result = fixture.splitTokenRange(first, last, count);

        // add additional test code here
        assertNotNull(result);
        assertEquals(1, result.size());
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
        new org.junit.runner.JUnitCore().run(MyBOPPartitionerTest.class);
    }
}