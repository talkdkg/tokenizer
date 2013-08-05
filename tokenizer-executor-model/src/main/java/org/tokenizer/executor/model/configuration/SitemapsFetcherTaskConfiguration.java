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

public class SitemapsFetcherTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;

    private String host;
    private String agentName = "Tokenizer";
    private String emailAddress = "info@tokenizer.ca";
    private String webAddress = "http://www.tokenizer.ca";

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public String getImplementationName() {
        return SitemapsFetcherTaskConfiguration.class.getSimpleName();
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    @Override
    public String toString() {
        return "SitemapsFetcherTaskConfiguration [host=" + host
                + ", agentName=" + agentName + ", emailAddress=" + emailAddress
                + ", webAddress=" + webAddress + "]";
    }
    
    
    
}
