/*
 * Copyright 2013 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
