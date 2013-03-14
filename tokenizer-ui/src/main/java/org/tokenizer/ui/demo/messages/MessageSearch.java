package org.tokenizer.ui.demo.messages;

import org.tokenizer.ui.components.MessageSearchComponent;
import org.tokenizer.ui.demo.AbstractScreen;

import com.vaadin.ui.Component;

public class MessageSearch extends AbstractScreen {

    @Override
    protected Component getChart() {
        return new MessageSearchComponent();

    }

}
