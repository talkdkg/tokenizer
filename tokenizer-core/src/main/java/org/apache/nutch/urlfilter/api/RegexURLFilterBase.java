/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.nutch.net.URLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic {@link org.apache.nutch.net.URLFilter URL filter} based on regular
 * expressions.
 * 
 * <p>
 * The regular expressions rules are expressed in a file. The file of rules is determined for each implementation using
 * the {@link #getRulesReader(Configuration conf)} method.
 * </p>
 * 
 * <p>
 * The format of this file is made of many rules (one per line):<br/>
 * <code>
 * [+-]&lt;regex&gt;
 * </code><br/>
 * where plus (<code>+</code>)means go ahead and index it and minus ( <code>-</code>)means no.
 * </p>
 * 
 * @author J&eacute;r&ocirc;me Charron
 */
public abstract class RegexURLFilterBase implements URLFilter {

    /** My logger */
    private final static Logger LOG = LoggerFactory.getLogger(RegexURLFilterBase.class);
    /** An array of applicable rules */
    private List<RegexRule> rules;

    /**
     * Constructs a new empty RegexURLFilterBase
     */
    public RegexURLFilterBase() {
    }

    /**
     * Constructs a new RegexURLFilter and init it with a file of rules.
     * 
     * @param filename
     *            is the name of rules file.
     */
    public RegexURLFilterBase(final File filename) throws IOException, IllegalArgumentException {
        this(new FileReader(filename));
    }

    /**
     * Constructs a new RegexURLFilter and inits it with a list of rules.
     * 
     * @param rules
     *            string with a list of rules, one rule per line
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public RegexURLFilterBase(final String rules) throws IOException, IllegalArgumentException {
        this(new StringReader(rules));
    }

    /**
     * Constructs a new RegexURLFilter and init it with a Reader of rules.
     * 
     * @param reader
     *            is a reader of rules.
     */
    protected RegexURLFilterBase(final Reader reader) throws IOException, IllegalArgumentException {
        rules = readRules(reader);
    }

    /**
     * Creates a new {@link RegexRule}.
     * 
     * @param sign
     *            of the regular expression. A <code>true</code> value means
     *            that any URL matching this rule must be included, whereas a <code>false</code> value means that any
     *            URL matching this rule
     *            must be excluded.
     * @param regex
     *            is the regular expression associated to this rule.
     */
    protected abstract RegexRule createRule(boolean sign, String regex);

    @Override
    public boolean accept(final String url) {
        for (RegexRule rule : rules) {
            if (rule.match(url)) {
                LOG.debug("Rule match for {} {}", url, rule);
                return rule.accept();
            }
        }
        LOG.warn("No rules match for {} - NOT FILTERING", url);
        return false;
    }

    /**
     * Read the specified file of rules.
     * 
     * @param reader
     *            is a reader of regular expressions rules.
     * @return the corresponding {@RegexRule rules}.
     */
    private List<RegexRule> readRules(final Reader reader) throws IOException, IllegalArgumentException {
        BufferedReader in = new BufferedReader(reader);
        List<RegexRule> rules = new ArrayList<RegexRule>();
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            char first = line.charAt(0);
            boolean sign = false;
            switch (first) {
                case '+' :
                    sign = true;
                    break;
                case '-' :
                    sign = false;
                    break;
                case ' ' :
                case '\n' :
                case '#' : // skip blank & comment lines
                    continue;
                default :
                    throw new IOException("Invalid first character: " + line);
            }
            String regex = line.substring(1);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Adding rule [" + regex + "]");
            }
            RegexRule rule = createRule(sign, regex);
            rules.add(rule);
        }
        return rules;
    }

}
