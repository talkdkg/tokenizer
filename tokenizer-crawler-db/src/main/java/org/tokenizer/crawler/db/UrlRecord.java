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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;

public class UrlRecord {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(UrlRecord.class);
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final String EMPTY_STRING = "";
    private static final Date EMPTY_DATE = new Date(0);
    private byte[] digest = EMPTY_ARRAY;
    private String url = EMPTY_STRING;
    private String host = EMPTY_STRING;
    private byte[] hostInverted = EMPTY_ARRAY;
    private Date timestamp = EMPTY_DATE;
    private int fetchAttemptCounter = 0;
    private int httpResponseCode = 0;
    private byte[] webpageDigest = EMPTY_ARRAY;

    public UrlRecord(final String url) {
        this.url = url;
        this.host = HttpUtils.getHost(url);
        this.hostInverted = HttpUtils.getHostInverted(host);
        try {
            this.digest = MD5.digest(url.getBytes(HttpUtils.ASCII_CHARSET));
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        }
    }

    public UrlRecord(final byte[] digest, final String url, final String host,
            final byte[] hostInverted, final Date timestamp,
            final int fetchAttemptCounter, final int httpResponseCode,
            final byte[] webpageDigest) {
        this.digest = digest;
        this.url = url;
        this.host = host;
        this.hostInverted = hostInverted;
        this.timestamp = timestamp;
        this.fetchAttemptCounter = fetchAttemptCounter;
        this.httpResponseCode = httpResponseCode;
        this.webpageDigest = webpageDigest;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(final int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public byte[] getDigest() {
        return digest;
    }

    public void setDigest(final byte[] digest) {
        this.digest = digest;
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

    public int getFetchAttemptCounter() {
        return fetchAttemptCounter;
    }

    public void setFetchAttemptCounter(final int fetchAttemptCounter) {
        this.fetchAttemptCounter = fetchAttemptCounter;
    }

    public byte[] getWebpageDigest() {
        return webpageDigest;
    }

    public void setWebpageDigest(final byte[] webpageDigest) {
        this.webpageDigest = webpageDigest;
    }

    @Override
    public String toString() {
        return "UrlRecord [digest=" + Arrays.toString(digest) + ", url=" + url
                + ", host=" + host + ", hostInverted="
                + Arrays.toString(hostInverted) + ", timestamp=" + timestamp
                + ", fetchAttemptCounter=" + fetchAttemptCounter
                + ", httpResponseCode=" + httpResponseCode + ", webpageDigest="
                + Arrays.toString(webpageDigest) + "]";
    }
}
