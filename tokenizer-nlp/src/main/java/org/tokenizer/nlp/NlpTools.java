package org.tokenizer.nlp;

public interface NlpTools {

    Text processFeatureExtraction(String text);

    int prunTree(Text.Sentence sentence);

}
