package org.tokenizer.crawler.db.model;

import org.tokenizer.core.util.HttpUtils;

import com.netflix.astyanax.annotations.Component;

public class UrlSitemapIDX {

    private String host;

    @Component(ordinal = 0)
    private String url;

    public UrlSitemapIDX() {
        super();
    }

    public UrlSitemapIDX(final String url) {
        this.url = url;
        this.host = HttpUtils.getHost(url);
    }

    public String getHost() {
        if (this.host == null) this.host = HttpUtils.getHost(url);
        return this.host;
    }

    //public void setHost(String host) {
    //    this.host = host;
    //}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.host = HttpUtils.getHost(url);
    }

    @Override
    public String toString() {
        return "UrlSitemapIDX [host=" + getHost() + ", url=" + url + "]";
    }

}