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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.utils.CharsetUtils;
import org.slf4j.Logger;
import org.tokenizer.core.datum.ParsedDatum;
import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.HttpHeaderNames;
import org.tokenizer.core.util.HttpUtils;
import org.tokenizer.core.util.IoUtils;
import org.tokenizer.core.util.ParserPolicy;

@SuppressWarnings("serial")
public class SimpleParser extends BaseParser {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SimpleParser.class);

	private boolean _extractLanguage = true;
	protected BaseContentExtractor _contentExtractor;
	protected BaseLinkExtractor _linkExtractor;
	private transient Parser _parser;

	public SimpleParser() {
		this(new ParserPolicy());
	}

	public SimpleParser(ParserPolicy parserPolicy) {
		this(new SimpleContentExtractor(), new SimpleLinkExtractor(), parserPolicy);
	}

	/**
	 * @param contentExtractor
	 *            to use instead of new {@link SimpleContentExtractor}()
	 * @param linkExtractor
	 *            to use instead of new {@link SimpleLinkExtractor}()
	 * @param parserPolicy
	 *            to customize operation of the parser <BR>
	 * <BR>
	 *            <B>Note:</B> There is no need to construct your own {@link SimpleLinkExtractor} simply to control the
	 *            set of link tags and attributes it processes. Instead, use {@link ParserPolicy#setLinkTags} and
	 *            {@link ParserPolicy#setLinkAttributeTypes}, and then pass this policy to
	 *            {@link SimpleParser#SimpleParser(ParserPolicy)}.
	 */
	public SimpleParser(BaseContentExtractor contentExtractor, BaseLinkExtractor linkExtractor, ParserPolicy parserPolicy) {
		super(parserPolicy);

		_contentExtractor = contentExtractor;
		_linkExtractor = linkExtractor;
	}

	protected synchronized void init() {
		if (_parser == null) {
			_parser = getTikaParser();
		}

		_contentExtractor.reset();
		_linkExtractor.setLinkTags(getParserPolicy().getLinkTags());
		_linkExtractor.setLinkAttributeTypes(getParserPolicy().getLinkAttributeTypes());
		_linkExtractor.reset();
	}

	@Override
	public Parser getTikaParser() {
		return new AutoDetectParser();
	}

	public void setExtractLanguage(boolean extractLanguage) {
		_extractLanguage = extractLanguage;
	}

	public boolean isExtractLanguage() {
		return _extractLanguage;
	}

	@Override
	public ParsedDatum parse(FetchedResult fetchedResult) throws Exception {
		init();

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(String.format("Parsing %s", fetchedResult.getBaseUrl()));
		}

		// Provide clues to the parser about the format of the content.
		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, fetchedResult.getBaseUrl());
		metadata.add(Metadata.CONTENT_TYPE, fetchedResult.getContentType());
		String charset = getCharset(fetchedResult);
		metadata.add(Metadata.CONTENT_LANGUAGE, getLanguage(fetchedResult, charset));

		InputStream is = new ByteArrayInputStream(fetchedResult.getContent(), 0, fetchedResult.getContent().length);

		try {
			URL baseUrl = getContentLocation(fetchedResult);
			metadata.add(Metadata.CONTENT_LOCATION, baseUrl.toExternalForm());

			Callable<ParsedDatum> c = new TikaCallable(_parser, _contentExtractor, _linkExtractor, is, metadata, isExtractLanguage());
			FutureTask<ParsedDatum> task = new FutureTask<ParsedDatum>(c);
			Thread t = new Thread(task);
			t.start();

			ParsedDatum result;
			try {
				result = task.get(getParserPolicy().getMaxParseDuration(), TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				task.cancel(true);
				throw e;
			} finally {
				t = null;
			}

			result.setHostAddress(fetchedResult.getHttpHost().getHostName());
			return result;
		} finally {
			IoUtils.safeClose(is);
		}
	}

	protected URL getContentLocation(FetchedResult fetchedResult) throws MalformedURLException {
		URL baseUrl = fetchedResult.getTargetUri().toURL();

		// See if we have a content location from the HTTP headers that we
		// should use as
		// the base for resolving relative URLs in the document.
		String clUrl = fetchedResult.getHeaders().get(HttpHeaderNames.CONTENT_LOCATION);
		if (clUrl != null) {
			// FUTURE KKr - should we try to keep processing if this step fails,
			// but
			// refuse to resolve relative links?
			baseUrl = new URL(baseUrl, clUrl);
		}
		return baseUrl;
	}

	/**
	 * Extract encoding from content-type
	 * 
	 * If a charset is returned, then it's a valid/normalized charset name that's supported on this platform.
	 * 
	 * @param datum
	 * @return charset in response headers, or null
	 */
	protected String getCharset(FetchedResult datum) {
		return CharsetUtils.clean(HttpUtils.getCharsetFromContentType(datum.getContentType()));
	}

	/**
	 * Extract language from (first) explicit header
	 * 
	 * @param fetchedDatum
	 * @param charset
	 * @return first language in response headers, or null
	 */
	protected String getLanguage(FetchedResult fetchedResult, String charset) {
		return fetchedResult.getHeaders().get(HttpHeaderNames.CONTENT_LANGUAGE);
	}

}
