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
package org.tokenizer.executor.engine;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.params.CookieSpecParamBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.AbortedFetchException;
import crawlercommons.fetcher.AbortedFetchReason;
import crawlercommons.fetcher.BadProtocolFetchException;
import crawlercommons.fetcher.BaseFetchException;
import crawlercommons.fetcher.EncodingUtils;
import crawlercommons.fetcher.EncodingUtils.ExpandedResult;
import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.HttpFetchException;
import crawlercommons.fetcher.IOFetchException;
import crawlercommons.fetcher.Payload;
import crawlercommons.fetcher.RedirectFetchException;
import crawlercommons.fetcher.RedirectFetchException.RedirectExceptionReason;
import crawlercommons.fetcher.UrlFetchException;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.LocalCookieStore;
import crawlercommons.fetcher.http.UserAgent;

@SuppressWarnings("serial")
public class SimpleHttpFetcher2 extends BaseHttpFetcher {
    private static Logger LOGGER = LoggerFactory.getLogger(SimpleHttpFetcher2.class);

    // We tried 10 seconds for all of these, but got a number of connection/read
    // timeouts for
    // sites that would have eventually worked, so bumping it up to 30 seconds.
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;

    // As per HttpComponents v.4.2.1, this will also include timeout needed to
    // get Connection from Pool.
    // From initial comment:
    // "This normally don't ever hit this timeout, since we manage the number of
    // fetcher threads to be <= the maxThreads value used to configure an
    // IHttpFetcher.
    // But the limit of connections/host can cause a timeout, when redirects
    // cause
    // multiple threads to hit the same domain. So jack the value way up."
    private static final int DEFAULT_CONNECTION_TIMEOUT = 100 * 1000;

    private static final int DEFAULT_MAX_THREADS = 1;

    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;

    private static final int DEFAULT_BYTEARRAY_SIZE = 32 * 1024;

    // Use the same values as Firefox (except that we don't accept deflate,
    // which we're not sure is implemented correctly - see the notes in
    // EncodingUtils/EncodingUtilsTest for more details).
    private static final String DEFAULT_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String DEFAULT_ACCEPT_CHARSET = "utf-8,ISO-8859-1;q=0.7,*;q=0.7";
    private static final String DEFAULT_ACCEPT_ENCODING = "x-gzip, gzip";

    // Keys used to access data in the Http execution context.
    private static final String PERM_REDIRECT_CONTEXT_KEY = "perm-redirect";
    private static final String REDIRECT_COUNT_CONTEXT_KEY = "redirect-count";
    private static final String HOST_ADDRESS = "host-address";

    // To be polite, set it small; if we use it, we will use less than a second
    // delay between subsequent fetches
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 5000;

    private IdleConnectionMonitorThread monitor;

    private final ThreadLocal<CookieStore> localCookieStore = new ThreadLocal<CookieStore>() {
        @Override
        protected CookieStore initialValue() {
            CookieStore cookieStore = new LocalCookieStore();
            return cookieStore;
        }
    };

    private static final String SSL_CONTEXT_NAMES[] = { "TLS", "Default", "SSL", };

    private static final String TEXT_MIME_TYPES[] = { "text/html", "application/x-asp", "application/xhtml+xml", "application/vnd.wap.xhtml+xml", 
        "application/rss+xml", "application/rdf+xml", "application/atom+xml", "application/xml", "text/xml" };
    
    private HttpVersion _httpVersion;
    private int _socketTimeout;
    private int _connectionTimeout;
    private int _maxRetryCount;

    transient private DefaultHttpClient _httpClient;

    private static class MyRequestRetryHandler implements HttpRequestRetryHandler {
        private final int _maxRetryCount;

        public MyRequestRetryHandler(final int maxRetryCount) {
            _maxRetryCount = maxRetryCount;
        }

        @Override
        public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Decide about retry #" + executionCount + " for exception " + exception.getMessage());
            }

