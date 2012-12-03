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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.util.Bytes;
import org.lilyproject.util.hbase.HBaseTableFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.CrawlerHBaseSchema.Table;

public class CrawlerHBaseRepository {
    private static final Logger LOG = LoggerFactory
            .getLogger(CrawlerHBaseRepository.class);
    HBaseTableFactory tableFactory;
    private final HTableInterface urlTable;
    private final HTableInterface webpageTable;
    private final HTableInterface xmlTable;
    private final HTableInterface messageTable;
    public static byte[] POSTFIX = new byte[] { 0 };
    public static final byte[] ZERO_LONG = Bytes.toBytes(0L);
    public static final byte[] ZERO_INT = Bytes.toBytes(0);

    public HTableInterface getUrlTable() {
        return urlTable;
    }

    public HTableInterface getWebpageTable() {
        return webpageTable;
    }

    public HTableInterface getXmlTable() {
        return xmlTable;
    }

    public CrawlerHBaseRepository(HBaseTableFactory tableFactory) {
        this.tableFactory = tableFactory;
        try {
            urlTable = CrawlerHBaseSchema.getUrlTable(tableFactory);
            webpageTable = CrawlerHBaseSchema.getWebpageTable(tableFactory);
            xmlTable = CrawlerHBaseSchema.getXmlTable(tableFactory);
            messageTable = CrawlerHBaseSchema.getMessageTable(tableFactory);
        } catch (IOException e) {
            throw new RuntimeException("Can't get access to tables", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Can't get access to tables", e);
        }
    }

    public void create(UrlRecord urlRecord) throws IOException {
        byte[] row = UrlRecordDecoder.encode(urlRecord.getUrl());
        Get get = new Get(row);
        if (urlTable.exists(get))
            return;
        Put put = new Put(row);
        put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
                CrawlerHBaseSchema.UrlColumn.TIMESTAMP.bytes, ZERO_LONG);
        put.add(CrawlerHBaseSchema.UrlCf.DATA.bytes,
                CrawlerHBaseSchema.UrlColumn.HTTP_RESPONSE_CODE.bytes, ZERO_INT);
        urlTable.put(put);
    }

    public UrlRecord getUrlRecord(String url) {
        byte[] row = UrlRecordDecoder.encode(url);
        Get get = new Get(row);
        try {
            if (!urlTable.exists(get))
                return null;
            Result result = urlTable.get(get);
            return decodeUrlRecord(result);
        } catch (IOException e) {
            LOG.error("", e);
            return null;
        }
    }

