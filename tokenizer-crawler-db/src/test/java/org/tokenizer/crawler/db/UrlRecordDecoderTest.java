package org.tokenizer.crawler.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class UrlRecordDecoderTest {
  
  @Before
  public void setUp() throws Exception {
    
  }
  
  @Test
  public void testDecodeRecordId() {
    
    assertEquals("www.a.b.c", UrlRecordDecoder.getHost("http://www.a.b.c"));
    assertEquals("www.a.b.c", UrlRecordDecoder.getHost("http://www.a.b.c/"));
    assertEquals("www.a.b.c", UrlRecordDecoder.getHost("http://www.a.b.c"));
    assertNotSame("www.a.b.c", UrlRecordDecoder.getHost("https://www.a.b.c"));
    assertEquals("", UrlRecordDecoder.getHost("https://www.a.b.c"));
    
    assertNull(UrlRecordDecoder.decode(UrlRecordDecoder.encode("http")));
    assertNull(UrlRecordDecoder.decode(UrlRecordDecoder.encode("https")));
    assertNull(UrlRecordDecoder.decode(UrlRecordDecoder.encode("http:")));
    assertNull(UrlRecordDecoder.decode(UrlRecordDecoder.encode("http:/")));
    
    String[] urls = {"http://a.b.c.kaka.z/d/e/f.h", "http://www/z", "http:///",
        "http://www", "http://www.a", "http://www.a/", "http://www.a//",
        "http:///www.a"
    
    };
    
    for (String url : urls) {
      assertEquals(url, UrlRecordDecoder.decode(UrlRecordDecoder.encode(url)));
    }
    
  }
  
}
