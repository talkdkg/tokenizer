package org.tokenizer.nlp.impl;

import org.tokenizer.nlp.NlpTools;
import org.tokenizer.nlp.Text;

/**
 *
 */
public class NlpToolsImpl implements NlpTools
{

    @Override
    public Text process(String text) {
        return new TextImpl();
    }
 }
