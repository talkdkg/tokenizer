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
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class ClassicRobotTaskConfigurationForm extends
        TaskConfigurationField<ClassicRobotTaskConfiguration> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private FieldGroup fieldGroup;

    @Override
    protected Component initContent() {
        FormLayout layout = new FormLayout();
        final Window window = new Window("Edit Task Configuration", layout);
        TextField name = new TextField("Name:");
        layout.addComponent(name);
        ComboBox state = new ComboBox("State");
        state.setNewItemsAllowed(false);
        state.setNullSelectionAllowed(false);
        state.addItem(TaskGeneralState.START_REQUESTED);
        state.addItem(TaskGeneralState.STOP_REQUESTED);
        state.addItem(TaskGeneralState.DELETE_REQUESTED);
        layout.addComponent(state);
        TextField host = new TextField("Host:");
        layout.addComponent(host);
        CheckBox followRedirects = new CheckBox("Follow redirects:");
        layout.addComponent(followRedirects);
        CheckBox followExternal = new CheckBox("Follow external outlinks:");
        layout.addComponent(followExternal);
        TextField agentName = new TextField("Robot name:");
        layout.addComponent(agentName);
        TextField emailAddress = new TextField("Robot Email:");
        layout.addComponent(emailAddress);
        TextField webAddress = new TextField("Robot description URL:");
        layout.addComponent(webAddress);
        fieldGroup = new BeanFieldGroup<ClassicRobotTaskConfiguration>(
                ClassicRobotTaskConfiguration.class);
        fieldGroup.bind(name, "name");
        fieldGroup.bind(state, "state");
        fieldGroup.bind(host, "host");
        fieldGroup.bind(followRedirects, "followRedirects");
        fieldGroup.bind(followExternal, "followExternal");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        Button button = new Button("Open configuration editor",
                new ClickListener() {

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        getUI().addWindow(window);
                    }
                });
        window.addCloseListener(new CloseListener() {

            @Override
            public void windowClose(final CloseEvent e) {
                try {
                    fieldGroup.commit();
                } catch (CommitException ex) {
                    ex.printStackTrace();
                }
            }
        });
        window.center();
        window.setWidth(null);
        layout.setWidth(null);
        layout.setMargin(true);
        return button;
    }

    @Override
    public Class getType() {
        return TaskConfiguration.class;
    }

    @Override
    protected void setInternalValue(
            final ClassicRobotTaskConfiguration classicRobotTaskConfiguration) {
        super.setInternalValue(classicRobotTaskConfiguration);
        fieldGroup
                .setItemDataSource(new BeanItem<ClassicRobotTaskConfiguration>(
                        classicRobotTaskConfiguration));
    }

    @Override
    public void setConverter(final Class<?> datamodelType) {
    }
}
