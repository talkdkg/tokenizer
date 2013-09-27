package org.tokenizer.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TextImpl implements Text, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String text;
    List<Sentence> sentences = new ArrayList<Sentence>();
    int sentiment;
    Set<String> features = new TreeSet<String>();
    
    @Override
    public List<Sentence> getSentences() {
        return sentences;
    }
    
    public void addSentence(Sentence sentence) {
        sentences.add(sentence);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public Set<String> getFeatures() {
        return features;
    }
    
    @Override
    public int getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "text: " + text + "\nsentences: " + sentences + "\ntotal sentiment: " + sentiment + "\nfeatures: " + features;
    }
    
    
    
}
