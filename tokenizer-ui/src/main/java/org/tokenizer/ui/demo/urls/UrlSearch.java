package org.tokenizer.ui.demo.urls;

import org.tokenizer.ui.components.UrlSearchComponent;
import org.tokenizer.ui.demo.AbstractVaadinChartExample;

import com.vaadin.ui.Component;

public class UrlSearch extends AbstractVaadinChartExample {

    @Override
    protected Component getChart() {
        return new UrlSearchComponent();

    }

}
