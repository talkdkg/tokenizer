package org.tokenizer.ui.demo;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;


public abstract class AbstractScreen extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(AbstractScreen.class);

    protected static final int COMMON_FIELD_WIDTH = 48;

    private final VerticalLayout content;

    public AbstractScreen() {
        content = this;
        content.setSizeFull();
    }

    protected abstract Component get();

    @Override
    public void attach() {
        super.attach();
        setup();
    }

    protected void setup() {
        if (content.getComponentCount() == 0) {
            LOG.debug("creating component instance...");
            final Component map = get();
            content.addComponent(map);
            content.setExpandRatio(map, 1);
        }
    }

}
