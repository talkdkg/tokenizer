/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.ui.v7.view;

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
