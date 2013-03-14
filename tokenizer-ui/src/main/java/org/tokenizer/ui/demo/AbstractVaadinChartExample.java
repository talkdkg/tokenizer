package org.tokenizer.ui.demo;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class AbstractVaadinChartExample extends VerticalLayout {

    private final VerticalLayout content;

    public AbstractVaadinChartExample() {
        content = this;
        content.setSizeFull();
    }

    protected abstract Component getChart();

    @Override
    public void attach() {
        super.attach();
        setup();
    }

    protected void setup() {
        if (content.getComponentCount() == 0) {
            final Component map = getChart();
            content.addComponent(map);
            content.setExpandRatio(map, 1);
        }
    }

}
