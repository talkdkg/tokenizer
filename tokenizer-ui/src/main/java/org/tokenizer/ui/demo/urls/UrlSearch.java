package org.tokenizer.ui.demo.urls;

import org.tokenizer.ui.components.UrlSearchComponent;
import org.tokenizer.ui.demo.AbstractScreen;

import com.vaadin.ui.Component;

public class UrlSearch extends AbstractScreen {

    @Override
    protected Component getChart() {
        return new UrlSearchComponent();

    }

}
