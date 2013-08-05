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

public class HtmlSplitterTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;
    private String xpath;
    private String host;
    private int splitAttemptCounter = 0;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(final String xpath) {
        this.xpath = xpath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getSplitAttemptCounter() {
        return splitAttemptCounter;
    }

    public void setSplitAttemptCounter(final int splitAttemptCounter) {
        this.splitAttemptCounter = splitAttemptCounter;
    }

    @Override
    public String getImplementationName() {
        return HtmlSplitterTaskConfiguration.class.getSimpleName();
    }

    @Override
    public String toString() {
        return "HtmlSplitterTaskConfiguration [xpath=" + xpath + ", host="
                + host + ", splitAttemptCounter=" + splitAttemptCounter + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + splitAttemptCounter;
        result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        HtmlSplitterTaskConfiguration other = (HtmlSplitterTaskConfiguration) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (splitAttemptCounter != other.splitAttemptCounter)
            return false;
        if (xpath == null) {
            if (other.xpath != null)
                return false;
        } else if (!xpath.equals(other.xpath))
            return false;
        return true;
    }
}
