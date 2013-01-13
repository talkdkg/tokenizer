/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.urlfilter.automaton;

// JDK imports
import java.io.IOException;
import java.io.Reader;
import java.util.regex.PatternSyntaxException;

import org.apache.nutch.urlfilter.api.RegexRule;
import org.apache.nutch.urlfilter.api.RegexURLFilterBase;

import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

// Automaton imports
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

    // Inherited Javadoc
    @Override
    protected RegexRule createRule(final boolean sign, final String regex) {
        return new Rule(sign, regex);
    }

    /*
     * ------------------------------------ *
     * </implementation:RegexURLFilterBase> *
     * ------------------------------------
     */
    public static void main(final String args[]) throws IOException {
        main(new AutomatonURLFilter(), args);
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
