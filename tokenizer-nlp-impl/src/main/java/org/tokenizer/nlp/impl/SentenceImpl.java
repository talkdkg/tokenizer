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

public class SentenceImpl implements Sentence {

    private String sentence;
    private Set<String> features = new HashSet<String>();
    private String treebank;
    private List<String> chunks = new ArrayList<String>();
    private int sentiment = 0;

    @Override
    public Set<String> getFeatures() {
        return features;
    }

    @Override
    public String getSentence() {
        return sentence;
    }

    @Override
    public String getTreebank() {
        return treebank;
    }

    @Override
    public List<String> getChunks() {
        return chunks;
    }

    @Override
    public int getSentiment() {
        return sentiment;
    }

}
