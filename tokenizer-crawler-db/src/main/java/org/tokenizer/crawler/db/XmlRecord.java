package org.tokenizer.crawler.db;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;

public class XmlRecord {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRecord.class);
    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private Date timestamp = DefaultValues.EMPTY_DATE;
    private String host = DefaultValues.EMPTY_STRING;
    private byte[] hostInverted = DefaultValues.EMPTY_ARRAY;
    private byte[] content = DefaultValues.EMPTY_ARRAY;
    private int parseAttemptCounter = 0;

    public XmlRecord(final String host, final byte[] content) {
        this.digest = MD5.digest(ArrayUtils.addAll(host.getBytes(), content));
        this.host = host;
        this.hostInverted = HttpUtils.getHostInverted(host);
        this.timestamp = new Date();
        this.content = content;
    }

    public XmlRecord(final byte[] digest, final Date timestamp,
            final byte[] hostInverted_parseAttemptCounter, final byte[] content) {
        this.digest = digest;
        this.timestamp = timestamp;
        this.hostInverted = Arrays.copyOfRange(
                hostInverted_parseAttemptCounter, 0,
                hostInverted_parseAttemptCounter.length - 4);
        this.host = HttpUtils.getHostUninverted(hostInverted);
        byte[] parseAttemptCounterBytes = Arrays.copyOfRange(
                hostInverted_parseAttemptCounter,
                hostInverted_parseAttemptCounter.length - 4,
                hostInverted_parseAttemptCounter.length);
        this.parseAttemptCounter = HttpUtils
                .bytesToInt(parseAttemptCounterBytes);
        this.content = content;
    }

    public String getHost() {
        return host;
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public int getParseAttemptCounter() {
        return parseAttemptCounter;
    }

    public void incrementParseAttemptCounter() {
        this.parseAttemptCounter++;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        try {
            return "XmlRecord [digest=" + Arrays.toString(digest) + ", host="
                    + host + ", hostInverted=" + Arrays.toString(hostInverted)
                    + ", timestamp=" + timestamp + ", content="
                    + (new String(content, "UTF-8")) + ", parseAttemptCounter="
                    + parseAttemptCounter + "]";
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
            return null;
        }
    }
}
