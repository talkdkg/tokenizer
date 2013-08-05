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
package org.apache.nutch.urlfilter.automaton;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.PatternSyntaxException;

import org.apache.nutch.urlfilter.api.RegexRule;
import org.apache.nutch.urlfilter.api.RegexURLFilterBase;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

/**
 * RegexURLFilterBase implementation based on the <a
 * href="http://www.brics.dk/automaton/">dk.brics.automaton</a> Finite-State
 * Automata for Java<sup>TM</sup>.
 * 
 * @author J&eacute;r&ocirc;me Charron
 * @see <a href="http://www.brics.dk/automaton/">dk.brics.automaton</a>
 */
public class AutomatonURLFilter extends RegexURLFilterBase {

    public static final String URLFILTER_AUTOMATON_FILE = "urlfilter.automaton.file";
    public static final String URLFILTER_AUTOMATON_RULES = "urlfilter.automaton.rules";

    public AutomatonURLFilter() {
        super();
    }

    public AutomatonURLFilter(final String filename) throws IOException,
            PatternSyntaxException {
        super(filename);
    }

    public AutomatonURLFilter(final Reader reader) throws IOException,
            IllegalArgumentException {
        super(reader);
    }

    @Override
    protected RegexRule createRule(final boolean sign, final String regex) {
        return new Rule(sign, regex);
    }


    private class Rule extends RegexRule {

        private final RunAutomaton automaton;

        Rule(final boolean sign, final String regex) {
            super(sign, regex);
            automaton = new RunAutomaton(
                    new RegExp(regex, RegExp.ALL).toAutomaton());
        }

        @Override
        protected boolean match(final String url) {
            return automaton.run(url);
        }

    }
}
