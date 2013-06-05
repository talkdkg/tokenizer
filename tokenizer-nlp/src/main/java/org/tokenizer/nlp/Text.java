package org.tokenizer.nlp;

import java.util.List;
import java.util.Set;

public interface Text {

    List<Sentence> getSentences();
    Set<String> getFeatures();
    int getSentiment();

}
