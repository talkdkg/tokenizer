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

import java.util.ArrayList;

import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;

public abstract class TaskConfigurationFormBase extends CustomField {
    private static final long serialVersionUID = 1L;
    private final Form taskConfigurationForm;
    private final TaskConfiguration taskConfiguration;

    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    public Form getTaskConfigurationForm() {
        return taskConfigurationForm;
    }

    public TaskConfigurationFormBase(TaskConfiguration taskConfiguration) {
        super();
        this.taskConfiguration = taskConfiguration;
        taskConfigurationForm = new Form();
        taskConfigurationForm.setCaption("Task Configuration for "
                + getType().getSimpleName());
        taskConfigurationForm.setWriteThrough(false);
        taskConfigurationForm.setFormFieldFactory(getFormFieldFactory());
        setCompositionRoot(taskConfigurationForm);
        BeanItem<TaskConfiguration> beanItem = new BeanItem<TaskConfiguration>(
                taskConfiguration);
        // enforce ordering of form fields:
        ArrayList<Object> itemPropertyIds = new ArrayList<Object>();
        itemPropertyIds.add("name");
        itemPropertyIds.add("generalState");
        for (Object o : beanItem.getItemPropertyIds()) {
            if (!"name".equals(o) && !"generalState".equals(o)) {
                itemPropertyIds.add(o);
            }
        }
        taskConfigurationForm.setItemDataSource(beanItem, itemPropertyIds);
    }

    protected abstract FormFieldFactory getFormFieldFactory();

    /**
     * commit changes
     */
    @Override
    public void commit() throws Buffered.SourceException, InvalidValueException {
        super.commit();
        taskConfigurationForm.commit();
    }

    /**
     * discard changes
     */
    @Override
    public void discard() throws Buffered.SourceException {
        super.discard();
        taskConfigurationForm.discard();
    }
}
