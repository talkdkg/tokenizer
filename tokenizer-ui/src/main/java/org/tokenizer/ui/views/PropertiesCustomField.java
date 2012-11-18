package org.tokenizer.ui.views;

import java.util.Map;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

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
            public void buttonClick(ClickEvent event) {
                Item item = container.addItem("");
                item.getItemProperty("Key").setValue("");
                item.getItemProperty("Value").setValue("");
                table.setPageLength(container.size());
            }
        });
        layout.addComponent(newMoon);
        layout.setComponentAlignment(newMoon, Alignment.MIDDLE_RIGHT);
        setCompositionRoot(layout);
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
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
            throw new ConversionException("Invalid type");
        super.setPropertyDataSource(newDataSource);
    }

    @Override
    public Class<?> getType() {
        // TODO Auto-generated method stub
        return null;
    }
}