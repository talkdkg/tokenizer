/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.crawler.db.model;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.DefaultValues;

public class XmlRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(XmlRecord.class);

    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private Date timestamp = DefaultValues.EMPTY_DATE;
    private String host = DefaultValues.EMPTY_STRING;
    private byte[] content = DefaultValues.EMPTY_ARRAY;
    private int parseAttemptCounter = 0;

    public XmlRecord(final String host, final byte[] content) {
        this.digest = MD5.digest(ArrayUtils.addAll(host.getBytes(), content));
        this.host = host;
        this.timestamp = new Date();
        this.content = content;
    }

    public XmlRecord(final byte[] digest, final Date timestamp, final String host, final byte[] content,
            final int parseAttemptCounter) {
        this.digest = digest;
        this.timestamp = timestamp;
        this.host = host;
        this.parseAttemptCounter = parseAttemptCounter;
        this.content = content;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(byte[] digest) {
        this.digest = digest;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getParseAttemptCounter() {
        return parseAttemptCounter;
    }

    public void setParseAttemptCounter(int parseAttemptCounter) {
        this.parseAttemptCounter = parseAttemptCounter;
    }

    public void incrementParseAttemptCounter() {
        this.parseAttemptCounter++;
    }

    public String getHostParseAttemptCounter() {
        return getHost() + String.valueOf(getParseAttemptCounter());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(digest);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        XmlRecord other = (XmlRecord) obj;
        if (!Arrays.equals(digest, other.digest))
            return false;
        return true;
    }

    @Override
    public String toString() {
        try {
            return "XmlRecord [digest=" + Arrays.toString(digest) + ", timestamp=" + timestamp + ", host=" + host
                    + ", content=" + new String(content, "UTF-8") + ", parseAttemptCounter=" + parseAttemptCounter + "]";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
