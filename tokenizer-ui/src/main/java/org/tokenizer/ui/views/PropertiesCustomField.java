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

import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

//import org.vaadin.addon.customfield.CustomField;
public class PropertiesCustomField extends CustomField {

    private static final long serialVersionUID = 1L;
    Table table;
    VerticalLayout layout;
    IndexedContainer container;

    public PropertiesCustomField() {
        table = new Table();
        table.setEditable(true);
        layout = new VerticalLayout();
        container = new IndexedContainer();
        container.addContainerProperty("Key", String.class, "");
        container.addContainerProperty("Value", String.class, "");
        table.setContainerDataSource(container);
        layout.addComponent(table);
        Button newMoon = new Button("New Property");
        newMoon.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                Item item = container.addItem("");
                item.getItemProperty("Key").setValue("");
                item.getItemProperty("Value").setValue("");
                table.setPageLength(container.size());
            }
        });
        layout.addComponent(newMoon);
        layout.setComponentAlignment(newMoon, Alignment.MIDDLE_RIGHT);
        // setCompositionRoot(layout);
    }

    @Override
    public void setPropertyDataSource(final Property newDataSource) {
        Object value = newDataSource.getValue();
        if (value instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) value;
            container.removeAllItems();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Item item = container.addItem(entry.getKey());
                item.getItemProperty("Key").setValue(entry.getKey());
                item.getItemProperty("Value").setValue(entry.getValue());
            }
            table.setPageLength(map.size());
        } else
            throw new RuntimeException("Invalid type");
        super.setPropertyDataSource(newDataSource);
    }

    @Override
    public Class<?> getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBuffered(final boolean buffered) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isBuffered() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeAllValidators() {
        // TODO Auto-generated method stub
    }

    @Override
    public void addValueChangeListener(final ValueChangeListener listener) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeValueChangeListener(final ValueChangeListener listener) {
        // TODO Auto-generated method stub
    }

    @Override
    protected Component initContent() {
        // TODO Auto-generated method stub
        return null;
    }
}
