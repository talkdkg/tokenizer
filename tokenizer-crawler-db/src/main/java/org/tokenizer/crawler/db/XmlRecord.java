package org.tokenizer.crawler.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.MD5;

public class XmlRecord {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRecord.class);
    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private String host = DefaultValues.EMPTY_STRING;
    private byte[] hostInverted = DefaultValues.EMPTY_ARRAY;
    private byte[] content = DefaultValues.EMPTY_ARRAY;
    private int parseAttemptCounter = 0;

    public XmlRecord(final byte[] content) {
        this.content = content;
        this.digest = MD5.digest(content);
    }

    public XmlRecord(final byte[] digest, final String host,
            final byte[] hostInverted, final byte[] content,
            final int parseAttemptCounter) {
        this.digest = digest;
        this.host = host;
        this.hostInverted = hostInverted;
        this.content = content;
        this.parseAttemptCounter = parseAttemptCounter;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public void setHostInverted(final byte[] hostInverted) {
        this.hostInverted = hostInverted;
    }

    public int getParseAttemptCounter() {
        return parseAttemptCounter;
    }

    public void setParseAttemptCounter(final int parseAttemptCounter) {
        this.parseAttemptCounter = parseAttemptCounter;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getContent() {
        return content;
    }
}
