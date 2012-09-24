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
package org.tokenizer.core.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.CharsetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.util.HttpUtils;

public class FetchedResult {

	private static final Logger LOG = LoggerFactory.getLogger(FetchedResult.class);

	private final String baseUrl;
	private final HttpHost httpHost;
	private final HttpUriRequest httpUriRequest;
	private final long fetchTime;
	private final byte[] content;
	private final String contentType;
	private final Metadata headers;
	private final boolean truncated;
	private final int httpStatus;
	private final String reasonPhrase;

	public FetchedResult(String baseUrl, HttpHost httpHost, HttpUriRequest httpUriRequest, long fetchTime, byte[] content, String contentType, Metadata headers, boolean truncated,
			int httpStatus, String reasonPhrase) {
		this.baseUrl = baseUrl;
		this.httpHost = httpHost;
		this.httpUriRequest = httpUriRequest;
		this.fetchTime = fetchTime;
		this.content = content;
		this.contentType = contentType;
		this.headers = headers;
		this.truncated = true;
		this.httpStatus = httpStatus;
		this.reasonPhrase = reasonPhrase;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public URI getTargetUri() {
		URI uri = this.httpUriRequest.getURI();

		if (uri.getScheme() == null) {
			try {
				URI targetUri = new URI(httpHost.getSchemeName(), uri.getUserInfo(), httpHost.getHostName(), httpHost.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
				return targetUri;

			} catch (URISyntaxException e) {
				LOG.error("", e);
				return null;
			}

		}
		return uri;

	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public HttpHost getHttpHost() {
		return httpHost;
	}

	public HttpUriRequest getHttpUriRequest() {
		return httpUriRequest;
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

	public Metadata getHeaders() {
		return headers;
	}

	public boolean isTruncated() {
		return truncated;
	}

	/**
	 * Extract encoding from content-type
	 * 
	 * If a charset is returned, then it's a valid/normalized charset name that's supported on this platform.
	 * 
	 * @return charset in response headers, or null
	 */

	public String getCharset() {
		return CharsetUtils.clean(HttpUtils.getCharsetFromContentType(getContentType()));
	}

	/**
	 * Extract language from (first) explicit header
	 * 
	 * @return first language in response headers, or null
	 */
	public String getLanguage() {
		return getHeaders().get(HttpHeaderNames.CONTENT_LANGUAGE);
	}

}