            if (executionCount >= _maxRetryCount)
                // Do not retry if over max retry count
                return false;
            else if (exception instanceof NoHttpResponseException)
                // Retry if the server dropped connection on us
                return true;
            else if (exception instanceof SSLHandshakeException)
                // Do not retry on SSL handshake exception
                return false;

            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            // Retry if the request is considered idempotent
            return idempotent;
        }
    }

    private static class MyRedirectException extends RedirectException {

        private final URI _uri;
        private final RedirectExceptionReason _reason;
        private final int httpStatusCode;

        public MyRedirectException(final String message, final URI uri, final RedirectExceptionReason reason, final int httpStatusCode) {
            super(message);
            _uri = uri;
            _reason = reason;
            this.httpStatusCode = httpStatusCode;
        }

        public URI getUri() {
            return _uri;
        }

        public RedirectExceptionReason getReason() {
            return _reason;
        }

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

    }

    /**
     * Handler to record last permanent redirect (if any) in context.
     * 
     */
    private static class MyRedirectStrategy extends DefaultRedirectStrategy {

        private final RedirectMode _redirectMode;

        public MyRedirectStrategy(final RedirectMode redirectMode) {
            super();

            _redirectMode = redirectMode;
        }

        @Override
        public URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
            URI result = super.getLocationURI(request, response, context);

            // HACK - some sites return a redirect with an explicit port number
            // that's the same as
            // the default port (e.g. 80 for http), and then when you use this
            // to make the next
            // request, the presence of the port in the domain triggers another
            // redirect, so you
            // fail with a circular redirect error. Avoid that by converting the
            // port number to
            // -1 in that case.
            //
            // Detailed scenrio:
            // http://www.test.com/MyPage ->
            // http://www.test.com:80/MyRedirectedPage ->
            // http://www.test.com/MyRedirectedPage
            // We can save bandwidth:
            if (result.getScheme().equalsIgnoreCase("http") && (result.getPort() == 80)) {
                try {
                    result = new URI(result.getScheme(), result.getUserInfo(), result.getHost(), -1, result.getPath(), result.getQuery(), result.getFragment());
                } catch (URISyntaxException e) {
                    LOGGER.warn("Unexpected exception removing port from URI", e);
                }
            }

            // Keep track of the number of redirects.
            Integer count = (Integer) context.getAttribute(REDIRECT_COUNT_CONTEXT_KEY);
            if (count == null) {
                count = new Integer(0);
            }

            context.setAttribute(REDIRECT_COUNT_CONTEXT_KEY, count + 1);

            // Record the last permanent redirect
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                context.setAttribute(PERM_REDIRECT_CONTEXT_KEY, result);
            }

            RedirectExceptionReason reason = null;

            if (_redirectMode == RedirectMode.FOLLOW_NONE) {
                switch (statusCode) {
                    case HttpStatus.SC_MOVED_TEMPORARILY:
                    reason = RedirectExceptionReason.TEMP_REDIRECT_DISALLOWED;
                        break;
                    case HttpStatus.SC_MOVED_PERMANENTLY:
                    reason = RedirectExceptionReason.PERM_REDIRECT_DISALLOWED;
                        break;
                    case HttpStatus.SC_TEMPORARY_REDIRECT:
                    reason = RedirectExceptionReason.TEMP_REDIRECT_DISALLOWED;
                        break;
                    case HttpStatus.SC_SEE_OTHER:
                    reason = RedirectExceptionReason.SEE_OTHER_DISALLOWED;
                        break;
                    default:
                }
            }

            if (_redirectMode == RedirectMode.FOLLOW_TEMP) {
                switch (statusCode) {
                    case HttpStatus.SC_MOVED_PERMANENTLY:
                    reason = RedirectExceptionReason.PERM_REDIRECT_DISALLOWED;
                        break;
                    case HttpStatus.SC_SEE_OTHER:
                    reason = RedirectExceptionReason.SEE_OTHER_DISALLOWED;
                        break;
                    default:
                }
            }

            if (reason != null)
                throw new MyRedirectException("RedirectMode disallowed redirect: " + _redirectMode, result, reason, statusCode);

            return result;
        }
    }

    /**
     * Interceptor to record host address in context.
     * 
     */
    private static class MyRequestInterceptor implements HttpRequestInterceptor {

        @Override
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

            HttpInetConnection connection = (HttpInetConnection) (context.getAttribute(ExecutionContext.HTTP_CONNECTION));

            context.setAttribute(HOST_ADDRESS, connection.getRemoteAddress().getHostAddress());
        }
    }

    private static class DummyX509TrustManager implements X509TrustManager {
        private X509TrustManager standardTrustManager = null;

        /**
         * Constructor for DummyX509TrustManager.
         */
        public DummyX509TrustManager(final KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
            super();
            String algo = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory factory = TrustManagerFactory.getInstance(algo);
            factory.init(keystore);
            TrustManager[] trustmanagers = factory.getTrustManagers();
            if (trustmanagers.length == 0)
                throw new NoSuchAlgorithmException(algo + " trust manager not supported");
            this.standardTrustManager = (X509TrustManager) trustmanagers[0];
        }

        /**
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],
         *      String)
         */
        public boolean isClientTrusted(final X509Certificate[] certificates) {
            return true;
        }

        /**
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],
         *      String)
         */
        public boolean isServerTrusted(final X509Certificate[] certificates) {
            return true;
        }

        /**
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.standardTrustManager.getAcceptedIssuers();
        }

        @Override
        public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
            // do nothing

        }

        @Override
        public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException {
            // do nothing

        }
    }

    public static class MyConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

        @Override
        public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
            if (response == null)
                throw new IllegalArgumentException("HTTP response may not be null");
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

    public static class IdleConnectionMonitorThread extends Thread {

        private final ClientConnectionManager connMgr;

        public IdleConnectionMonitorThread(final ClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (!interrupted()) {
                // Close expired connections
                connMgr.closeExpiredConnections();
                // Optionally, close connections
                // that have been idle longer than 30 sec
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                try {
                    Thread.currentThread().sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public SimpleHttpFetcher2(final UserAgent userAgent) {
        this(DEFAULT_MAX_THREADS, userAgent);
    }

    public SimpleHttpFetcher2(final int maxThreads, final UserAgent userAgent) {
        super(maxThreads, userAgent);

        _httpVersion = HttpVersion.HTTP_1_1;
        _socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        _connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        _maxRetryCount = DEFAULT_MAX_RETRY_COUNT;

        // Just to be explicit, we rely on lazy initialization of this so that
        // we don't have to worry about serializing it.
        _httpClient = null;
    }

    public HttpVersion getHttpVersion() {
        return _httpVersion;
    }

    public void setHttpVersion(final HttpVersion httpVersion) {
        if (_httpClient == null) {
            _httpVersion = httpVersion;
        } else
            throw new IllegalStateException("Can't change HTTP version after HttpClient has been initialized");
    }

    public int getSocketTimeout() {
        return _socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeoutInMs) {
        if (_httpClient == null) {
            _socketTimeout = socketTimeoutInMs;
        } else
            throw new IllegalStateException("Can't change socket timeout after HttpClient has been initialized");
    }

    public int getConnectionTimeout() {
        return _connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeoutInMs) {
        if (_httpClient == null) {
            _connectionTimeout = connectionTimeoutInMs;
        } else
            throw new IllegalStateException("Can't change connection timeout after HttpClient has been initialized");
    }

    public int getMaxRetryCount() {
        return _maxRetryCount;
    }

    public void setMaxRetryCount(final int maxRetryCount) {
        _maxRetryCount = maxRetryCount;
    }

    @Override
    public FetchedResult get(final String url, final Payload payload) throws BaseFetchException {
        try {
            URL realUrl = new URL(url);
            String protocol = realUrl.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https"))
                throw new BadProtocolFetchException(url);
        } catch (MalformedURLException e) {
            throw new UrlFetchException(url, e.getMessage());
        }

        return request(new HttpGet(), url, payload);
    }

    private FetchedResult request(final HttpRequestBase request, final String url, final Payload payload) throws BaseFetchException {
        init();

        try {
            return doRequest(request, url, payload);
        } catch (HttpFetchException e) {
            // Don't bother generating a trace for a 404 (not found)
            if (LOGGER.isTraceEnabled() && (e.getHttpStatus() != HttpStatus.SC_NOT_FOUND)) {
                LOGGER.trace(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            }

            throw e;
        } catch (AbortedFetchException e) {
            // Don't bother reporting that we bailed because the mime-type
            // wasn't one that we wanted.
            if (e.getAbortReason() != AbortedFetchReason.INVALID_MIMETYPE) {
                LOGGER.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            }
            throw e;
        } catch (BaseFetchException e) {
            LOGGER.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            throw e;
        }
    }

    public FetchedResult fetch(final String url) throws BaseFetchException {
        return fetch(new HttpGet(), url, new Payload());
    }

    public FetchedResult fetch(final HttpRequestBase request, final String url, final Payload payload) throws BaseFetchException {
        init();

        try {
            return doRequest(request, url, payload);
        } catch (BaseFetchException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Exception fetching %s", url), e);
            }
            throw e;
        }
    }

    private FetchedResult doRequest(final HttpRequestBase request, final String url, final Payload payload) throws BaseFetchException {
        LOGGER.trace("Fetching " + url);

        HttpResponse response;
        long readStartTime;
        Metadata headerMap = new Metadata();
        String redirectedUrl = null;
        String newBaseUrl = null;
        int numRedirects = 0;
        boolean needAbort = true;
        String contentType = "";
        String mimeType = "";
        String hostAddress = null;
        int httpStatus = -1;
        String reasonPhrase = null;

        // Create a local instance of cookie store, and bind to local context
        // Without this we get killed w/lots of threads, due to sync() on single
        // cookie store.
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = localCookieStore.get();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        StringBuilder fetchTrace = null;
        if (LOGGER.isTraceEnabled()) {
            fetchTrace = new StringBuilder("Fetched url: " + url);
        }

        try {
            request.setURI(new URI(url));

            readStartTime = System.currentTimeMillis();

            response = _httpClient.execute(request, localContext);

            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                headerMap.add(header.getName(), header.getValue());
            }

            httpStatus = response.getStatusLine().getStatusCode();
            reasonPhrase = response.getStatusLine().getReasonPhrase();

            if (LOGGER.isTraceEnabled()) {
                fetchTrace.append("; status code: " + httpStatus);
                if (headerMap.get(HttpHeaders.CONTENT_LENGTH) != null) {
                    fetchTrace.append("; Content-Length: " + headerMap.get(HttpHeaders.CONTENT_LENGTH));
                }

                if (headerMap.get(HttpHeaders.LOCATION) != null) {
                    fetchTrace.append("; Location: " + headerMap.get(HttpHeaders.LOCATION));
                }
            }

            if ((httpStatus < 200) || (httpStatus >= 300))
                // We can't just check against SC_OK, as some wackos return 201,
                // 202, etc
                throw new HttpFetchException(url, "Error fetching " + url, httpStatus, headerMap);

            redirectedUrl = extractRedirectedUrl(url, localContext);

            URI permRedirectUri = (URI) localContext.getAttribute(PERM_REDIRECT_CONTEXT_KEY);
            if (permRedirectUri != null) {
                newBaseUrl = permRedirectUri.toURL().toExternalForm();
            }

            Integer redirects = (Integer) localContext.getAttribute(REDIRECT_COUNT_CONTEXT_KEY);
            if (redirects != null) {
                numRedirects = redirects.intValue();
            }

            hostAddress = (String) (localContext.getAttribute(HOST_ADDRESS));
            if (hostAddress == null)
                throw new UrlFetchException(url, "Host address not saved in context");

            Header cth = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if (cth != null) {
                contentType = cth.getValue();
            }

            // Check if we should abort due to mime-type filtering. Note that
            // this will fail if the server
            // doesn't report a mime-type, but that's how we want it as this
            // configuration is typically
            // used when only a subset of parsers are installed/enabled, so we
            // don't want the auto-detect
            // code in Tika to get triggered & try to process an unsupported
            // type. If you want unknown
            // mime-types from the server to be processed, set "" as one of the
            // valid mime-types in FetcherPolicy.
            mimeType = getMimeTypeFromContentType(contentType);
            Set<String> mimeTypes = getValidMimeTypes();
            if ((mimeTypes != null) && (mimeTypes.size() > 0)) {
                if (!mimeTypes.contains(mimeType))
                    throw new AbortedFetchException(url, "Invalid mime-type: " + mimeType, AbortedFetchReason.INVALID_MIMETYPE);
            }

            needAbort = false;
        } catch (ClientProtocolException e) {
            // Oleg guarantees that no abort is needed in the case of an
            // IOException (which is is a subclass of)
            needAbort = false;

            // If the root case was a "too many redirects" error, we want to map
            // this to a specific
            // exception that contains the final redirect.
            if (e.getCause() instanceof MyRedirectException) {
                MyRedirectException mre = (MyRedirectException) e.getCause();
                String redirectUrl = url;

                try {
                    redirectUrl = mre.getUri().toURL().toExternalForm();
                } catch (MalformedURLException e2) {
                    LOGGER.warn("Invalid URI saved during redirect handling: " + mre.getUri());
                }

                throw new RedirectFetchException(url, redirectUrl, mre.getReason(), mre.httpStatusCode);
            } else if (e.getCause() instanceof RedirectException) {
                e.printStackTrace();
                throw new RedirectFetchException(url, extractRedirectedUrl(url, localContext), RedirectExceptionReason.TOO_MANY_REDIRECTS, 399);
            } else
                throw new IOFetchException(url, e);
        } catch (IOException e) {
            // Oleg guarantees that no abort is needed in the case of an
            // IOException
            needAbort = false;
            throw new IOFetchException(url, e);
        } catch (URISyntaxException e) {
            throw new UrlFetchException(url, e.getMessage());
        } catch (IllegalStateException e) {
            throw new UrlFetchException(url, e.getMessage());
        } catch (BaseFetchException e) {
            throw e;
        } catch (Exception e) {
            // Map anything else to a generic IOFetchException
            // TODO KKr - create generic fetch exception
            throw new IOFetchException(url, new IOException(e));
        } finally {
            safeAbort(needAbort, request);
        }

        // Figure out how much data we want to try to fetch.
        int maxContentSize = getMaxContentSize(mimeType);
        int targetLength = maxContentSize;
        boolean truncated = false;
        String contentLengthStr = headerMap.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthStr != null) {
            try {
                int contentLength = Integer.parseInt(contentLengthStr);
                if (contentLength > targetLength) {
                    truncated = true;
                } else {
                    targetLength = contentLength;
                }
            } catch (NumberFormatException e) {
                // Ignore (and log) invalid content length values.
                LOGGER.warn("Invalid content length in header: " + contentLengthStr);
            }
        }

        // Now finally read in response body, up to targetLength bytes.
        // Note that entity might be null, for zero length responses.
        byte[] content = new byte[0];
        long readRate = 0;
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

                int readRequests = 0;
                int minResponseRate = getMinResponseRate();
                // TODO KKr - we need to monitor the rate while reading a
                // single block. Look at HttpClient
                // metrics support for how to do this. Once we fix this, fix
                // the test to read a smaller (< 20K)
                // chuck of data.
                while ((totalRead < targetLength) && ((bytesRead = in.read(buffer, 0, Math.min(buffer.length, targetLength - totalRead))) != -1)) {
                    readRequests += 1;
                    totalRead += bytesRead;
                    out.write(buffer, 0, bytesRead);

                    // Assume read time is at least one millisecond, to avoid
                    // DBZ exception.
                    long totalReadTime = Math.max(1, System.currentTimeMillis() - readStartTime);
                    readRate = (totalRead * 1000L) / totalReadTime;

                    // Don't bail on the first read cycle, as we can get a
                    // hiccup starting out.
                    // Also don't bail if we've read everything we need.
                    if ((readRequests > 1) && (totalRead < targetLength) && (readRate < minResponseRate))
                        throw new AbortedFetchException(url, "Slow response rate of " + readRate + " bytes/sec", AbortedFetchReason.SLOW_RESPONSE_RATE);

                    // Check to see if we got interrupted.
                    if (Thread.interrupted())
                        throw new AbortedFetchException(url, AbortedFetchReason.INTERRUPTED);
                }

                content = out.toByteArray();
                needAbort = truncated || (in.available() > 0);
            } catch (IOException e) {
                // We don't need to abort if there's an IOException
                throw new IOFetchException(url, e);
            } finally {
                safeAbort(needAbort, request);
                safeClose(in);
            }
        }

        // Toss truncated image content.
        if ((truncated) && (!isTextMimeType(mimeType)))
            throw new AbortedFetchException(url, "Truncated image", AbortedFetchReason.CONTENT_SIZE);

        // Now see if we need to uncompress the content.
        String contentEncoding = headerMap.get(HttpHeaders.CONTENT_ENCODING);
        if (contentEncoding != null) {
            if (LOGGER.isTraceEnabled()) {
                fetchTrace.append("; Content-Encoding: " + contentEncoding);
            }
/*
            
            try {
                if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                    if (truncated)
                        throw new AbortedFetchException(url, "Truncated compressed data", AbortedFetchReason.CONTENT_SIZE);
                    else {
                        ExpandedResult expandedResult = EncodingUtils.processGzipEncoded(content, maxContentSize);
                        truncated = expandedResult.isTruncated();
                        if ((truncated) && (!isTextMimeType(mimeType)))
                            throw new AbortedFetchException(url, "Truncated decompressed image", AbortedFetchReason.CONTENT_SIZE);
                        else {
                            content = expandedResult.getExpanded();
                            if (LOGGER.isTraceEnabled()) {
                                fetchTrace.append("; unzipped to " + content.length + " bytes");
                            }
                        }
                     }
                }
            } catch (IOException e) {
                throw new IOFetchException(url, e);
            }
*/
        
        }

        // Finally dump out the trace msg we've been building.
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(fetchTrace.toString());
        }

        // TODO KKr - Save truncated flag in FetchedResult/FetchedDatum.
        return new FetchedResult(url, redirectedUrl, System.currentTimeMillis(), headerMap, content, contentType, (int) readRate, payload, newBaseUrl, numRedirects, hostAddress, httpStatus,
                        reasonPhrase);
    }

    private boolean isTextMimeType(final String mimeType) {
        for (String textContentType : TEXT_MIME_TYPES) {
            if (textContentType.equals(mimeType))
                return true;
        }
        return false;
    }

    private String extractRedirectedUrl(final String url, final HttpContext localContext) {
        // This was triggered by HttpClient with the redirect count was
        // exceeded.
        HttpHost host = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        HttpUriRequest finalRequest = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);

        try {
            URL hostUrl = new URI(host.toURI()).toURL();
            return new URL(hostUrl, finalRequest.getURI().toString()).toExternalForm();
        } catch (MalformedURLException e) {
            LOGGER.warn("Invalid host/uri specified in final fetch: " + host + finalRequest.getURI());
            return url;
        } catch (URISyntaxException e) {
            LOGGER.warn("Invalid host/uri specified in final fetch: " + host + finalRequest.getURI());
            return url;
        }
    }

    private static void safeClose(final Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (Exception e) {
                // Ignore any errors
            }
        }
    }

    private static void safeAbort(final boolean needAbort, final HttpRequestBase request) {
        if (needAbort && (request != null)) {
            try {
                request.abort();
            } catch (Throwable t) {
                // Ignore any errors
            }
        }
    }

    private void init() {
        if (_httpClient == null) {
            synchronized (SimpleHttpFetcher2.class) {
                if (_httpClient != null)
                    return;

                // Create and initialize HTTP parameters
                HttpParams params = new BasicHttpParams();

                // Set the socket and connection timeout to be something
                // reasonable.
                HttpConnectionParams.setSoTimeout(params, _socketTimeout);
                HttpConnectionParams.setConnectionTimeout(params, _connectionTimeout);

                /*
                 * CoreConnectionPNames.TCP_NODELAY='http.tcp.nodelay':
                 * determines whether Nagle's algorithm is to be used. Nagle's
                 * algorithm tries to conserve bandwidth by minimizing the
                 * number of segments that are sent. When applications wish to
                 * decrease network latency and increase performance, they can
                 * disable Nagle's algorithm (that is enable TCP_NODELAY. Data
                 * will be sent earlier, at the cost of an increase in bandwidth
                 * consumption. This parameter expects a value of type
                 * java.lang.Boolean. If this parameter is not set, TCP_NODELAY
                 * will be enabled (no delay).
                 */
                HttpConnectionParams.setTcpNoDelay(params, true);

                /*
                 * CoreConnectionPNames.STALE_CONNECTION_CHECK=
                 * 'http.connection.stalecheck': determines whether stale
                 * connection check is to be used. Disabling stale connection
                 * check may result in a noticeable performance improvement (the
                 * check can cause up to 30 millisecond overhead per request) at
                 * the risk of getting an I/O error when executing a request
                 * over a connection that has been closed at the server side.
                 * This parameter expects a value of type java.lang.Boolean. For
                 * performance critical operations the check should be disabled.
                 * If this parameter is not set, the stale connection check will
                 * be performed before each request execution.
                 * 
                 * We don't need I/O exceptions in case if Server doesn't
                 * support Kee-Alive option; our client by default always tries
                 * keep-alive.
                 */
                // Even with stale checking enabled, a connection can "go stale"
                // between the check and the
                // next request. So we still need to handle the case of a closed
                // socket (from the server side),
                // and disabling this check improves performance.
                HttpConnectionParams.setStaleCheckingEnabled(params, false);

                HttpProtocolParams.setVersion(params, _httpVersion);
                HttpProtocolParams.setUserAgent(params, _userAgent.getUserAgentString());
                HttpProtocolParams.setContentCharset(params, "UTF-8");
                HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

                /*
                 * CoreProtocolPNames.USE_EXPECT_CONTINUE=
                 * 'http.protocol.expect-continue': activates the Expect:
                 * 100-Continue handshake for the entity enclosing methods. The
                 * purpose of the Expect: 100-Continue handshake is to allow the
                 * client that is sending a request message with a request body
                 * to determine if the origin server is willing to accept the
                 * request (based on the request headers) before the client
                 * sends the request body. The use of the Expect: 100-continue
                 * handshake can result in a noticeable performance improvement
                 * for entity enclosing requests (such as POST and PUT) that
                 * require the target server's authentication. The Expect:
                 * 100-continue handshake should be used with caution, as it may
                 * cause problems with HTTP servers and proxies that do not
                 * support HTTP/1.1 protocol. This parameter expects a value of
                 * type java.lang.Boolean. If this parameter is not set,
                 * HttpClient will not attempt to use the handshake.
                 */
                HttpProtocolParams.setUseExpectContinue(params, true);

                /*
                 * CoreProtocolPNames.WAIT_FOR_CONTINUE=
                 * 'http.protocol.wait-for-continue': defines the maximum period
                 * of time in milliseconds the client should spend waiting for a
                 * 100-continue response. This parameter expects a value of type
                 * java.lang.Integer. If this parameter is not set HttpClient
                 * will wait 3 seconds for a confirmation before resuming the
                 * transmission of the request body.
                 */
                params.setIntParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 5000);

                CookieSpecParamBean cookieParams = new CookieSpecParamBean(params);
                cookieParams.setSingleHeader(false);

                // Create and initialize scheme registry
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

                SSLSocketFactory sf = null;

                for (String contextName : SSL_CONTEXT_NAMES) {
                    try {
                        SSLContext sslContext = SSLContext.getInstance(contextName);
                        sslContext.init(null, new TrustManager[] { new DummyX509TrustManager(null) }, null);
                        sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                        break;
                    } catch (NoSuchAlgorithmException e) {
                        LOGGER.debug("SSLContext algorithm not available: " + contextName);
                    } catch (Exception e) {
                        LOGGER.debug("SSLContext can't be initialized: " + contextName, e);
                    }
                }

                if (sf != null) {
                    schemeRegistry.register(new Scheme("https", 443, sf));
                } else {
                    LOGGER.warn("No valid SSLContext found for https");
                }

                PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager(schemeRegistry);
                poolingClientConnectionManager.setMaxTotal(_maxThreads);
                poolingClientConnectionManager.setDefaultMaxPerRoute(getMaxConnectionsPerHost());

                _httpClient = new DefaultHttpClient(poolingClientConnectionManager, params);

                _httpClient.setHttpRequestRetryHandler(new MyRequestRetryHandler(_maxRetryCount));
                _httpClient.setRedirectStrategy(new MyRedirectStrategy(getRedirectMode()));
                _httpClient.addRequestInterceptor(new MyRequestInterceptor());

                // FUTURE KKr - support authentication
                HttpClientParams.setAuthenticating(params, false);
                HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);

                ClientParamBean clientParams = new ClientParamBean(params);
                if (getMaxRedirects() == 0) {
                    clientParams.setHandleRedirects(false);
                } else {
                    clientParams.setHandleRedirects(true);
                    clientParams.setMaxRedirects(getMaxRedirects());
                }

                // Set up default headers. This helps us get back from servers
                // what we want.
                HashSet<Header> defaultHeaders = new HashSet<Header>();
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, getAcceptLanguage()));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, DEFAULT_ACCEPT_CHARSET));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, DEFAULT_ACCEPT_ENCODING));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, DEFAULT_ACCEPT));

                clientParams.setDefaultHeaders(defaultHeaders);

                _httpClient.setKeepAliveStrategy(new MyConnectionKeepAliveStrategy());

                monitor = new IdleConnectionMonitorThread(poolingClientConnectionManager);
                monitor.start();

            }
        }
    }

    @Override
    public void abort() {
        // TODO Actually try to abort
    }

    @Override
    protected void finalize() {
        monitor.interrupt();
        _httpClient.getConnectionManager().shutdown();
        _httpClient = null;
    }

}
