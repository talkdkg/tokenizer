package org.tokenizer.crawler.db.model;

import java.io.Serializable;
import java.util.HashMap;

import org.tokenizer.core.util.HttpUtils;

public class HostRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String host;
    private String tld;
    private byte[] hostInverted;
    private HashMap<String, Object> payload = new HashMap<String, Object>();

    public HostRecord(final String host) {
        this.host = host;
    }

    public HostRecord(final byte[] hostInverted) {
        this.hostInverted = hostInverted;
        this.host = HttpUtils.getHostUninverted(hostInverted);
    }

    public String getHost() {
        return host;
    }

    public String getTld() {
        if (host == null)
            return null;
        if (tld == null) {
            int index = host.lastIndexOf('.');
            if (index != -1)
                tld = host.substring(index + 1);
        }

        return tld;

    }

    public byte[] getHostInverted() {
        if (host == null)
            return null;
        if (hostInverted == null) {
            hostInverted = HttpUtils.getHostInverted(host);
        }

        return hostInverted;

    }

    public HashMap<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(HashMap<String, Object> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "HostRecord [host=" + host + "]";
    }

    
    
    
}