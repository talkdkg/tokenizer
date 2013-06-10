package org.tokenizer.nlp;

import java.util.List;
import java.util.Set;

public interface Sentence {
    Set<String> getFeatures();
    String getSentence();
    String getTreebank();
    List<String> getChunks();
    int getSentiment();
}
