package org.tokenizer.crawler.db;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>RepositoryExceptionTest</code> contains tests for the class <code>{@link RepositoryException}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class RepositoryExceptionTest {
    /**
     * Run the RepositoryException() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testRepositoryException_1()
        throws Exception {

        RepositoryException result = new RepositoryException();

        // add additional test code here
        assertNotNull(result);
        assertEquals(null, result.getCause());
        assertEquals("org.tokenizer.crawler.db.RepositoryException", result.toString());
        assertEquals(null, result.getMessage());
        assertEquals(null, result.getLocalizedMessage());
    }

    /**
     * Run the RepositoryException(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testRepositoryException_2()
        throws Exception {
        String message = "";

        RepositoryException result = new RepositoryException(message);

        // add additional test code here
        assertNotNull(result);
        assertEquals(null, result.getCause());
        assertEquals("org.tokenizer.crawler.db.RepositoryException: ", result.toString());
        assertEquals("", result.getMessage());
        assertEquals("", result.getLocalizedMessage());
    }

    /**
     * Run the RepositoryException(Throwable) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testRepositoryException_3()
        throws Exception {
        Throwable cause = new Throwable();

        RepositoryException result = new RepositoryException(cause);

        // add additional test code here
        assertNotNull(result);
        assertEquals("org.tokenizer.crawler.db.RepositoryException: java.lang.Throwable", result.toString());
        assertEquals("java.lang.Throwable", result.getMessage());
        assertEquals("java.lang.Throwable", result.getLocalizedMessage());
    }

    /**
     * Run the RepositoryException(String,Throwable) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testRepositoryException_4()
        throws Exception {
        String message = "";
        Throwable cause = new Throwable();

        RepositoryException result = new RepositoryException(message, cause);

        // add additional test code here
        assertNotNull(result);
        assertEquals("org.tokenizer.crawler.db.RepositoryException: ", result.toString());
        assertEquals("", result.getMessage());
        assertEquals("", result.getLocalizedMessage());
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
        new org.junit.runner.JUnitCore().run(RepositoryExceptionTest.class);
    }
}