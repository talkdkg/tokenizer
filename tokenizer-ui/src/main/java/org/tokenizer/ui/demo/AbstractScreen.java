package org.tokenizer.ui.demo;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class AbstractScreen extends VerticalLayout {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(AbstractScreen.class);

    private final VerticalLayout content;

    public AbstractScreen() {
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
            LOG.debug("creating component instance...");
            final Component map = getChart();
            content.addComponent(map);
            content.setExpandRatio(map, 1);
        }
    }

}
