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

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.params.CookieSpecParamBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.AbortedFetchException;
import crawlercommons.fetcher.AbortedFetchReason;
import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.EncodingUtils;
import crawlercommons.fetcher.EncodingUtils.ExpandedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.IOFetchException;
import crawlercommons.fetcher.UrlFetchException;
import crawlercommons.fetcher.UserAgent;

public class SimpleHttpClient {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleHttpClient.class);

	private static final int DEFAULT_KEEP_ALIVE_DURATION = 5000;

	private static final int DEFAULT_SO_TIMEOUT = 30000;
	private static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
	private static final int DEFAULT_MAX_REDIRECTS = 8;

	// ThreadLocal replacement to traditional HttpContext
	private static LocalContext context = new LocalContext();

	/**
	 * CoreConnectionPNames.TCP_NODELAY='http.tcp.nodelay': determines whether Nagle's algorithm is to be used. Nagle's
	 * algorithm tries to conserve bandwidth by minimizing the number of segments that are sent. When applications wish
	 * to decrease network latency and increase performance, they can disable Nagle's algorithm (that is enable
	 * TCP_NODELAY. Data will be sent earlier, at the cost of an increase in bandwidth consumption. This parameter
	 * expects a value of type java.lang.Boolean. If this parameter is not set, TCP_NODELAY will be enabled (no delay).
	 */
	private static final boolean DEFAULT_TCP_NO_DELAY = true;

	/**
	 * CoreConnectionPNames.STALE_CONNECTION_CHECK='http.connection.stalecheck': determines whether stale connection
	 * check is to be used. Disabling stale connection check may result in a noticeable performance improvement (the
	 * check can cause up to 30 millisecond overhead per request) at the risk of getting an I/O error when executing a
	 * request over a connection that has been closed at the server side. This parameter expects a value of type
	 * java.lang.Boolean. For performance critical operations the check should be disabled. If this parameter is not
	 * set, the stale connection check will be performed before each request execution.
	 * 
	 * We don't need I/O exceptions in case if Server doesn't support Kee-Alive option; our client by default always
	 * tries keep-alive.
	 */
	private static final boolean DEFAULT_CONNECTION_STALE_CHECK = false;

	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us,en-gb,en;q=0.7,*;q=0.3";
	private static final String DEFAULT_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private static final String DEFAULT_ACCEPT_CHARSET = "utf-8,ISO-8859-1;q=0.7,*;q=0.7";
	private static final String DEFAULT_ACCEPT_ENCODING = "x-gzip, gzip";

	private static final String SSL_CONTEXT_NAMES[] = { "TLS", "Default", "SSL", };

	private int soTimeout = DEFAULT_SO_TIMEOUT;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	private boolean tcpNoDelay = DEFAULT_TCP_NO_DELAY;
	private boolean connectionStaleCheck = DEFAULT_CONNECTION_STALE_CHECK;
	private int maxRedirects = DEFAULT_MAX_REDIRECTS;

	private UserAgent userAgent;

	private final static int DEFAULT_MAX_THREADS = 1024;
	private int maxThreads = DEFAULT_MAX_THREADS;

	private final static int DEFAULT_MAX_PER_ROUTE = 2;
	private int maxPerRoute = DEFAULT_MAX_PER_ROUTE;

	private HttpClient httpClient;
	private ThreadSafeClientConnManager cm;

	private static final String TEXT_MIME_TYPES[] = { "text/html", "application/x-asp", "application/xhtml+xml", "application/vnd.wap.xhtml+xml", "text/xml", };

	protected Set<String> validMimeTypes = new HashSet<String>();
	protected Map<String, Integer> maxContentSizes = new HashMap<String, Integer>();

	public static final int DEFAULT_MAX_CONTENT_SIZE = 32 * 1024 * 1024;
	protected int defaultMaxContentSize = DEFAULT_MAX_CONTENT_SIZE;

	private static final int BUFFER_SIZE = 8 * 1024;

	/**
	 * average size of Amazon.com page is 300Kb; We should minimize bytearray copy
	 */
	private static final int DEFAULT_BYTEARRAY_SIZE = 256 * 1024;

	/**
	 * @deprecated
	 * @param maxThreads
	 * @param userAgent
	 */
	public SimpleHttpClient(int maxThreads, UserAgent userAgent) {
		this(userAgent);
	}

	/**
	 * @param userAgent
	 */
	public SimpleHttpClient(UserAgent userAgent) {
		this.userAgent = userAgent;
		init();
	}

	public FetchedResult get(String url) throws BaseFetchException {
		return request(new HttpGet(), url);
	}

	/**
	 * TODO: verify that...
	 */
	private FetchedResult request(HttpRequestBase request, String url) throws BaseFetchException {
		init();

		try {
			return doRequest(request, url);
		} catch (HttpFetchException e) {
			if (LOG.isTraceEnabled() && (e.getHttpStatus() != HttpStatus.SC_NOT_FOUND)) {
				LOG.trace(String.format("Exception fetching %s (%s)", url, e.getMessage()));
			}
			throw e;
		} catch (AbortedFetchException e) {
			if (e.getAbortReason() != AbortedFetchReason.INVALID_MIMETYPE) {
				LOG.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
			}
			throw e;
		} catch (BaseFetchException e) {
			LOG.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
			throw e;
		}
	}

	private void init() {

		if (httpClient == null) {

			synchronized (SimpleHttpClient.class) {

				if (httpClient != null)
					return;

				HttpParams params = new BasicHttpParams();

				HttpConnectionParams.setSoTimeout(params, soTimeout);
				HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
				HttpConnectionParams.setTcpNoDelay(params, tcpNoDelay);
				HttpConnectionParams.setStaleCheckingEnabled(params, connectionStaleCheck);

				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setUserAgent(params, userAgent.getUserAgentString());
				HttpProtocolParams.setContentCharset(params, "UTF-8");
				HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

				/**
				 * CoreProtocolPNames.USE_EXPECT_CONTINUE= 'http.protocol.expect-continue': activates the Expect:
				 * 100-Continue handshake for the entity enclosing methods. The purpose of the Expect: 100-Continue
				 * handshake is to allow the client that is sending a request message with a request body to determine
				 * if the origin server is willing to accept the request (based on the request headers) before the
				 * client sends the request body. The use of the Expect: 100-continue handshake can result in a
				 * noticeable performance improvement for entity enclosing requests (such as POST and PUT) that require
				 * the target server's authentication. The Expect: 100-continue handshake should be used with caution,
				 * as it may cause problems with HTTP servers and proxies that do not support HTTP/1.1 protocol. This
				 * parameter expects a value of type java.lang.Boolean. If this parameter is not set, HttpClient will
				 * not attempt to use the handshake.
				 */
				HttpProtocolParams.setUseExpectContinue(params, true);

				/**
				 * CoreProtocolPNames.WAIT_FOR_CONTINUE='http.protocol.wait-for-continue': defines the maximum period of
				 * time in milliseconds the client should spend waiting for a 100-continue response. This parameter
				 * expects a value of type java.lang.Integer. If this parameter is not set HttpClient will wait 3
				 * seconds for a confirmation before resuming the transmission of the request body.
				 */
				params.setIntParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 5000);

				HttpClientParams.setAuthenticating(params, false);
				HttpClientParams.setCookiePolicy(params, CookiePolicy.BEST_MATCH);
				CookieSpecParamBean cookieParams = new CookieSpecParamBean(params);
				cookieParams.setSingleHeader(false);

				ClientParamBean clientParams = new ClientParamBean(params);
				clientParams.setHandleRedirects(true);
				clientParams.setMaxRedirects(maxRedirects);

				HashSet<Header> defaultHeaders = new HashSet<Header>();
				defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, DEFAULT_ACCEPT_LANGUAGE));
				defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, DEFAULT_ACCEPT_CHARSET));
				defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, DEFAULT_ACCEPT_ENCODING));
				defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, DEFAULT_ACCEPT));

				clientParams.setDefaultHeaders(defaultHeaders);

				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

				//schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

				SSLSocketFactory sf = null;

				for (String contextName : SSL_CONTEXT_NAMES) {
					try {
						SSLContext sslContext = SSLContext.getInstance(contextName);
						sslContext.init(null, new TrustManager[] { new DummyX509TrustManager(null) }, null);
						sf = new SSLSocketFactory(sslContext, new DummyX509HostnameVerifier());
						break;
					} catch (NoSuchAlgorithmException e) {
						LOG.debug("SSLContext algorithm not available: " + contextName);
					} catch (Exception e) {
						LOG.debug("SSLContext can't be initialized: " + contextName, e);
					}
				}

				if (sf != null) {
					schemeRegistry.register(new Scheme("https", 443, sf));
				} else {
					LOG.warn("No valid SSLContext found for https");
				}

				ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(schemeRegistry);
				cm.setMaxTotal(maxThreads);
				cm.setDefaultMaxPerRoute(maxPerRoute);

				httpClient = new DefaultHttpClient(cm, params);

				((DefaultHttpClient) httpClient).setKeepAliveStrategy(new MyConnectionKeepAliveStrategy());

				IdleConnectionMonitorThread monitor = new IdleConnectionMonitorThread(cm);
				monitor.start();
			}
		}
	}

	protected void finalize() {
		httpClient.getConnectionManager().shutdown();
	}

	public static class IdleConnectionMonitorThread extends Thread {

		private final ClientConnectionManager connMgr;
		private volatile boolean shutdown;

		public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
			super();
			this.connMgr = connMgr;
			this.setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (!shutdown) {
					synchronized (this) {
						wait(5000);
						// Close expired connections
						connMgr.closeExpiredConnections();
						// Optionally, close connections
						// that have been idle longer than 30 sec
						connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				// terminate
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}

	public static class MyConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			if (response == null) {
				throw new IllegalArgumentException("HTTP response may not be null");
			}
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					try {
						return Long.parseLong(value) * 1000;
					} catch (NumberFormatException ignore) {
					}
				}
			}
			return DEFAULT_KEEP_ALIVE_DURATION;
		}

	}

	private static class DummyX509HostnameVerifier extends AbstractVerifier {

		@Override
		public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
			try {
				verify(host, cns, subjectAlts, false);
			} catch (SSLException e) {
				LOG.warn("Invalid SSL certificate for " + host + ": " + e.getMessage());
			}
		}

		@Override
		public final String toString() {
			return "DUMMY_VERIFIER";
		}

	}

	private static class DummyX509TrustManager implements X509TrustManager {
		private X509TrustManager standardTrustManager = null;

		/**
		 * Constructor for DummyX509TrustManager.
		 */
		public DummyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
			super();
			String algo = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory factory = TrustManagerFactory.getInstance(algo);
			factory.init(keystore);
			TrustManager[] trustmanagers = factory.getTrustManagers();
			if (trustmanagers.length == 0) {
				throw new NoSuchAlgorithmException(algo + " trust manager not supported");
			}
			this.standardTrustManager = (X509TrustManager) trustmanagers[0];
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[], String)
		 */
		public boolean isClientTrusted(X509Certificate[] certificates) {
			return true;
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[], String)
		 */
		public boolean isServerTrusted(X509Certificate[] certificates) {
			return true;
		}

		/**
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return this.standardTrustManager.getAcceptedIssuers();
		}

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// do nothing

		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// do nothing

		}
	}

	private FetchedResult doRequest(HttpRequestBase request, String url) throws BaseFetchException {
		LOG.trace("Fetching " + url);

		HttpResponse response;
		Metadata headerMap = new Metadata();
		boolean needAbort = true;
		String contentType = "";
		String mimeType = "";

		HttpHost httpHost = null;
		HttpUriRequest httpUriRequest = null;

		StringBuilder fetchTrace = null;
		if (LOG.isTraceEnabled()) {
			fetchTrace = new StringBuilder("Fetched url: " + url);
		}

		int httpStatus = -1;
		String reasonPhrase = null;

		try {

			request.setURI(new URI(url));
			response = httpClient.execute(request, context.get());

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				headerMap.add(header.getName(), header.getValue());
			}

			httpStatus = response.getStatusLine().getStatusCode();
			reasonPhrase = response.getStatusLine().getReasonPhrase();

			if (LOG.isTraceEnabled()) {
				fetchTrace.append("; status code: " + httpStatus);
				if (headerMap.get(HttpHeaders.CONTENT_LENGTH) != null) {
					fetchTrace.append("; Content-Length: " + headerMap.get(HttpHeaders.CONTENT_LENGTH));
				}
				if (headerMap.get(HttpHeaders.LOCATION) != null) {
					fetchTrace.append("; Location: " + headerMap.get(HttpHeaders.LOCATION));
				}
			}

			if ((httpStatus < 200) || (httpStatus >= 300)) {
				// We can't just check against SC_OK, as some wackos return 201, 202, etc
				throw new HttpFetchException(url, "Error fetching " + url, httpStatus, headerMap);
			}

			httpHost = (HttpHost) context.get().getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			httpUriRequest = (HttpUriRequest) context.get().getAttribute(ExecutionContext.HTTP_REQUEST);

			Header contentTypeHeader = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
			if (contentTypeHeader != null) {
				contentType = contentTypeHeader.getValue();
			}

			// Check if we should abort due to mime-type filtering. Note that this will fail if the server
			// doesn't report a mime-type, but that's how we want it as this configuration is typically
			// used when only a subset of parsers are installed/enabled, so we don't want the auto-detect
			// code in Tika to get triggered & try to process an unsupported type. If you want unknown
			// mime-types from the server to be processed, set "" as one of the valid mime-types in FetcherPolicy.
			mimeType = getMimeTypeFromContentType(contentType);
			Set<String> mimeTypes = getValidMimeTypes();
			if ((mimeTypes != null) && (mimeTypes.size() > 0)) {
				if (!mimeTypes.contains(mimeType)) {
					throw new AbortedFetchException(url, "Invalid mime-type: " + mimeType, AbortedFetchReason.INVALID_MIMETYPE);
				}
			}

			needAbort = false;

		} catch (ClientProtocolException e) {
			needAbort = false;
			throw new IOFetchException(url, e);
		} catch (IOException e) {
			needAbort = false;
			if (e instanceof ConnectionPoolTimeoutException) {
				ThreadSafeClientConnManager cm = (ThreadSafeClientConnManager) httpClient.getConnectionManager();
				int numConnections = cm.getConnectionsInPool();
				cm.closeIdleConnections(0, TimeUnit.MILLISECONDS);
				LOG.error(String.format("Got ConnectionPoolTimeoutException: %d connections before, %d after idle close", numConnections, cm.getConnectionsInPool()));
			}
			throw new IOFetchException(url, e);
		} catch (URISyntaxException e) {
			throw new UrlFetchException(url, e.getMessage());
		} catch (IllegalStateException e) {
			throw new UrlFetchException(url, e.getMessage());
		} catch (Exception e) {
			throw new IOFetchException(url, new IOException(e));
		} finally {
			safeAbort(needAbort, request);
		}

		// How much data we want to try to fetch:
		int maxContentSize = getMaxContentSize(mimeType);
		int targetLength = maxContentSize;
		boolean truncated = false;
		String contentLengthStr = headerMap.get(HttpHeaders.CONTENT_LENGTH);
		if (contentLengthStr != null) {
			try {
				int contentLength = Integer.parseInt(contentLengthStr);
				if (contentLength > targetLength) {
					truncated = true;
					LOG.debug("was {} bytes; truncated to {} bytes", contentLength, targetLength);
				} else {
					targetLength = contentLength;
				}
			} catch (NumberFormatException e) {
				LOG.warn("Invalid content length in header: " + contentLengthStr);
			}
		}

		// Now finally read in response body, up to targetLength bytes.
		// Note that entity might be null, for zero length responses.
		byte[] content = new byte[0];
		HttpEntity entity = response.getEntity();
		needAbort = true;

		if (entity != null) {
			InputStream in = null;

			try {
				in = entity.getContent();
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = 0;
				int totalRead = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream(DEFAULT_BYTEARRAY_SIZE);

				while ((totalRead < targetLength) && ((bytesRead = in.read(buffer, 0, Math.min(buffer.length, targetLength - totalRead))) != -1)) {
					totalRead += bytesRead;
					out.write(buffer, 0, bytesRead);
					if (Thread.interrupted()) {
						throw new AbortedFetchException(url, AbortedFetchReason.INTERRUPTED);
					}
				}

				content = out.toByteArray();
				needAbort = truncated || (in.available() > 0);
			} catch (IOException e) {
				throw new IOFetchException(url, e);
			} finally {
				safeAbort(needAbort, request);
				safeClose(in);
			}
		}

		if ((truncated) && (!isTextMimeType(mimeType))) {
			throw new AbortedFetchException(url, "Truncated non-text content: " + mimeType, AbortedFetchReason.CONTENT_SIZE);
		}

		String contentEncoding = headerMap.get(HttpHeaders.CONTENT_ENCODING);
		if (contentEncoding != null) {
			if (LOG.isTraceEnabled()) {
				fetchTrace.append("; Content-Encoding: " + contentEncoding);
			}
			try {
				if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
					if (truncated) {
						throw new AbortedFetchException(url, "Truncated compressed data", AbortedFetchReason.CONTENT_SIZE);
					} else {
						ExpandedResult expandedResult = EncodingUtils.processGzipEncoded(content, maxContentSize);
						truncated = expandedResult.isTruncated();
						if ((truncated) && (!isTextMimeType(mimeType))) {
							throw new AbortedFetchException(url, "Truncated decompressed image", AbortedFetchReason.CONTENT_SIZE);
						} else {
							content = expandedResult.getExpanded();
							if (LOG.isTraceEnabled()) {
								fetchTrace.append("; unzipped to " + content.length + " bytes");
							}
						}
						//                    } else if ("deflate".equals(contentEncoding)) {
						//                        content = EncodingUtils.processDeflateEncoded(content);
						//                        if (LOGGER.isTraceEnabled()) {
						//                            fetchTrace.append("; inflated to " + content.length + " bytes");
						//                        }
					}
				}
			} catch (IOException e) {
				throw new IOFetchException(url, e);
			}
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace(fetchTrace.toString());
		}

		return new FetchedResult(url, httpHost, httpUriRequest, System.currentTimeMillis(), content, contentType, headerMap, truncated, httpStatus, reasonPhrase);

	}

	protected static String getMimeTypeFromContentType(String contentType) {
		String result = "";
		MediaType mt = MediaType.parse(contentType);
		if (mt != null) {
			result = mt.getType() + "/" + mt.getSubtype();
		}

		return result;
	}

	public Set<String> getValidMimeTypes() {
		return validMimeTypes;
	}

	public void setValidMimeTypes(Set<String> validMimeTypes) {
		validMimeTypes = new HashSet<String>(validMimeTypes);
	}

	public void addValidMimeTypes(Set<String> validMimeTypes) {
		validMimeTypes.addAll(validMimeTypes);
	}

	public void addValidMimeType(String validMimeType) {
		validMimeTypes.add(validMimeType);
	}

	private boolean isTextMimeType(String mimeType) {

		LOG.info("Mime Type: {}", mimeType);

		for (String textContentType : TEXT_MIME_TYPES) {
			if (textContentType.equals(mimeType)) {
				return true;
			}
		}
		return false;
	}

	private static void safeAbort(boolean needAbort, HttpRequestBase request) {
		if (needAbort && (request != null)) {
			try {
				request.abort();
			} catch (Throwable t) {
				// Ignore any errors
			}
		}
	}

	public void setDefaultMaxContentSize(int defaultMaxContentSize) {
		this.defaultMaxContentSize = defaultMaxContentSize;
	}

	public int getDefaultMaxContentSize() {
		return this.defaultMaxContentSize;
	}

	public void setMaxContentSize(String mimeType, int maxContentSize) {
		this.maxContentSizes.put(mimeType, maxContentSize);
	}

	public int getMaxContentSize(String mimeType) {
		Integer result = this.maxContentSizes.get(mimeType);
		if (result == null) {
			result = getDefaultMaxContentSize();
		}

		return result;
	}

	private static void safeClose(Closeable o) {
		if (o != null) {
			try {
				o.close();
			} catch (Exception e) {
				// Ignore any errors
			}
		}
	}

}
