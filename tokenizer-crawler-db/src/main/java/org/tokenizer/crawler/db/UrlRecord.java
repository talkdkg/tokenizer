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

import java.io.Serializable;
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.JavaSerializationUtils;

public class UrlRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(UrlRecord.class);

    // standard
    private String baseUrl = DefaultValues.EMPTY_STRING;
    private String fetchedUrl = DefaultValues.EMPTY_STRING;
    private long fetchTime = 0L;
    private String contentType = DefaultValues.EMPTY_STRING;
    private Metadata headers = DefaultValues.EMPTY_METADATA;
    private String newBaseUrl = DefaultValues.EMPTY_STRING;
    private int numRedirects = 0;
    private String hostAddress = DefaultValues.EMPTY_STRING;
    private int httpStatus = 0;
    private String reasonPhrase = DefaultValues.EMPTY_STRING;

    // calculated
    private int fetchAttemptCounter = 0;
    private int httpResponseCode = 0;
    private byte[] webpageDigest = DefaultValues.EMPTY_ARRAY;

    public UrlRecord(final String url) {
        this.baseUrl = url;
    }

    public UrlRecord(final String url, final long fetchTime,
            final byte[] webpageDigest) {
        this.baseUrl = url;
        this.fetchTime = fetchTime;
        this.webpageDigest = webpageDigest;
    }

    public UrlRecord(String baseUrl, String fetchedUrl, long fetchTime,
            String contentType, Metadata headers, String newBaseUrl,
            int numRedirects, String hostAddress, int httpStatus,
            String reasonPhrase, int fetchAttemptCounter, int httpResponseCode,
            byte[] webpageDigest) {
        super();
        this.baseUrl = baseUrl;
        this.fetchedUrl = fetchedUrl;
        this.fetchTime = fetchTime;
        this.contentType = contentType;
        this.headers = headers;
        this.newBaseUrl = newBaseUrl;
        this.numRedirects = numRedirects;
        this.hostAddress = hostAddress;
        this.httpStatus = httpStatus;
        this.reasonPhrase = reasonPhrase;
        this.fetchAttemptCounter = fetchAttemptCounter;
        this.httpResponseCode = httpResponseCode;
        this.webpageDigest = webpageDigest;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseHost() {
        return HttpUtils.getHost(baseUrl);
    }

    public String getFetchedUrl() {
        return fetchedUrl;
    }

    public void setFetchedUrl(String fetchedUrl) {
        this.fetchedUrl = fetchedUrl;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Metadata getHeaders() {
        return headers;
    }

    public byte[] getHeadersSerialized() {
        return JavaSerializationUtils.serialize(headers);
    }

    public void setHeaders(Metadata headers) {
        this.headers = headers;
    }

    public void setHeaders(byte[] headersSerialized) {
        if (headersSerialized != null)
            this.headers = (Metadata) JavaSerializationUtils
                    .deserialize(headersSerialized);
    }

    public String getNewBaseUrl() {
        return newBaseUrl;
    }

    public void setNewBaseUrl(String newBaseUrl) {
        this.newBaseUrl = newBaseUrl;
    }

    public int getNumRedirects() {
        return numRedirects;
    }

    public void setNumRedirects(int numRedirects) {
        this.numRedirects = numRedirects;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public int getFetchAttemptCounter() {
        return fetchAttemptCounter;
    }

    public void setFetchAttemptCounter(int fetchAttemptCounter) {
        this.fetchAttemptCounter = fetchAttemptCounter;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    public byte[] getWebpageDigest() {
        return webpageDigest;
    }

    public void setWebpageDigest(byte[] webpageDigest) {
        this.webpageDigest = webpageDigest;
    }

    @Override
    public String toString() {
        return "UrlRecord [baseUrl=" + baseUrl + ", fetchedUrl=" + fetchedUrl
                + ", fetchTime=" + fetchTime + ", contentType=" + contentType
                + ", headers=" + headers + ", newBaseUrl=" + newBaseUrl
                + ", numRedirects=" + numRedirects + ", hostAddress="
                + hostAddress + ", httpStatus=" + httpStatus
                + ", reasonPhrase=" + reasonPhrase + ", fetchAttemptCounter="
                + fetchAttemptCounter + ", httpResponseCode="
                + httpResponseCode + ", webpageDigest="
                + Arrays.toString(webpageDigest) + "]";
    }

}
