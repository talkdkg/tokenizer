package org.tokenizer.crawler.db.model;

import org.tokenizer.core.util.HttpUtils;

import com.netflix.astyanax.annotations.Component;

public class TimestampUrlIDX {

    private String host;

    @Component(ordinal = 0)
    private long timestamp;

    @Component(ordinal = 1)
    private String url;

    public TimestampUrlIDX() {
        super();
    }

    public TimestampUrlIDX(final String url) {
        this.timestamp = 0;
        this.url = url;
        this.host = HttpUtils.getHost(url);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TimestampUrlIDX [host=" + host + ", timestamp=" + timestamp + ", url=" + url + "]";
    }


}
