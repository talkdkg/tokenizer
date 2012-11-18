package org.tokenizer.executor.model.configuration;


public class SitemapsFetcherTaskConfiguration implements TaskConfiguration {
    private static final long serialVersionUID = 1L;
    
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
}
