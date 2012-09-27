package org.tokenizer.crawler.db;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.io.hfile.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.lilyproject.util.hbase.HBaseTableFactory;

public class CrawlerHBaseSchema {
  
  private static final HTableDescriptor webpageTableDescriptor;
  
  static {
    webpageTableDescriptor = new HTableDescriptor(Table.WEBPAGE.bytes);
    webpageTableDescriptor.addFamily(new HColumnDescriptor(
        WebpageCf.DATA.bytes, 1, Compression.Algorithm.GZ.getName(), false,
        true, HConstants.FOREVER, HColumnDescriptor.DEFAULT_BLOOMFILTER));
  }
  
  private static final HTableDescriptor urlTableDescriptor;
  
  static {
    urlTableDescriptor = new HTableDescriptor(Table.URL.bytes);
    urlTableDescriptor.addFamily(new HColumnDescriptor(UrlCf.DATA.bytes, 1,
        Compression.Algorithm.NONE.getName(), false, true, HConstants.FOREVER,
        HColumnDescriptor.DEFAULT_BLOOMFILTER));
  }
  
  public static enum Table {
    WEBPAGE("webpage"), URL("url");
    
    public final byte[] bytes;
    public final String name;
    
    Table(String name) {
      this.name = name;
      this.bytes = Bytes.toBytes(name);
    }
  }
  
  /**
   * Column families in the immutable webpage table. First one immutable (and
   * MD5 is based upon it), and second (could be) mutable
   */
  public static enum WebpageCf {
    DATA("data");
    
    public final byte[] bytes;
    public final String name;
    
    WebpageCf(String name) {
      this.name = name;
      this.bytes = Bytes.toBytes(name);
    }
  }
  
  public static enum WebpageColumn {
    CONTENT("content"), CHARSET("charset");
    
    public final byte[] bytes;
    public final String name;
    
    WebpageColumn(String name) {
      this.name = name;
      this.bytes = Bytes.toBytes(name);
    }
  }
  
  public static enum UrlCf {
    DATA("data");
    
    public final byte[] bytes;
    public final String name;
    
    UrlCf(String name) {
      this.name = name;
      this.bytes = Bytes.toBytes(name);
    }
  }
  
  public static enum UrlColumn {
    URL("url"), TIMESTAMP("timestamp"), STATUS("status");
    
    public final byte[] bytes;
    public final String name;
    
    UrlColumn(String name) {
      this.name = name;
      this.bytes = Bytes.toBytes(name);
    }
  }
  
  public static HTableInterface getWebpageTable(HBaseTableFactory tableFactory)
      throws IOException, InterruptedException {
    return tableFactory.getTable(webpageTableDescriptor);
  }
  
  public static HTableInterface getUrlTable(HBaseTableFactory tableFactory,
      boolean clientMode) throws IOException, InterruptedException {
    return tableFactory.getTable(urlTableDescriptor, !clientMode);
  }
  
}
