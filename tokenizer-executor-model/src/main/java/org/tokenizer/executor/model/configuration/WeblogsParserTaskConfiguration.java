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
package org.tokenizer.executor.model.configuration;

public class WeblogsParserTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;

    private String url = "";
    private int delay = 300000;

    private String agentName = "Tokenizer";
    private String emailAddress = "info@tokenizer.ca";
    private String webAddress = "http://www.tokenizer.ca";

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(final String agentName) {
        this.agentName = agentName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(final String webAddress) {
        this.webAddress = webAddress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String getImplementationName() {
        return WeblogsParserTaskConfiguration.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((agentName == null) ? 0 : agentName.hashCode());
        result = prime * result + delay;
        result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((webAddress == null) ? 0 : webAddress.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WeblogsParserTaskConfiguration other = (WeblogsParserTaskConfiguration) obj;
        if (agentName == null) {
            if (other.agentName != null)
                return false;
        }
        else if (!agentName.equals(other.agentName))
            return false;
        if (delay != other.delay)
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        }
        else if (!emailAddress.equals(other.emailAddress))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        if (webAddress == null) {
            if (other.webAddress != null)
                return false;
        }
        else if (!webAddress.equals(other.webAddress))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WeblogsSubscriberTaskConfiguration [url=" + url + ", delay=" + delay + ", agentName=" + agentName
                + ", emailAddress=" + emailAddress + ", webAddress=" + webAddress + "]";
    }

}
