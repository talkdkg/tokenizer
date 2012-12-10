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

import java.util.Date;

import org.tokenizer.core.util.MD5;

public class WebpageRecord {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final String EMPTY_STRING = "";
    private static final Date EMPTY_DATE = new Date(0);
    private byte[] digest = EMPTY_ARRAY;
    private String url = EMPTY_STRING;
    private String host = EMPTY_STRING;
    private byte[] hostInverted = EMPTY_ARRAY;
    private Date timestamp = EMPTY_DATE;
    private String charset = EMPTY_STRING;
    private byte[] content = EMPTY_ARRAY;
    private int splitAttemptCounter = 0;

    public WebpageRecord(final byte[] content) {
        this.content = content;
        this.digest = MD5.digest(content);
    }

    public WebpageRecord(final byte[] digest, final String url,
            final String host, final byte[] hostInverted, final Date timestamp,
            final String charset, final byte[] content,
            final int splitAttemptCounter) {
        this.digest = digest;
        this.url = url;
        this.host = host;
        this.hostInverted = hostInverted;
        this.timestamp = timestamp;
        this.charset = charset;
        this.content = content;
        this.splitAttemptCounter = splitAttemptCounter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    public byte[] getDigest() {
        return digest;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setDigest(final byte[] digest) {
        this.digest = digest;
    }

    public int getSplitAttemptCounter() {
        return splitAttemptCounter;
    }

    public void setSplitAttemptCounter(final int splitAttemptCounter) {
        this.splitAttemptCounter = splitAttemptCounter;
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public void setHostInverted(final byte[] hostInverted) {
        this.hostInverted = hostInverted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
}
