/*
 * Copyright 2007-2012 Tokenizer Inc.
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
package org.tokenizer.ui.views;

import org.tokenizer.executor.model.api.TaskGeneralState;

import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class DefaultTaskConfigurationFieldFactory extends DefaultFieldFactory {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(DefaultTaskConfigurationFieldFactory.class);
    private final ComboBox state;

    public DefaultTaskConfigurationFieldFactory() {
        super();
        state = new ComboBox("State");
        state.setNewItemsAllowed(false);
        state.setNullSelectionAllowed(false);
        state.addItem(TaskGeneralState.START_REQUESTED);
        state.addItem(TaskGeneralState.STOP_REQUESTED);
        state.addItem(TaskGeneralState.DELETE_REQUESTED);
    }

    @Override
    public Field createField(final Item item, final Object propertyId,
            final Component uiContext) {
        Field field = null;
        if (propertyId.equals("name")) {
            field = super.createField(item, propertyId, uiContext);
        } else if (propertyId.equals("generalState")) {
            field = state;
        } else if (propertyId.equals("xpath")) {
            TextField textField = new TextField();
            textField.setCaption("XPath");
            textField.setWidth(400, Field.UNITS_PIXELS);
            textField.setMaxLength(256);
            field = textField;
        } else {
            field = super.createField(item, propertyId, uiContext);
        }
        LOG.trace("createField: {}", propertyId);
        return field;
    }
}
