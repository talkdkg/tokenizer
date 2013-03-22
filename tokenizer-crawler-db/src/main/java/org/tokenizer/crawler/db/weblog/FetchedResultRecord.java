package org.tokenizer.crawler.db.weblog;

import java.util.Date;

import org.tokenizer.core.util.HttpUtils;

import com.netflix.astyanax.annotations.Component;

import crawlercommons.fetcher.FetchedResult;

public class FetchedResultRecord {

    private final String host;

    @Component(ordinal = 0)
    private final String url;

    @Component(ordinal = 1)
    private final Date timestamp;

    private final FetchedResult fetchedResult;

    public FetchedResultRecord(final String url, final Date timestamp,
            final FetchedResult fetchedResult) {
        this.url = url;
        this.host = HttpUtils.getHost(url);
        this.timestamp = timestamp;
        this.fetchedResult = fetchedResult;
    }

    public FetchedResult getFetchedResult() {
        return fetchedResult;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getHostInverted() {
        return HttpUtils.getHostInverted(host);
    }

}
