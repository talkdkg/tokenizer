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
package org.tokenizer.executor.engine.twitter;

import org.tokenizer.core.StringPool;
import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;

public class TweetCollectorTaskConfiguration extends AbstractTaskConfiguration {

    private static final long serialVersionUID = 1L;
    private String keywords = StringPool.EMPTY;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String getImplementationName() {
        return TweetCollectorTaskConfiguration.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
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
        TweetCollectorTaskConfiguration other = (TweetCollectorTaskConfiguration) obj;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        }
        else if (!keywords.equals(other.keywords))
            return false;
        return true;
    }

    public boolean isSampleStream() {
        return StringPool.EMPTY.equals(keywords);
    }

}
