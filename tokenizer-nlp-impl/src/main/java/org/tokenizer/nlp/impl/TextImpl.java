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
package org.tokenizer.nlp.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tokenizer.nlp.Sentence;
import org.tokenizer.nlp.Text;

public class TextImpl implements Text {

    private List<Sentence> sentences = new ArrayList<Sentence>();
    private Set<String> features = new HashSet<String>();
    private int sentiment = 0;

    @Override
    public List<Sentence> getSentences() {
        return sentences;
    }

    @Override
    public Set<String> getFeatures() {
        return features;
    }

    @Override
    public int getSentiment() {
        return sentiment;
    }

}
