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
package org.tokenizer.core.parser;

import java.io.Serializable;

import org.apache.http.HttpHeaders;
import org.apache.tika.parser.Parser;
import org.apache.tika.utils.CharsetUtils;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.util.HttpUtils;
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
  
  public abstract ParsedDatum parse(FetchedResult fetchedResult)
      throws Exception;
  
  /**
   * Extract encoding from content-type
   * 
   * If a charset is returned, then it's a valid/normalized charset name that's
   * supported on this platform.
   * 
   * @param fetchedResult
   * @return charset in response headers, or null
   */
  //protected String getCharset(FetchedResult fetchedResult) {
  //  return CharsetUtils.clean(HttpUtils.getCharsetFromContentType(fetchedResult
  //      .getContentType()));
  //}
  
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
