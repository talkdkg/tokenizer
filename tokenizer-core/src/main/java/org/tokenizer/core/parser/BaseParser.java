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
package org.tokenizer.core.parser;

import java.io.Serializable;

import org.apache.http.HttpHeaders;
import org.apache.tika.parser.Parser;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.util.ParserPolicy;

import crawlercommons.fetcher.FetchedResult;

@SuppressWarnings("serial")
public abstract class BaseParser implements Serializable {

    private ParserPolicy _policy;

    public BaseParser(ParserPolicy policy) {
        _policy = policy;
    }

    public ParserPolicy getParserPolicy() {
        return _policy;
    }

    public abstract Parser getTikaParser();

    public abstract ParsedDatum parse(FetchedResult fetchedResult) throws Exception;

    /**
     * Extract encoding from content-type
     * 
     * If a charset is returned, then it's a valid/normalized charset name that's
     * supported on this platform.
     * 
     * @param fetchedResult
     * @return charset in response headers, or null
     */
    // protected String getCharset(FetchedResult fetchedResult) {
    // return CharsetUtils.clean(HttpUtils.getCharsetFromContentType(fetchedResult
    // .getContentType()));
    // }

    /**
     * Extract language from (first) explicit header
     * 
     * @param fetchedResult
     * @param charset
     * @return first language in response headers, or null
     */
    protected String getLanguage(FetchedResult fetchedResult) {
        return fetchedResult.getHeaders().get(HttpHeaders.CONTENT_LANGUAGE);
    }

}
