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
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.JavaSerializationUtils;
import org.tokenizer.crawler.db.DefaultValues;

public class UrlRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UrlRecord.class);

    // standard fields from crawlercommons.fetcher.FetchedResult except "content" and "payload"...
    // so that UrlRecord + WebpageRecord := FetchedResult, without huge amount of possibly duplicated content
    // TODO: WebpageRecord contains duplicate of some fields... such as baseUrl etc... shouldn't be a problem!

    // fields from crawlercommons.fetcher.FetchedResult, except not-used Payload:

    // Primary Key:
    private final String baseUrl;
    private String fetchedUrl;
    private long fetchTime;
    private String contentType;
    private int responseRate;
    private Metadata headers;
    private String newBaseUrl;
    private int numRedirects;
    // should be IP address:
    private String hostAddress;
    private int httpStatus;
    private String reasonPhrase;

    // calculated
    private final String host;
    private int fetchAttemptCounter;
    private byte[] webpageDigest;

    // @formatter:off
    public UrlRecord(
            final String baseUrl, 
            final String fetchedUrl, 
            final long fetchTime, 
            final String contentType, 
            final int responseRate,
            final Metadata headers,
            final String newBaseUrl, 
            final int numRedirects, 
            final String hostAddress,
            final int httpStatus, 
            final String reasonPhrase,
            final String host,
            final int fetchAttemptCounter, 
            final byte[] webpageDigest) {
        
        if (baseUrl != null) 
            this.baseUrl = baseUrl;
        else 
            this.baseUrl = DefaultValues.EMPTY_STRING;
        
        if (fetchedUrl != null)
            this.fetchedUrl = fetchedUrl;
        else
            this.fetchedUrl = DefaultValues.EMPTY_STRING;
        
        this.fetchTime = fetchTime;
        
        if (contentType != null)
            this.contentType = contentType;
        else
            this.contentType = DefaultValues.EMPTY_STRING;
        
        this.responseRate = responseRate;
        
        if (headers != null)
            this.headers = headers;
        else
            this.headers = DefaultValues.EMPTY_METADATA;
        
        if (newBaseUrl != null)
            this.newBaseUrl = newBaseUrl;
        else
            this.newBaseUrl = DefaultValues.EMPTY_STRING;
        
        this.numRedirects = numRedirects;
        
        if (hostAddress != null)
            this.hostAddress = hostAddress;
        else
            this.hostAddress = DefaultValues.EMPTY_STRING;
        
        this.httpStatus = httpStatus;
        
        if (reasonPhrase != null)
            this.reasonPhrase = reasonPhrase;
        else
            this.reasonPhrase = DefaultValues.EMPTY_STRING;
        
        if (host != null)
            this.host = host;
        else 
            this.host = HttpUtils.getHost(baseUrl);
        
        this.fetchAttemptCounter = fetchAttemptCounter;
        
        if (webpageDigest != null)
            this.webpageDigest = webpageDigest;
        else
            this.webpageDigest = DefaultValues.EMPTY_ARRAY;
    
    }
    // @formatter:on

    // @formatter:off
    public UrlRecord(final String baseUrl) {
        this(
                baseUrl, 
                null, 
                0, 
                null, 
                0,
                null,
                null, 
                0, 
                null,
                0, 
                null,
                null,
                0, 
                null); 
    }
    // @formatter:on

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getFetchedUrl() {
        return fetchedUrl;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public String getContentType() {
        return contentType;
    }

    public int getResponseRate() {
        return responseRate;
    }

    public Metadata getHeaders() {
        return headers;
    }

    public String getNewBaseUrl() {
        return newBaseUrl;
    }

    public int getNumRedirects() {
        return numRedirects;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getHost() {
        return host;
    }

    public int getFetchAttemptCounter() {
        return fetchAttemptCounter;
    }

    public byte[] getWebpageDigest() {
        return webpageDigest;
    }

    public void setFetchedUrl(String fetchedUrl) {
        this.fetchedUrl = fetchedUrl;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setResponseRate(int responseRate) {
        this.responseRate = responseRate;
    }

    public void setHeaders(Metadata headers) {
        this.headers = headers;
    }

    public void setNewBaseUrl(String newBaseUrl) {
        this.newBaseUrl = newBaseUrl;
    }

    public void setNumRedirects(int numRedirects) {
        this.numRedirects = numRedirects;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setFetchAttemptCounter(int fetchAttemptCounter) {
        this.fetchAttemptCounter = fetchAttemptCounter;
    }

    public void setWebpageDigest(byte[] webpageDigest) {
        this.webpageDigest = webpageDigest;
    }

    @Override
    public String toString() {
        return "UrlRecord [baseUrl=" + baseUrl + ", fetchedUrl=" + fetchedUrl + ", fetchTime=" + fetchTime
                + ", contentType=" + contentType + ", responseRate=" + responseRate + ", headers=" + headers
                + ", newBaseUrl=" + newBaseUrl + ", numRedirects=" + numRedirects + ", hostAddress=" + hostAddress
                + ", httpStatus=" + httpStatus + ", reasonPhrase=" + reasonPhrase + ", host=" + host
                + ", fetchAttemptCounter=" + fetchAttemptCounter + ", webpageDigest=" + Arrays.toString(webpageDigest)
                + "]";
    }

    //
    // public byte[] getHeadersSerialized() {
    // return JavaSerializationUtils.serialize(headers);
    // }
    //
    // public void setHeaders(byte[] headersSerialized) {
    // if (headersSerialized != null)
    // this.headers = (Metadata) JavaSerializationUtils.deserialize(headersSerialized);
    // }

}
