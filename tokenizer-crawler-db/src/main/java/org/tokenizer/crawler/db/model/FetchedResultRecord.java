package org.tokenizer.crawler.db.model;

import java.io.Serializable;
import java.util.Date;

import org.tokenizer.core.util.HttpUtils;

import com.netflix.astyanax.annotations.Component;

import crawlercommons.fetcher.FetchedResult;

public class FetchedResultRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;

    @Component(ordinal = 0)
    private String url;

    @Component(ordinal = 1)
    private Date timestamp;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private FetchedResult fetchedResult;

    public FetchedResultRecord() {
    }

    public FetchedResultRecord(final String url, final Date timestamp,
            final FetchedResult fetchedResult) {
        this.url = url;
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
        if (host == null)
            host = HttpUtils.getHost(url);
        return host;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getHostInverted() {
        return HttpUtils.getHostInverted(getHost());
    }

    @Override
    public String toString() {
        return "FetchedResultRecord [host=" + host + ", url=" + url
                + ", timestamp=" + timestamp + "]";
        //+ ", fetchedResult="
        //        + fetchedResult + "]";
    }

}
