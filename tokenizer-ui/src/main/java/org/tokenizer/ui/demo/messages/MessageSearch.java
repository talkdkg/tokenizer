package org.tokenizer.ui.demo.messages;

import org.tokenizer.ui.components.MessageSearchComponent;
import org.tokenizer.ui.demo.AbstractVaadinChartExample;

import com.vaadin.ui.Component;

public class MessageSearch extends AbstractVaadinChartExample {

    @Override
    protected Component getChart() {
        return new MessageSearchComponent();

    }

}