    public boolean containsUrlRecord(String url) {
        byte[] row = UrlRecordDecoder.encode(url);
        Get get = new Get(row);
        try {
            return urlTable.exists(get);
        } catch (IOException e) {
            LOG.error("", e);
            return false;
        }
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
                CrawlerHBaseSchema.UrlColumn.DIGEST.bytes,
                urlRecord.getDigest());
        urlTable.put(put);
    }

    public void create(WebpageRecord webpageRecord) throws IOException {
        byte[] row = webpageRecord.getDigest();
        Get get = new Get(row);
        // TODO: implement simple cache & measure performance improvements
        if (webpageTable.exists(get))
            return;
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

    public static long countUrl() {
        LOG.info("Counting URLs...");
        Configuration customConf = new Configuration();
        customConf.setStrings("hbase.zookeeper.quorum", "localhost");
        // Increase RPC timeout, in case of a slow computation
        customConf.setLong("hbase.rpc.timeout", 600000);
        // Default is 1, set to a higher value for faster scanner.next(..)
        customConf.setLong("hbase.client.scanner.caching", 1000);
        Configuration configuration = HBaseConfiguration.create(customConf);
        AggregationClient aggregationClient = new AggregationClient(
                configuration);
        Scan scan = new Scan();
        scan.addFamily(CrawlerHBaseSchema.UrlCf.DATA.bytes);
        long rowCount = 0L;
        try {
            rowCount = aggregationClient.rowCount(Table.URL.bytes, null, scan);
        } catch (Throwable e) {
            LOG.error("", e);
        }
        return rowCount;
    }

    public String nextUrl(String currentUrl) {
        Scan scan = new Scan();
        if (currentUrl != null) {
            byte[] currentUrlEncoded = UrlRecordDecoder.encode(currentUrl);
            byte[] startRow = Bytes.add(currentUrlEncoded, POSTFIX);
            scan.setStartRow(startRow);
        }
        String url = null;
        ResultScanner resultScanner = null;
        try {
            resultScanner = this.urlTable.getScanner(scan);
            Result r = resultScanner.next();
            if (r != null) {
                url = UrlRecordDecoder.decode(r.getRow());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (resultScanner != null) {
                resultScanner.close();
            }
        }
        return url;
    }

    public List<String> nextUrl(String currentUrl, int count) {
        List<String> urls = new ArrayList<String>();
        Scan scan = new Scan();
        if (currentUrl != null) {
            byte[] currentUrlEncoded = UrlRecordDecoder.encode(currentUrl);
            byte[] startRow = Bytes.add(currentUrlEncoded, POSTFIX);
            scan.setStartRow(startRow);
        } else {
            scan.setStartRow(POSTFIX);
        }
        ResultScanner rs = null;
        try {
            rs = this.urlTable.getScanner(scan);
            int i = 0;
            for (Result r = rs.next(); r != null; r = rs.next()) {
                String url = UrlRecordDecoder.decode(r.getRow());
                urls.add(url);
                i++;
                if (i == count) {
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return urls;
    }

    public WebpageRecord getWebpageRecord(byte[] digest) {
        Get get = new Get(digest);
        try {
            if (!webpageTable.exists(get))
                return null;
            Result result = webpageTable.get(get);
            return decodeWebpageRecord(result);
        } catch (IOException e) {
            LOG.error("", e);
            return null;
        }
    }

    public static UrlRecord decodeUrlRecord(Result result) {
        UrlRecord urlRecord = new UrlRecord();
        String url = UrlRecordDecoder.decode(result);
        urlRecord.setUrl(url);
        byte[] digest = result.getValue(CrawlerHBaseSchema.UrlCf.DATA.bytes,
                CrawlerHBaseSchema.UrlColumn.DIGEST.bytes);
        if (digest != null) {
            urlRecord.setDigest(digest);
        }
        byte[] timestamp = result.getValue(CrawlerHBaseSchema.UrlCf.DATA.bytes,
                CrawlerHBaseSchema.UrlColumn.TIMESTAMP.bytes);
        if (timestamp != null) {
            urlRecord.setTimestamp(Bytes.toLong(timestamp));
        }
        byte[] httpResponseCode = result.getValue(
                CrawlerHBaseSchema.UrlCf.DATA.bytes,
                CrawlerHBaseSchema.UrlColumn.HTTP_RESPONSE_CODE.bytes);
        if (httpResponseCode != null) {
            urlRecord.setHttpResponseCode(Bytes.toInt(httpResponseCode));
        }
        return urlRecord;
    }

    public static WebpageRecord decodeWebpageRecord(Result result) {
        WebpageRecord webpage = new WebpageRecord(result.getRow());
        byte[] url = result.getValue(CrawlerHBaseSchema.WebpageCf.DATA.bytes,
                CrawlerHBaseSchema.WebpageColumn.URL.bytes);
        byte[] timestamp = result.getValue(
                CrawlerHBaseSchema.WebpageCf.DATA.bytes,
                CrawlerHBaseSchema.WebpageColumn.TIMESTAMP.bytes);
        byte[] charset = result.getValue(
                CrawlerHBaseSchema.WebpageCf.DATA.bytes,
                CrawlerHBaseSchema.WebpageColumn.CHARSET.bytes);
        byte[] content = result.getValue(
                CrawlerHBaseSchema.WebpageCf.DATA.bytes,
                CrawlerHBaseSchema.WebpageColumn.CONTENT.bytes);
        webpage.setUrl(Bytes.toString(url));
        // webpage.setTimestamp(Bytes.toLong(timestamp));
        webpage.setCharset(Bytes.toString(charset));
        webpage.setContent(content);
        return webpage;
    }

    public void createXmlObjects(String[] xmlObjects, String host) {
        for (String xml : xmlObjects) {
            if (xml == null || xml.length() == 0) {
                continue;
            }
            byte[] row;
            try {
                row = UrlRecordDecoder.encode("http://" + host + "/"
                        + MD5.MD5(xml));
                LOG.warn("encoded: " + new String(row));
                LOG.warn("xml: {}", xml);
                Get get = new Get(row);
                // TODO: implement simple cache & measure performance
                // improvements
                if (xmlTable.exists(get))
                    return;
                Put put = new Put(row);
                put.add(CrawlerHBaseSchema.XmlCf.DATA.bytes,
                        CrawlerHBaseSchema.XmlColumn.XML.bytes,
                        Bytes.toBytes(xml));
                xmlTable.put(put);
            } catch (UnsupportedEncodingException e) {
                LOG.error(StringUtils.EMPTY, e);
            } catch (IOException e) {
                LOG.error(StringUtils.EMPTY, e);
            }
        }
    }

    public void createMessage(MessageRecord messageRecord) {
        byte[] row;
        try {
            row = UrlRecordDecoder.encode("http://" + messageRecord.getHost()
                    + "/" + messageRecord.getDigest());
            Get get = new Get(row);
            // TODO: implement simple cache & measure performance
            // improvements
            if (messageTable.exists(get))
                return;
            Put put = new Put(row);
            boolean empty = true;
            if (messageRecord.getAge() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.AGE.bytes,
                        Bytes.toBytes(messageRecord.getAge()));
                empty = false;
            }
            if (messageRecord.getAuthor() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.AUTHOR.bytes,
                        Bytes.toBytes(messageRecord.getAuthor()));
                empty = false;
            }
            if (messageRecord.getContent() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.CONTENT.bytes,
                        Bytes.toBytes(messageRecord.getContent()));
                empty = false;
            }
            if (messageRecord.getDate() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.DATE.bytes,
                        Bytes.toBytes(messageRecord.getDate()));
                empty = false;
            }
            if (messageRecord.getSex() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.SEX.bytes,
                        Bytes.toBytes(messageRecord.getSex()));
                empty = false;
            }
            if (messageRecord.getTitle() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.TITLE.bytes,
                        Bytes.toBytes(messageRecord.getTitle()));
                empty = false;
            }
            if (messageRecord.getTopic() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.TOPIC.bytes,
                        Bytes.toBytes(messageRecord.getTopic()));
                empty = false;
            }
            if (messageRecord.getUserRating() != null) {
                put.add(CrawlerHBaseSchema.MessageCf.DATA.bytes,
                        CrawlerHBaseSchema.MessageColumn.USER_RATING.bytes,
                        Bytes.toBytes(messageRecord.getUserRating()));
                empty = false;
            }
            if (!empty) {
                messageTable.put(put);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error(StringUtils.EMPTY, e);
        } catch (IOException e) {
            LOG.error(StringUtils.EMPTY, e);
        }
    }
}
