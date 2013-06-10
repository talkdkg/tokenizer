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
