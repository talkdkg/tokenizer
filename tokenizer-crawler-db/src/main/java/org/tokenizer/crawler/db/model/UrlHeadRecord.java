/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
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

import org.apache.tika.metadata.Metadata;

import crawlercommons.fetcher.FetchedResult;

public class UrlHeadRecord {

    private final String baseUrl;
    private final String fetchedUrl;
    private final long fetchTime;
    private final String contentType;
    private final Metadata headers;
    private final String newBaseUrl;
    private final int numRedirects;
    private final String hostAddress;
    private final int httpStatus;
    private final String reasonPhrase;

    public UrlHeadRecord(FetchedResult fetchedResult) {

        baseUrl = fetchedResult.getBaseUrl();
        fetchedUrl = fetchedResult.getFetchedUrl();
        fetchTime = fetchedResult.getFetchTime();
        contentType = fetchedResult.getContentType();
        headers = fetchedResult.getHeaders();
        newBaseUrl = fetchedResult.getNewBaseUrl();
        numRedirects = fetchedResult.getNumRedirects();
        hostAddress = fetchedResult.getHostAddress();
        httpStatus = fetchedResult.getHttpStatus();
        reasonPhrase = fetchedResult.getReasonPhrase();
    }

    public UrlHeadRecord(final String baseUrl, final String fetchedUrl,
            final long fetchTime, final String contentType,
            final Metadata headers, final String newBaseUrl,
            final int numRedirects, final String hostAddress,
            final int httpStatus, final String reasonPhrase
    ) {
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
    }

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

    @Override
    public String toString() {
        return "UrlHeadRecord [baseUrl=" + baseUrl + ", fetchedUrl="
                + fetchedUrl + ", fetchTime=" + fetchTime + ", contentType="
                + contentType + ", headers=" + headers + ", newBaseUrl="
                + newBaseUrl + ", numRedirects=" + numRedirects
                + ", hostAddress=" + hostAddress + ", httpStatus=" + httpStatus
                + ", reasonPhrase=" + reasonPhrase + "]";
    }
    
    
}
