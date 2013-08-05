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
package org.tokenizer.crawler.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;

public class WebpageRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final ArrayList<byte[]> EMPTY_ARRAYLIST = new ArrayList<byte[]>();
    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private String host = DefaultValues.EMPTY_STRING;
    private String url = DefaultValues.EMPTY_STRING;
    private byte[] hostInverted = DefaultValues.EMPTY_ARRAY;
    private Date timestamp = DefaultValues.EMPTY_DATE;
    private String charset = DefaultValues.EMPTY_STRING;
    private byte[] content = DefaultValues.EMPTY_ARRAY;
    private int splitAttemptCounter = 0;
    private ArrayList<byte[]> xmlLinks = EMPTY_ARRAYLIST;

    public ArrayList<byte[]> getXmlLinks() {
        return xmlLinks;
    }

    public void setXmlLinks(final ArrayList<byte[]> xmlLinks) {
        if (xmlLinks != null) {
            this.xmlLinks = xmlLinks;
        }
    }

    public void addXmlLink(final byte[] xmlLink) {
        xmlLinks.add(xmlLink);
    }

    public WebpageRecord(final String url, final Date timestamp, final String charset, final byte[] content,
            final ArrayList<byte[]> xmlLinks) {
        this.digest = MD5.digest(content);
        this.url = url;
        this.host = HttpUtils.getHost(url);
        this.hostInverted = HttpUtils.getHostInverted(host);
        this.timestamp = timestamp;
        this.charset = charset;
        this.content = content;
        if (xmlLinks != null) {
            this.xmlLinks = xmlLinks;
        }
    }

    public WebpageRecord(final String url, final long timestamp, final String charset, final byte[] content,
            final ArrayList<byte[]> xmlLinks) {
        this(url, new Date(timestamp), charset, content, xmlLinks);
    }

    public WebpageRecord(final byte[] digest, final String url, final byte[] hostInverted_splitAttemptCounter,
            final Date timestamp, final String charset, final byte[] content, final ArrayList<byte[]> xmlLinks) {
        this.digest = digest;
        this.url = url;
        byte[] splitAttemptCounterBytes = Arrays.copyOfRange(hostInverted_splitAttemptCounter,
                hostInverted_splitAttemptCounter.length - 4, hostInverted_splitAttemptCounter.length);
        this.splitAttemptCounter = HttpUtils.bytesToInt(splitAttemptCounterBytes);
        this.host = HttpUtils.getHost(url);
        this.hostInverted = HttpUtils.getHostInverted(host);
        this.timestamp = timestamp;
        this.charset = charset;
        this.content = content;
        if (xmlLinks != null) {
            this.xmlLinks = xmlLinks;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getHost() {
        return host;
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCharset() {
        return charset;
    }

    public int getSplitAttemptCounter() {
        return splitAttemptCounter;
    }

    public void incrementSplitAttemptCounter() {
        this.splitAttemptCounter++;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "WebpageRecord [digest=" + Arrays.toString(digest) + ", url=" + url + ", host=" + host
                + ", hostInverted=" + Arrays.toString(hostInverted) + ", timestamp=" + timestamp + ", charset="
                + charset + ", splitAttemptCounter=" + splitAttemptCounter + "]";
    }
}
