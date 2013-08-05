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
package org.apache.nutch.urlfilter.api;

/**
 * A generic regular expression rule.
 */
public abstract class RegexRule {

    private final boolean sign;
    private final String regex;

    /**
     * Constructs a new regular expression rule.
     * 
     * @param sign
     *            specifies if this rule must filter-in or filter-out.
     *            A <code>true</code> value means that any url matching this rule
     *            must be accepted, a <code>false</code> value means that any url
     *            matching this rule must be rejected.
     * @param regex
     *            is the regular expression used for matching (see {@link #match(String)} method).
     */
    protected RegexRule(final boolean sign, final String regex) {
        this.sign = sign;
        this.regex = regex;
    }

    /**
     * Return if this rule is used for filtering-in or out.
     * 
     * @return <code>true</code> if any url matching this rule must be accepted,
     *         otherwise <code>false</code>.
     */
    protected boolean accept() {
        return sign;
    }

    /**
     * Checks if a url matches this rule.
     * 
     * @param url
     *            is the url to check.
     * @return <code>true</code> if the specified url matches this rule,
     *         otherwise <code>false</code>.
     */
    protected abstract boolean match(String url);

    @Override
    public String toString() {
        return "RegexRule [sign=" + sign + ", regex=" + regex + "]";
    }

}
