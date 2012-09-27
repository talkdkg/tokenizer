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
package org.tokenizer.executor.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.http.FetchedResult;
import org.tokenizer.core.http.SimpleHttpClient;

import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.HttpFetchException;

public class FetcherEngineUtils {

	public static FetchedResult fetch(String url, MetricsCache metricsCache, SimpleHttpClient simpleHttpClient) throws InterruptedException {
		long start = System.currentTimeMillis();
		FetchedResult fetchedResult = null;
		try {
			fetchedResult = simpleHttpClient.get(url);
			if (fetchedResult.getHttpStatus() >= 200 && fetchedResult.getHttpStatus() < 300) {
				metricsCache.increment(MetricsCache.TOTAL_RESPONSE_TIME_KEY, System.currentTimeMillis() - start);
				metricsCache.increment(MetricsCache.URL_OK_KEY);
			} else {
				metricsCache.increment(MetricsCache.OTHER_ERRORS);
			}
		} catch (BaseFetchException e) {
			// TODO: forced... due to existing (inherited from crawler-commons) design:
			if (e.getMessage().contains("Aborted due to INTERRUPTED")) {
				throw new InterruptedException();
			}
			if (e instanceof HttpFetchException) {
				fetchedResult = new FetchedResult(url, null, null, 0, null, null, null, false, ((HttpFetchException) e).getHttpStatus(), e.getMessage());
			} else {
				fetchedResult = new FetchedResult(url, null, null, 0, null, null, null, false, -1, e.getMessage());
			}
			metricsCache.increment(MetricsCache.URL_ERROR_KEY);
		}
		return fetchedResult;
	}


}
