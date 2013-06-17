package org.tokenizer.crawler.db.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.tokenizer.crawler.db.model.WeblogRecord;

public class TimestampUrlIDXTest {

    @Test
    public void testWeblogRecord_1() throws Exception {
        TimestampUrlIDX result = new TimestampUrlIDX("http://www.amazon.com/%C3%81gnes-Szak%C3%A1ly/dp/B001QR2DR4");
        assertNotNull(result);
        System.out.println(result);

    }

}
