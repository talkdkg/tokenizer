/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.crawler.db;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.lilyproject.util.hbase.HBaseTableFactory;

public class CrawlerHBaseRepository {
  
  HBaseTableFactory tableFactory;
  protected HTableInterface urlTable;
  protected HTableInterface webpageTable;
  
  public static final byte[] ZERO_LONG = Bytes.toBytes(0L);
  public static final byte[] ZERO_INT = Bytes.toBytes(0);
  
  public CrawlerHBaseRepository(HBaseTableFactory tableFactory) {
    this.tableFactory = tableFactory;
    
    try {
      urlTable = CrawlerHBaseSchema.getUrlTable(tableFactory);
      webpageTable = CrawlerHBaseSchema.getWebpageTable(tableFactory);
    } catch (IOException e) {
      throw new RuntimeException("Can't get access to tables", e);
    } catch (InterruptedException e) {
      throw new RuntimeException("Can't get access to tables", e);
    }
    
  }
  
  public void create(UrlRecord urlRecord) throws IOException {
    
    byte[] row = UrlRecordDecoder.encode(urlRecord.getUrl());
    
    Get get = new Get(row);
    
    if (urlTable.exists(get)) return;
    
    Put put = new Put(row);
    
    put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
        CrawlerHBaseSchema.UrlColumn.TIMESTAMP.bytes, ZERO_LONG);
    put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
        CrawlerHBaseSchema.UrlColumn.HTTP_RESPONSE_CODE.bytes, ZERO_INT);
    
    urlTable.put(put);
    
  }
  
  public void update(UrlRecord urlRecord) throws IOException {
    byte[] row = UrlRecordDecoder.encode(urlRecord.getUrl());
    
    Put put = new Put(row);
    
    put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
        CrawlerHBaseSchema.UrlColumn.TIMESTAMP.bytes,
        Bytes.toBytes(urlRecord.getTimestamp()));
    put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
        CrawlerHBaseSchema.UrlColumn.HTTP_RESPONSE_CODE.bytes,
        Bytes.toBytes(urlRecord.getHttpResponseCode()));
    put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
        CrawlerHBaseSchema.UrlColumn.DIGEST.bytes, urlRecord.getDigest());
    
    urlTable.put(put);
    
  }
  
  public void create(WebpageRecord webpageRecord) throws IOException {
    
    byte[] row = webpageRecord.getDigest();
    
    Get get = new Get(row);
    
    // TODO: implement simple cache & measure performance improvements
    if (webpageTable.exists(get)) return;
    
    Put put = new Put(row);
    put.add(CrawlerHBaseSchema.WebpageCf.DATA.bytes,
        CrawlerHBaseSchema.WebpageColumn.URL.bytes,
        Bytes.toBytes(webpageRecord.getUrl()));
    put.add(CrawlerHBaseSchema.WebpageCf.DATA.bytes,
        CrawlerHBaseSchema.WebpageColumn.TIMESTAMP.bytes,
        Bytes.toBytes(webpageRecord.getTimestamp()));
    put.add(CrawlerHBaseSchema.WebpageCf.DATA.bytes,
        CrawlerHBaseSchema.WebpageColumn.CONTENT.bytes,
        webpageRecord.getContent());
    put.add(CrawlerHBaseSchema.WebpageCf.DATA.bytes,
        CrawlerHBaseSchema.WebpageColumn.CHARSET.bytes,
        Bytes.toBytes(webpageRecord.getCharset()));
    
    webpageTable.put(put);
    
  }
  
}
