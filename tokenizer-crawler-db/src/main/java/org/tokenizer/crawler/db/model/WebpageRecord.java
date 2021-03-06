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
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.CharsetUtils;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.MD5;
import org.tokenizer.crawler.db.DefaultValues;

import crawlercommons.fetcher.FetchedResult;

public class WebpageRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    // Primary Key, based upon "content" field; for better performance we desided not to concatenate "host" with
    // "content"...
    // common pattern: MD5.digest(ArrayUtils.addAll(host.getBytes(), content));
    // TODO: "Redirects" and other types of response should be stored in UrlRecord too
    private byte[] digest;

    // 'vertical' index, defined by 'baseUrl' value:
    private final String host;

    // fields from crawlercommons.fetcher.FetchedResult, except not-used Payload:
    private final String baseUrl;
    private final String fetchedUrl;
    private final long fetchTime;
    private final byte[] content;
    private final String contentType;
    private final int responseRate;
    private final Metadata headers;
    private final String newBaseUrl;
    private final int numRedirects;
    // should be IP address:
    private final String hostAddress;
    private final int httpStatus;
    private final String reasonPhrase;

    // Indexes for internal processing:
    private int extractOutlinksAttemptCounter;
    private int splitAttemptCounter;

    private ArrayList<byte[]> xmlLinks = new ArrayList<byte[]>();

    public WebpageRecord(final FetchedResult fetchedResult) {
        //@formatter:off
        this(
                null,
                HttpUtils.getHost(fetchedResult.getBaseUrl()), 
                fetchedResult.getBaseUrl(), 
                fetchedResult.getFetchedUrl(), 
                fetchedResult.getFetchTime(),
                fetchedResult.getContent(),
                fetchedResult.getContentType(), 
                fetchedResult.getResponseRate(), 
                fetchedResult.getHeaders(), 
                fetchedResult.getNewBaseUrl(), 
                fetchedResult.getNumRedirects(), 
                fetchedResult.getHostAddress(), 
                fetchedResult.getHttpStatus(), 
                fetchedResult.getReasonPhrase(),
                0,
                0);
        //@formatter:on
    }

    // @formatter:off
    public WebpageRecord(
            final byte[] digest,
            final String host, 
            final String baseUrl, 
            final String fetchedUrl,
            final long fetchTime, 
            final byte[] content, 
            final String contentType, 
            final int responseRate,
            final Metadata headers, 
            final String newBaseUrl, 
            final int numRedirects, 
            final String hostAddress,
            final int httpStatus, 
            final String reasonPhrase,
            final int extractOutlinksAttemptCounter,
            final int splitAttemptCounter) {
        // @formatter:on

        this.digest = digest;

        if (host != null) {
            this.host = host;
        }
        else {
            this.host = DefaultValues.EMPTY_STRING;
        }

        if (baseUrl != null) {
            this.baseUrl = baseUrl;
        }
        else {
            this.baseUrl = DefaultValues.EMPTY_STRING;
        }

        if (fetchedUrl != null) {
            this.fetchedUrl = fetchedUrl;
        }
        else {
            this.fetchedUrl = DefaultValues.EMPTY_STRING;
        }

        this.fetchTime = fetchTime;

        if (content != null) {
            this.content = content;
        }
        else {
            this.content = DefaultValues.EMPTY_ARRAY;
        }

        if (contentType != null) {
            this.contentType = contentType;
        }
        else {
            this.contentType = DefaultValues.EMPTY_STRING;
        }

        this.responseRate = responseRate;

        if (headers != null) {
            this.headers = headers;
        }
        else {
            this.headers = DefaultValues.EMPTY_METADATA;
        }

        if (newBaseUrl != null) {
            this.newBaseUrl = newBaseUrl;
        }
        else {
            this.newBaseUrl = DefaultValues.EMPTY_STRING;
        }

        this.numRedirects = numRedirects;

        if (hostAddress != null) {
            this.hostAddress = hostAddress;
        }
        else {
            this.hostAddress = DefaultValues.EMPTY_STRING;
        }

        this.httpStatus = httpStatus;

        if (reasonPhrase != null) {
            this.reasonPhrase = reasonPhrase;
        }
        else {
            this.reasonPhrase = DefaultValues.EMPTY_STRING;
        }

        this.extractOutlinksAttemptCounter = extractOutlinksAttemptCounter;

        this.splitAttemptCounter = splitAttemptCounter;
    }

    public String getHost() {
        return host;
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

    public byte[] getContent() {
        return content;
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

    public FetchedResult toFetchedResult() {
        // @formatter:off
        return new FetchedResult(
                baseUrl, 
                fetchedUrl, 
                fetchTime, 
                headers, 
                content, 
                contentType, 
                responseRate,
                null,
                newBaseUrl, 
                numRedirects, 
                hostAddress, 
                httpStatus, 
                reasonPhrase);
     // @formatter:on
    }

    public String getCharset() {
        return CharsetUtils.clean(HttpUtils.getCharsetFromContentType(getContentType()));
    }

    public int getExtractOutlinksAttemptCounter() {
        return extractOutlinksAttemptCounter;
    }

    public String getHostExtractOutlinksAttemptCounter() {
        return getHost() + String.valueOf(getExtractOutlinksAttemptCounter());
    }

    public void setExtractOutlinksAttemptCounter(int extractOutlinksAttemptCounter) {
        this.extractOutlinksAttemptCounter = extractOutlinksAttemptCounter;
    }

    public void incrementExtractOutlinksAttemptCounter() {
        this.extractOutlinksAttemptCounter++;
    }

    public int getSplitAttemptCounter() {
        return splitAttemptCounter;
    }

    public String getHostSplitAttemptCounter() {
        return getHost() + String.valueOf(getSplitAttemptCounter());
    }

    public void setSplitAttemptCounter(int splitAttemptCounter) {
        this.splitAttemptCounter = splitAttemptCounter;
    }

    public void incrementSplitAttemptCounter() {
        this.splitAttemptCounter++;
    }

    public byte[] getDigest() {
        if (this.digest == null) {
            if (this.content == null)
                this.digest = new byte[0];
            else
                this.digest = MD5.digest(ArrayUtils.addAll(getHost().getBytes(), getContent()));
        }
        return this.digest;
    }

    public ArrayList<byte[]> getXmlLinks() {
        return xmlLinks;
    }

    public void setXmlLinks(ArrayList<byte[]> xmlLinks) {
        this.xmlLinks = xmlLinks;
    }

    public void addXmlLink(byte[] digest) {
        this.xmlLinks.add(digest);
    }

    @Override
    public String toString()
    {
        try
        {
            return "WebpageRecord [host=" + host + ", baseUrl=" + baseUrl + ", fetchedUrl=" + fetchedUrl + ", fetchTime="
                    + fetchTime + ", content=" + new String(content, "UTF-8") + ", contentType=" + contentType
                    + ", responseRate=" + responseRate + ", headers=" + headers + ", newBaseUrl=" + newBaseUrl
                    + ", numRedirects=" + numRedirects + ", hostAddress=" + hostAddress + ", httpStatus=" + httpStatus
                    + ", reasonPhrase=" + reasonPhrase + ", extractOutlinksAttemptCounter=" + extractOutlinksAttemptCounter
                    + ", splitAttemptCounter=" + splitAttemptCounter + ", xmlLinks=" + xmlLinks + "]";
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
