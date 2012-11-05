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
package org.tokenizer.ui.widgets;

import org.apache.commons.cli.CommandLine;
import org.apache.zookeeper.KeeperException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;

public class TaskForm extends Form implements ClickListener {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskForm.class);

    private Button save = new Button("Save", (ClickListener) this);
    private Button cancel = new Button("Cancel", (ClickListener) this);
    private Button edit = new Button("Edit", (ClickListener) this);

    private TaskInfoBean newTask;

    private boolean newTaskMode = false;

    private final ComboBox type = new ComboBox("Type");

    private final ComboBox state = new ComboBox("State");

    @SuppressWarnings("serial")
    public TaskForm() {

        setWriteThrough(false);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(save);
        footer.addComponent(cancel);
        footer.addComponent(edit);
        footer.setVisible(false);

        setFooter(footer);

        type.setNewItemsAllowed(false);
        type.setNullSelectionAllowed(false);

        type.addItem("");

        type.addItem("ClassicRobotTask");
        type.addItem("RssFetcherTask");
        type.addItem("SimpleMultithreadedFetcher");
        type.addItem("SitemapsTask");

        state.setNewItemsAllowed(false);
        state.setNullSelectionAllowed(false);

        state.addItem(TaskGeneralState.START_REQUESTED);
        state.addItem(TaskGeneralState.STOP_REQUESTED);
        state.addItem(TaskGeneralState.DELETE_REQUESTED);

        /*
         * Field factory for overriding how the component for city selection is
         * created
         */
        setFormFieldFactory(new DefaultFieldFactory() {

            @Override
            public Field createField(Item item, Object propertyId,
                    Component uiContext) {

                Field field = null;
                if (propertyId.equals("taskConfiguration.type")) {
                    field = type;
                } else if (propertyId.equals("generalState")) {
                    field = state;
                } else if ("configuration.properties".equals(propertyId)) {
                    // field = new PropertiesCustomField();
                    // field.setPropertyDataSource(item
                    // .getItemProperty("properties"));
                    // field.setCaption("Properties");
                } else if ("configuration.seeds".equals(propertyId)) {
                } else if ("name".equals(propertyId)) {
                    field = super.createField(item, propertyId, uiContext);
                } else if ("taskConfiguration.tld".equals(propertyId)) {
                    field = super.createField(item, propertyId, uiContext);
                } else if ("configuration.seeds".equals(propertyId)) {
                } else {
                    field = super.createField(item, propertyId, uiContext);
                }

                LOG.trace("createField: {}", propertyId);

                return field;

            }

        });

    }

    public void addTask() {
        newTask = new TaskInfoBean();
        
        BeanItem<TaskInfoBean> item = new BeanItem(newTask,
                new String[] { });

        item.addItemProperty("taskConfiguration.name",
                new NestedMethodProperty(newTask,
                        "taskConfiguration.name"));

        item.addItemProperty(
                "taskConfiguration.tld",
                new NestedMethodProperty(newTask, "taskConfiguration.tld"));

        item.addItemProperty("taskConfiguration.type",
                new NestedMethodProperty(newTask,
                        "taskConfiguration.type"));

        
        setItemDataSource(item);
        newTaskMode = true;
        setReadOnly(false);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Button source = event.getButton();

        if (source == save) {
            if (!isValid()) {
                return;
            }

            commit();
            if (newTaskMode) {

                try {
                    MyVaadinApplication.getModel().addTask(newTask);
                } catch (TaskExistsException e) {
                    LOG.error("", e);
                } catch (TaskModelException e) {
                    LOG.error("", e);
                } catch (TaskValidityException e) {
                    LOG.error("", e);
                }

                // setItemDataSource(newTaskConfiguration);

                newTaskMode = false;
            } else {

                BeanItem<TaskInfoBean> item = (BeanItem<TaskInfoBean>) getItemDataSource();

                TaskInfoBean task = item.getBean();
                try {
                    update(task);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            setReadOnly(true);
        } else if (source == cancel) {
            if (newTaskMode) {
                newTaskMode = false;
                setItemDataSource(null);
            } else {
                discard();
            }
            setReadOnly(true);
        } else if (source == edit) {

            TaskInfoBean task2;
            try {
                task2 = MyVaadinApplication.getModel().getMutableTask(
                        (String) getItemProperty("name").getValue());

                BeanItem<TaskInfoBean> item = new BeanItem(task2,
                        new String[] { "name", "generalState" });

                item.addItemProperty(
                        "taskConfiguration.tld",
                        new NestedMethodProperty(task2, "taskConfiguration.tld"));

                item.addItemProperty("taskConfiguration.type",
                        new NestedMethodProperty(task2,
                                "taskConfiguration.type"));

                super.setItemDataSource(item);

                setReadOnly(false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (KeeperException e) {
                LOG.error("", e);
            } catch (TaskNotFoundException e) {
                LOG.error("", e);
            }

        }
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        newTaskMode = false;
        if (newDataSource != null) {
            // super.setItemDataSource(newDataSource, orderedProperties);
            super.setItemDataSource(newDataSource);

            setReadOnly(true);
            getFooter().setVisible(true);
        } else {
            super.setItemDataSource(null);
            getFooter().setVisible(false);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {

        super.setReadOnly(readOnly);

        if (!readOnly && !newTaskMode) {
            getField("name").setReadOnly(true);
            getField("taskConfiguration.tld").setReadOnly(true);
            getField("taskConfiguration.type").setReadOnly(true);
        }

        save.setVisible(!readOnly);
        cancel.setVisible(!readOnly);
        edit.setVisible(readOnly);
    }

    public void update(TaskInfoBean task) throws Exception {

        String taskName = task.getTaskConfiguration().getName();
        if (!MyVaadinApplication.getModel().hasTask(taskName)) {
            LOG.info("Task does not exist: {}", taskName);
            return;
        }

        String lock = MyVaadinApplication.getModel().lockTask(taskName);
        try {
            TaskInfoBean freshTask = MyVaadinApplication.getModel()
                    .getMutableTask(taskName);

            boolean changes = false;

            if (task.getTaskConfiguration() != null
                    && !task.getTaskConfiguration().equals(
                            freshTask.getTaskConfiguration())) {
                freshTask.setTaskConfiguration(task.getTaskConfiguration());
                changes = true;
            }

            if (task.getGeneralState() != null
                    && task.getGeneralState() != freshTask.getGeneralState()) {
                freshTask.setGeneralState(task.getGeneralState());
                changes = true;
            }

            if (changes) {
                MyVaadinApplication.getModel().updateTask(freshTask, lock);
                LOG.debug("Task definition updated: " + taskName);
            } else {
                LOG.debug("Task already matches the specified settings, did not update it.");
            }

        } finally {
            // In case we requested deletion of a task, it might be that the
            // lock is
            // already removed by the time we get here as part of the task
            // deletion.
            boolean ignoreMissing = task.getGeneralState() != null
                    && task.getGeneralState() == TaskGeneralState.DELETE_REQUESTED;
            MyVaadinApplication.getModel().unlockTask(lock, ignoreMissing);
        }
    }

}
