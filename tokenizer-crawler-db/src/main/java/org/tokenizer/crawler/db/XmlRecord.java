package org.tokenizer.crawler.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.MD5;

public class XmlRecord {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRecord.class);
    private String host;
    private String digest;
    private byte[] xml;

    public XmlRecord() {
    }

    public XmlRecord(String host, byte[] xml) {
        this.host = host;
        this.xml = xml;
        this.digest = MD5.MD5(xml);
    }

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

    @Override
    public String toString() {
        return "XmlRecord [host=" + host + ", digest=" + digest + ", xml="
                + new String(xml) + "]";
    }
}
