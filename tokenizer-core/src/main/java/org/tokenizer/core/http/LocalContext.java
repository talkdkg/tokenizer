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

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * BasicCookieStore is synchronized class. This class helps by managing a thread-based cache of an HttpContext (each
 * with own CookieStore). Instances of this class should typically be stored in static fields.
 */
public class LocalContext {

	private ThreadLocal<HttpContext> LOCAL = new ThreadLocal<HttpContext>() {
		protected HttpContext initialValue() {
			HttpContext localContext = new BasicHttpContext();
			CookieStore cookieStore = new LocalCookieStore();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			return localContext;
		}
	};

	public HttpContext get() {
		return LOCAL.get();
	}

}
