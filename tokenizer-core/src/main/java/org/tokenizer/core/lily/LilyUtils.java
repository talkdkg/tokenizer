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
package org.tokenizer.core.lily;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.client.LilyClient;
import org.lilyproject.client.NoServersException;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.util.zookeeper.ZkConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;

public class LilyUtils {

	private static final Logger LOG = LoggerFactory.getLogger(LilyUtils.class);

	/**
	 * Namespace "raw.http" reflects 1. RAW 2. CA Protocol is HTTP
	 */
	public static final String RAW_HTTP_NAMESPACE = "raw.http";

	/**
	 * Name reflects - "url" means traditional single-page-per-URL approach - primary key is constructed upon URL
	 * (contr-sample: "ping")
	 */
	public static final QName RAW_RECORD_TYPE_QNAME = new QName(RAW_HTTP_NAMESPACE, "url");

	public static final QName FETCHED_RESULT_TLD_QNAME = new QName(RAW_HTTP_NAMESPACE, "tld");
	public static final QName FETCHED_RESULT_BASE_URL_QNAME = new QName(RAW_HTTP_NAMESPACE, "baseUrl");
	public static final QName FETCHED_RESULT_FETCHED_URL_QNAME = new QName(RAW_HTTP_NAMESPACE, "fetchedUrl");
	public static final QName FETCHED_RESULT_FETCH_TIME_QNAME = new QName(RAW_HTTP_NAMESPACE, "timestamp");
	public static final QName FETCHED_RESULT_CONTENT_QNAME = new QName(RAW_HTTP_NAMESPACE, "content");
	public static final QName FETCHED_RESULT_CONTENT_TYPE_QNAME = new QName(RAW_HTTP_NAMESPACE, "contentType");
	public static final QName FETCHED_RESULT_LANGUAGE_QNAME = new QName(RAW_HTTP_NAMESPACE, "language");
	public static final QName FETCHED_RESULT_CHARSET_QNAME = new QName(RAW_HTTP_NAMESPACE, "charset");
	public static final QName FETCHED_RESULT_RESPONSE_CODE_QNAME = new QName(RAW_HTTP_NAMESPACE, "responseCode");
	public static final QName FETCHED_RESULT_RESPONSE_MESSAGE_QNAME = new QName(RAW_HTTP_NAMESPACE, "responseMessage");

	// for RSS feeds
	public static final QName RSS_TITLE_QNAME = new QName(RAW_HTTP_NAMESPACE, "rssTitle");
	public static final QName RSS_DESCRIPTION_QNAME = new QName(RAW_HTTP_NAMESPACE, "rssDescription");
	public static final QName RSS_PUBLISHED_DATE_QNAME = new QName(RAW_HTTP_NAMESPACE, "rssPublishedDate");
	public static final QName RSS_AUTHOR_QNAME = new QName(RAW_HTTP_NAMESPACE, "rssAuthor");
	public static final QName RSS_CATEGORY_QNAME = new QName(RAW_HTTP_NAMESPACE, "rssCategory");

	
	private static volatile LilyClient lilyClient;
	private static Repository repository;

	public static Repository getRepository() {

		if (repository == null) {
			synchronized (LilyUtils.class) {
				try {
					init();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return repository;
	}

	private static void init() throws IOException, InterruptedException, KeeperException, ZkConnectException, NoServersException, RepositoryException {
		String connectString = TokenizerConfig.getProperties().getProperty("zookeeper.connectString");
		lilyClient = new LilyClient(connectString, TokenizerConfig.getInt("zookeeper.timeout", 120000));
		repository = lilyClient.getRepository();
	}

	/**
	 * @throws IOException
	 */
	public static synchronized void close() throws IOException {
		if (lilyClient != null)
			lilyClient.close();
		lilyClient = null;
	}

	@Override
	public void finalize() throws Throwable {
		close();
	}

}
