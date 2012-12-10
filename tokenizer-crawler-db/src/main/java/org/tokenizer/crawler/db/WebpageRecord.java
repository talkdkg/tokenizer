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

import java.util.Arrays;
import java.util.Date;

import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;

public class WebpageRecord {

    private byte[] digest = DefaultValues.EMPTY_ARRAY;
    private String url = DefaultValues.EMPTY_STRING;
    private String host = DefaultValues.EMPTY_STRING;
    private byte[] hostInverted = DefaultValues.EMPTY_ARRAY;
    private Date timestamp = DefaultValues.EMPTY_DATE;
    private String charset = DefaultValues.EMPTY_STRING;
    private byte[] content = DefaultValues.EMPTY_ARRAY;
    private int splitAttemptCounter = 0;

    public WebpageRecord(final String host, final byte[] content) {
        setHost(host);
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

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
        this.hostInverted = HttpUtils.getHostInverted(host);
    }

    public byte[] getHostInverted() {
        return hostInverted;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public int getSplitAttemptCounter() {
        return splitAttemptCounter;
    }

    public void setSplitAttemptCounter(final int splitAttemptCounter) {
        this.splitAttemptCounter = splitAttemptCounter;
    }

    public byte[] getDigest() {
        return digest;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "WebpageRecord [digest=" + Arrays.toString(digest) + ", url="
                + url + ", host=" + host + ", hostInverted="
                + Arrays.toString(hostInverted) + ", timestamp=" + timestamp
                + ", charset=" + charset + ", splitAttemptCounter="
                + splitAttemptCounter + "]";
    }
}
