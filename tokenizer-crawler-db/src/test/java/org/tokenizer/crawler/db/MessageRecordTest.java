package org.tokenizer.crawler.db;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The class <code>MessageRecordTest</code> contains tests for the class <code>{@link MessageRecord}</code>.
 *
 * @generatedBy CodePro at 4/1/13 2:52 PM
 * @author Fuad
 * @version $Revision: 1.0 $
 */
public class MessageRecordTest {
    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_1()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = "";
        String title = "";
        String content = "";

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_2()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = null;
        String title = "";
        String content = "";

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_3()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = "";
        String title = null;
        String content = "";

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_4()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = null;
        String title = null;
        String content = "";

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_5()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = "";
        String title = "";
        String content = null;

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_6()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = null;
        String title = "";
        String content = null;

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_7()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = "";
        String title = null;
        String content = null;

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_8()
        throws Exception {
        byte[] digest = new byte[] {};
        String author = null;
        String title = null;
        String content = null;

        MessageRecord result = new MessageRecord(digest, author, title, content);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_9()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_10()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = null;
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_11()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_12()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = null;
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_13()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = null;
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_14()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = null;
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_15()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = null;
        byte[] hostInverted = new byte[] {};
        String topic = null;
        String date = null;
        String author = null;
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_16()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = null;
        String sex = "";
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_17()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = null;
        String title = "";
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_18()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = null;
        String content = "";
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_19()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = null;
        String userRating = "";

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_20()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = "";
        String sex = "";
        String title = "";
        String content = "";
        String userRating = null;

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_21()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = "";
        String date = "";
        String author = "";
        String age = null;
        String sex = null;
        String title = null;
        String content = null;
        String userRating = null;

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_22()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = null;
        byte[] hostInverted = new byte[] {};
        String topic = null;
        String date = null;
        String author = null;
        String age = null;
        String sex = null;
        String title = null;
        String content = null;
        String userRating = null;

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_23()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = "";
        byte[] hostInverted = new byte[] {};
        String topic = null;
        String date = null;
        String author = null;
        String age = null;
        String sex = null;
        String title = null;
        String content = null;
        String userRating = null;

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the MessageRecord(byte[],String,byte[],String,String,String,String,String,String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMessageRecord_24()
        throws Exception {
        byte[] digest = new byte[] {};
        String host = null;
        byte[] hostInverted = new byte[] {};
        String topic = null;
        String date = null;
        String author = null;
        String age = null;
        String sex = null;
        String title = null;
        String content = null;
        String userRating = null;

        MessageRecord result = new MessageRecord(digest, host, hostInverted, topic, date, author, age, sex, title, content, userRating);

        // add additional test code here
        assertNotNull(result);
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result.toString());
        assertEquals("", result.getHost());
        assertEquals("", result.getContent());
        assertEquals("", result.getDate());
        assertEquals("", result.getTopic());
        assertEquals("", result.getAge());
        assertEquals("", result.getTitle());
        assertEquals("", result.getAuthor());
        assertEquals("", result.getSex());
        assertEquals("", result.getUserRating());
        assertEquals("1969-12-31T19:00:00.000-05:00", result.getISO8601Date());
    }

    /**
     * Run the String getAge() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetAge_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getAge();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getAuthor() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetAuthor_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getAuthor();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getContent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetContent_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getContent();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getDate() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetDate_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getDate();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the byte[] getDigest() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetDigest_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        byte[] result = fixture.getDigest();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the String getHost() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHost_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getHost();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the byte[] getHostInverted() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetHostInverted_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        byte[] result = fixture.getHostInverted();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    /**
     * Run the String getISO8601Date() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetISO8601Date_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getISO8601Date();

        // add additional test code here
        assertEquals("1969-12-31T19:00:00.000-05:00", result);
    }

    /**
     * Run the String getISO8601Date() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetISO8601Date_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getISO8601Date();

        // add additional test code here
        assertEquals("1969-12-31T19:00:00.000-05:00", result);
    }

    /**
     * Run the String getSex() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetSex_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getSex();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getTitle() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTitle_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getTitle();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getTopic() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetTopic_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getTopic();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the String getUserRating() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testGetUserRating_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.getUserRating();

        // add additional test code here
        assertEquals("", result);
    }

    /**
     * Run the void main(String[]) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testMain_1()
        throws Exception {
        String[] args = new String[] {};

        MessageRecord.main(args);

        // add additional test code here
    }

    /**
     * Run the void setAge(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetAge_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String age = "";

        fixture.setAge(age);

        // add additional test code here
    }

    /**
     * Run the void setAge(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetAge_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String age = null;

        fixture.setAge(age);

        // add additional test code here
    }

    /**
     * Run the void setDate(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetDate_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String date = "";

        fixture.setDate(date);

        // add additional test code here
    }

    /**
     * Run the void setDate(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetDate_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String date = null;

        fixture.setDate(date);

        // add additional test code here
    }

    /**
     * Run the void setHost(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetHost_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String host = "";

        fixture.setHost(host);

        // add additional test code here
    }

    /**
     * Run the void setHost(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetHost_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String host = null;

        fixture.setHost(host);

        // add additional test code here
    }

    /**
     * Run the void setHostInverted(byte[]) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetHostInverted_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        byte[] hostInverted = new byte[] {};

        fixture.setHostInverted(hostInverted);

        // add additional test code here
    }

    /**
     * Run the void setHostInverted(byte[]) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetHostInverted_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        byte[] hostInverted = new byte[] {};

        fixture.setHostInverted(hostInverted);

        // add additional test code here
    }

    /**
     * Run the void setSex(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetSex_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String sex = "";

        fixture.setSex(sex);

        // add additional test code here
    }

    /**
     * Run the void setSex(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetSex_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String sex = null;

        fixture.setSex(sex);

        // add additional test code here
    }

    /**
     * Run the void setTopic(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetTopic_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String topic = "";

        fixture.setTopic(topic);

        // add additional test code here
    }

    /**
     * Run the void setTopic(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetTopic_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String topic = null;

        fixture.setTopic(topic);

        // add additional test code here
    }

    /**
     * Run the void setUserRating(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetUserRating_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String userRating = "";

        fixture.setUserRating(userRating);

        // add additional test code here
    }

    /**
     * Run the void setUserRating(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testSetUserRating_2()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");
        String userRating = null;

        fixture.setUserRating(userRating);

        // add additional test code here
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 4/1/13 2:52 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        MessageRecord fixture = new MessageRecord(new byte[] {}, "", "", "");

        String result = fixture.toString();

        // add additional test code here
        assertEquals("MessageRecord [digest=[], host=, hostInverted=[], topic=, date=, author=, age=, sex=, title=, content=, userRating=]", result);
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
        new org.junit.runner.JUnitCore().run(MessageRecordTest.class);
    }
}