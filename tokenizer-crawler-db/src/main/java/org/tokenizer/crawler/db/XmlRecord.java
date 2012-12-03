package org.tokenizer.crawler.db;

import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlRecord {
    private static final Logger LOG = LoggerFactory.getLogger(XmlRecord.class);
    // primary key: (inverted) http://host/MD5
    private String host;
    private String digest;
    private byte[] xml;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public byte[] getXml() {
        return xml;
    }

    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public XmlRecord(Result result) {
        String url = UrlRecordDecoder.decode(result);
        LOG.warn("url: " + url);
        // remove "http://" prefix
        url = url.substring(7);
        int splitPosition = url.indexOf('/');
        if (splitPosition > 0) {
            this.host = url.substring(0, splitPosition);
            this.digest = url.substring(splitPosition + 1);
        } else {
            LOG.warn("url doesn't end with MD5: {}", url);
        }
        this.xml = result.getValue(CrawlerHBaseSchema.XmlCf.DATA.bytes,
                CrawlerHBaseSchema.XmlColumn.XML.bytes);
    }

    @Override
    public String toString() {
        return "XmlRecord [host=" + host + ", digest=" + digest + ", xml="
                + new String(xml) + "]";
    }
}
