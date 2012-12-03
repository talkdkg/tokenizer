package org.tokenizer.crawler.db;

import java.util.Arrays;

import org.apache.hadoop.hbase.client.Result;

public class XmlRecord {
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
        // remove "http://" prefix
        url = url.substring(7);
        int splitPosition = url.indexOf('/');
        this.host = url.substring(0, splitPosition);
        this.digest = url.substring(splitPosition + 1);
        this.xml = result.getValue(CrawlerHBaseSchema.XmlCf.DATA.bytes,
                CrawlerHBaseSchema.XmlColumn.XML.bytes);
    }

    @Override
    public String toString() {
        return "XmlRecord [host=" + host + ", digest=" + digest + ", xml="
                + Arrays.toString(xml) + "]";
    }
}
