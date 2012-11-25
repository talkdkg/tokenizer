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

import org.apache.zookeeper.KeeperException;
import org.lilyproject.util.zookeeper.ZkLockException;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.MyVaadinApplication;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;

public class TaskForm extends Form implements ClickListener {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskForm.class);
    private final MyVaadinApplication app;
    private final Button save = new Button("Save", (ClickListener) this);
    private final Button cancel = new Button("Cancel", (ClickListener) this);
    private final Button edit = new Button("Edit", (ClickListener) this);
    private TaskInfoBean newTask;
    private boolean newTaskMode = false;
    private final TaskConfigurationComponent taskConfigurationComponent;

    @SuppressWarnings("serial")
    public TaskForm(MyVaadinApplication app) {
        this.app = app;
        setWriteThrough(false);
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(save);
        footer.addComponent(cancel);
        footer.addComponent(edit);
        footer.setVisible(false);
        setFooter(footer);
        // setFormFieldFactory(new DefaultFieldFactory() {
        // @Override
        // public Field createField(Item item, Object propertyId,
        // Component uiContext) {
        // Field field = null;
        // if (propertyId.equals("name")) {
        // field = super.createField(item, propertyId, uiContext);
        // } else if (propertyId.equals("generalState")) {
        // field = state;
        // } else {
        // field = super.createField(item, propertyId, uiContext);
        // }
        // LOG.trace("createField: {}", propertyId);
        // return field;
        // }
        // });
        taskConfigurationComponent = new TaskConfigurationComponent();
        getLayout().addComponent(taskConfigurationComponent);
        setReadOnly(true);
    }

    public void addTask() {
        newTask = new TaskInfoBean();
        BeanItem<TaskInfoBean> item = new BeanItem(newTask,
                new String[] { "name" });
        setItemDataSource(item);
        newTaskMode = true;
        setReadOnly(false);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Button source = event.getButton();
        if (source == save) {
            if (!isValid())
                return;
            commit();
            if (newTaskMode) {
                CustomField cf = taskConfigurationComponent
                        .getConfigurationField();
                cf.commit();
                TaskConfiguration taskConfiguration = (TaskConfiguration) cf
                        .getValue();
                LOG.info("taskConfiguration: {}", taskConfiguration);
                newTask.setTaskConfiguration(taskConfiguration);
                if (cf instanceof ClassicRobotTaskConfigurationForm) {
                }
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
                TaskInfoBean mutableTask = update(task);
                Item item2 = app.getTaskView().getTaskList()
                        .getItem(app.getTaskView().getTaskList().getValue());
                setItemDataSource(item2);
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
                BeanItem<TaskInfoBean> item = new BeanItem(task2, new String[] {
                        "name", "generalState" });
                setItemDataSource(item);
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
        LOG.info("" + getItemPropertyIds());
        if (!readOnly && !newTaskMode) {
            getField("name").setReadOnly(true);
        }
        taskConfigurationComponent.setVisible(!readOnly);
        // if (readOnly)
        // getLayout().removeComponent(taskConfigurationComponent);
        // else getLayout().addComponent(taskConfigurationComponent);
        save.setVisible(!readOnly);
        cancel.setVisible(!readOnly);
        edit.setVisible(readOnly);
    }

    private TaskInfoBean update(TaskInfoBean task) {
        String taskName = task.getTaskConfiguration().getName();
        String lock = lockTask(taskName);
        if (lock == null)
            return null;
        TaskInfoBean mutableTask = getMutableTask(taskName);
        if (mutableTask == null)
            return null;
        boolean changes = false;
        if (task.getTaskConfiguration() != null
                && !task.getTaskConfiguration().equals(
                        mutableTask.getTaskConfiguration())) {
            mutableTask.setTaskConfiguration(task.getTaskConfiguration());
            changes = true;
        }
        if (task.getTaskConfiguration().getGeneralState() != null
                && task.getTaskConfiguration().getGeneralState() != mutableTask
                        .getTaskConfiguration().getGeneralState()) {
            mutableTask.getTaskConfiguration().setGeneralState(
                    task.getTaskConfiguration().getGeneralState());
            changes = true;
        }
        if (changes) {
            updateTask(mutableTask, lock);
            LOG.debug("Task definition updated: " + taskName);
        } else {
            LOG.debug("Task already matches the specified settings, did not update it.");
        }
        unlockTask(lock, mutableTask);
        return mutableTask;
    }

    public static String lockTask(String taskName) {
        String lock = null;
        try {
            lock = MyVaadinApplication.getModel().lockTask(taskName);
        } catch (ZkLockException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.warn("Task not found: {}", taskName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskModelException e) {
            LOG.error("", e);
        }
        return lock;
    }

    public static void unlockTask(String lock, TaskInfoBean mutableTask) {
        boolean ignoreMissing = mutableTask.getTaskConfiguration()
                .getGeneralState() != null
                && mutableTask.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED;
        try {
            MyVaadinApplication.getModel().unlockTask(lock, ignoreMissing);
        } catch (ZkLockException e) {
            LOG.error("", e);
        }
    }

    public static TaskInfoBean getMutableTask(String taskName) {
        TaskInfoBean mutableTask = null;
        try {
            mutableTask = MyVaadinApplication.getModel().getMutableTask(
                    taskName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.error("", e);
        }
        return mutableTask;
    }

    public static void updateTask(TaskInfoBean mutableTask, String lock) {
        try {
            MyVaadinApplication.getModel().updateTask(mutableTask, lock);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.warn("Task not found: {}", mutableTask);
        } catch (TaskConcurrentModificationException e) {
            LOG.error("", e);
        } catch (ZkLockException e) {
            LOG.error("", e);
        } catch (TaskUpdateException e) {
            LOG.error("", e);
        } catch (TaskValidityException e) {
            LOG.error("", e);
        }
    }
}
