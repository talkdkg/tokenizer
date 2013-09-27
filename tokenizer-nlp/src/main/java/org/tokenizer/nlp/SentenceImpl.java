package org.tokenizer.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SentenceImpl implements Sentence, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    String sentence;
    String treebank;
    Set<String> features = new TreeSet<String>();
    List<String> chunks = new ArrayList<String>();
    int sentiment;
    
    @Override
    public String getSentence() {
        return sentence;
    }
    
    public void setSentence(final String sentence) {
        this.sentence = sentence;
    }
    
    @Override
    public String getTreebank() {
        return treebank;
    }
    
    public void setTreebank(final String treebank) {
        this.treebank = treebank;
    }
    
    @Override
    public Set<String> getFeatures() {
        return features;
    }
    
    @Override
    public List<String> getChunks() {
        return chunks;
    }
    
    @Override
    public void addFeature(String feature) {
        this.features.add(feature);
    }
    
    @Override
    public void addChunk(String chunk) {
        this.chunks.add(chunk);
        
    }
    
    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }
    
    @Override
    public int getSentiment() {
        return this.sentiment;
    }
    
    @Override
    public String toString() {
        return "\n\nsentence: " + sentence + "\ntreebank: " + treebank + "\nfeatures: " + features + "\nchunks: "
                + chunks
                + "\nsentiment: " + sentiment;
    }
    
}
