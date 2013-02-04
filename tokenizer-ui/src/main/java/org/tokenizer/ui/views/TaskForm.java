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

import java.util.UUID;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.MyVaadinUI;
import org.tokenizer.util.zookeeper.ZkLockException;

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
    private final MyVaadinUI app;
    private final Button save = new Button("Save", this);
    private final Button cancel = new Button("Cancel", this);
    private final Button edit = new Button("Edit", this);
    private boolean newTaskMode = false;
    private TaskConfigurationComponent taskConfigurationComponent = null;
    TaskConfigurationField taskConfigurationField = null;

    public TaskForm(final MyVaadinUI app) {
        setCaption("Task Info");
        this.app = app;
        // setWriteThrough(false);
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(save);
        footer.addComponent(cancel);
        footer.addComponent(edit);
        footer.setVisible(false);
        setFooter(footer);
        setReadOnly(true);
        /*
         * setFormFieldFactory(new DefaultFieldFactory() {
         * 
         * @Override public Field createField(final Item item, final Object
         * propertyId, final Component uiContext) { Field field = null; if
         * ("taskConfiguration".equals(propertyId)) { // TaskConfiguration
         * taskConfiguration = (TaskConfiguration) // item //
         * .getItemProperty(propertyId).getValue(); UUID uuid = (UUID)
         * item.getItemProperty("uuid").getValue(); TaskInfoBean taskInfoBean =
         * null; try { taskInfoBean = MyVaadinUI.getModel().getMutableTask(
         * uuid); } catch (InterruptedException e) { // TODO Auto-generated
         * catch block e.printStackTrace(); } catch (KeeperException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } catch
         * (TaskNotFoundException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); } // if (taskInfoBean == null) return null;
         * TaskConfiguration taskConfiguration = taskInfoBean
         * .getTaskConfiguration(); LOG.debug(taskConfiguration.toString()); if
         * (taskConfiguration instanceof SitemapsFetcherTaskConfiguration) {
         * field = new SitemapsFetcherTaskConfigurationForm(); } else if
         * (taskConfiguration instanceof HtmlSplitterTaskConfiguration) { field
         * = new HtmlSplitterTaskConfigurationForm(); } else if
         * (taskConfiguration instanceof MessageParserTaskConfiguration) { field
         * = new MessageParserTaskConfigurationForm(); } else if
         * (taskConfiguration instanceof TweetCollectorTaskConfiguration) {
         * field = new TweetCollectorTaskConfigurationForm(); } else if
         * (taskConfiguration instanceof ClassicRobotTaskConfiguration) { field
         * = new ClassicRobotTaskConfigurationForm(); } else if
         * (taskConfiguration instanceof RssFetcherTaskConfiguration) { field =
         * new RssFetcherTaskConfigurationForm(); } else if (taskConfiguration
         * instanceof SimpleMultithreadedFetcherTaskConfiguration) { field = new
         * SimpleMultithreadedFetcherTaskConfigurationForm(); } // field = new
         * BeanSetFieldWrapper<Person>(select, // Person.class,
         * getPersonContainer(), "lastName"); //
         * field.setCaption(createCaptionByPropertyId(propertyId)); // field =
         * taskConfigurationField; } else if ("counters".equals(propertyId)) { }
         * else { field = super.createField(item, propertyId, uiContext); if
         * (field instanceof TextField) { // show null as an empty string
         * ((TextField) field).setNullRepresentation(""); } } return field; }
         * });
         */
    }

    public void addTask() {
        UUID uuid = UUID.randomUUID();
        TaskInfoBean newTask = new TaskInfoBean(uuid);
        String[] propertyIds = {};
        BeanItem<TaskInfoBean> item = new BeanItem(newTask, propertyIds);
        setItemDataSource(item);
        newTaskMode = true;
        setReadOnly(false);
        taskConfigurationComponent = new TaskConfigurationComponent();
        getLayout().addComponent(taskConfigurationComponent);
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        Button source = event.getButton();
        if (source == save) {
            if (!isValid())
                return;
            commit();
            if (newTaskMode) {
                try {
                    TaskConfigurationField subform = taskConfigurationComponent
                            .getTaskConfigurationField();
                    subform.commit();
                    TaskConfiguration taskConfiguration = (TaskConfiguration) subform
                            .getConvertedValue();
                    LOG.info("taskConfiguration: {}", taskConfiguration);
                    UUID uuid = UUID.randomUUID();
                    TaskInfoBean taskInfoBean = new TaskInfoBean(uuid);
                    taskInfoBean.setTaskConfiguration(taskConfiguration);
                    MyVaadinUI.getModel().addTask(taskInfoBean);
                } catch (TaskExistsException e) {
                    LOG.error("", e);
                } catch (TaskModelException e) {
                    LOG.error("", e);
                } catch (TaskValidityException e) {
                    LOG.error("", e);
                }
                newTaskMode = false;
            } else {
                BeanItem<TaskInfoBean> item = (BeanItem<TaskInfoBean>) getItemDataSource();
                TaskInfoBean task = item.getBean();
                taskConfigurationField.commit();
                LOG.debug("task configuration updated: {}",
                        taskConfigurationField.getConvertedValue());
                task.setTaskConfiguration((TaskConfiguration) taskConfigurationField
                        .getConvertedValue());
                update(task);
            }
            setReadOnly(true);
        } else if (source == cancel) {
            if (newTaskMode) {
                newTaskMode = false;
                // setItemDataSource(null);
            } else {
                discard();
            }
            setReadOnly(true);
        } else if (source == edit) {
            // setItemDataSource(getItemDataSource(),
            // Arrays.asList(new String[] { "taskConfiguration" }));
            setReadOnly(false);
        }
    }

    @Override
    public void setItemDataSource(final Item newDataSource) {
        // super.setItemDataSource(newDataSource,
        // Arrays.asList(new String[] { "taskConfiguration" }));
        super.setItemDataSource(newDataSource);
        newTaskMode = false;
        setReadOnly(true);
        if (newDataSource == null) {
            getFooter().setVisible(false);
        } else {
            getFooter().setVisible(true);
        }
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        super.setReadOnly(readOnly);
        save.setVisible(!readOnly);
        cancel.setVisible(!readOnly);
        edit.setVisible(readOnly);
        if (taskConfigurationComponent != null) {
            getLayout().removeComponent(taskConfigurationComponent);
        }
        if (taskConfigurationField != null) {
            getLayout().removeComponent(taskConfigurationField);
        }
    }

    private TaskInfoBean update(final TaskInfoBean task) {
        UUID uuid = task.getUuid();
        String lock = lockTask(uuid);
        if (lock == null)
            return null;
        TaskInfoBean mutableTask = getMutableTask(uuid);
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
            LOG.debug("Task definition updated: {}", uuid);
        } else {
            LOG.debug("Task already matches the specified settings, did not update it.");
        }
        unlockTask(lock, mutableTask);
        return mutableTask;
    }

    public static String lockTask(final UUID uuid) {
        String lock = null;
        try {
            lock = MyVaadinUI.getModel().lockTask(uuid);
        } catch (ZkLockException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.warn("Task not found: {}", uuid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskModelException e) {
            LOG.error("", e);
        }
        return lock;
    }

    public static void unlockTask(final String lock,
            final TaskInfoBean mutableTask) {
        boolean ignoreMissing = mutableTask.getTaskConfiguration()
                .getGeneralState() != null
                && mutableTask.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED;
        try {
            MyVaadinUI.getModel().unlockTask(lock, ignoreMissing);
        } catch (ZkLockException e) {
            LOG.error("", e);
        }
    }

    public static TaskInfoBean getMutableTask(final UUID uuid) {
        TaskInfoBean mutableTask = null;
        try {
            mutableTask = MyVaadinUI.getModel().getMutableTask(uuid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (KeeperException e) {
            LOG.error("", e);
        } catch (TaskNotFoundException e) {
            LOG.error("", e);
        }
        return mutableTask;
    }

    public static void updateTask(final TaskInfoBean mutableTask,
            final String lock) {
        try {
            MyVaadinUI.getModel().updateTask(mutableTask, lock);
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
