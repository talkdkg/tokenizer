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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.ui.MyVaadinUI;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class TaskContainer extends IndexedContainer {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(TaskContainer.class);
    MyVaadinUI app;
    private static final String TASK_SUBMISSON_DATE = "Task Submission Date";

    public TaskContainer(final MyVaadinUI app) {
        super();
        this.app = app;
        ExecutorModelListener listener = new MyExecutorModelListener();
        Collection<TaskInfoBean> tasks = MyVaadinUI.getModel().getTasks(
                listener);
        addContainerProperty("uuid", UUID.class, null);
        addContainerProperty("name", String.class, null);
        addContainerProperty("type", String.class, null);
        addContainerProperty(TASK_SUBMISSON_DATE, Date.class, null);
        addContainerProperty("General State", TaskGeneralState.class, null);
        addContainerProperty("ZkDataVersion", Integer.class, 0);
        // addContainerProperty("taskConfiguration", TaskConfiguration.class,
        // null);
        synchronized (app) {
            for (TaskInfoBean task : tasks) {
                Item item = addItem(task.getUuid());
                item.getItemProperty("uuid").setValue(task.getUuid());
                item.getItemProperty("name").setValue(
                        task.getTaskConfiguration().getName());
                item.getItemProperty("type").setValue(
                        task.getTaskConfiguration().getImplementationName());
                item.getItemProperty(TASK_SUBMISSON_DATE).setValue(
                        task.getSubmitDate());
                item.getItemProperty("General State").setValue(
                        task.getTaskConfiguration().getGeneralState());
                item.getItemProperty("ZkDataVersion").setValue(
                        task.getZkDataVersion());
                for (Map.Entry<String, Long> entry : task.getCounters()
                        .entrySet()) {
                    addContainerProperty(entry.getKey(), Long.class, 0L);
                    item.getItemProperty(entry.getKey()).setValue(
                            entry.getValue());
                }
                // item.getItemProperty("taskConfiguration").setValue(
                // task.getTaskConfiguration());
                LOG.debug("BeanItem: {}", item);
            }
        }
    }

    private class MyExecutorModelListener implements ExecutorModelListener {

        @Override
        public void process(final ExecutorModelEvent event) {
            LOG.debug("Event::: {}", event.getType());
            synchronized (app) {
                UUID uuid = event.getUuid();
                TaskInfoBean newTask;
                boolean propertyAdded = false;
                try {
                    newTask = MyVaadinUI.getModel().getTask(uuid);
                } catch (TaskNotFoundException e) {
                    /*
                     * this will also handle
                     * ExecutorModelEventType.TASK_DEFINITION_REMOVED
                     */
                    removeItem(uuid);
                    return;
                }
                if (event.getType() == ExecutorModelEventType.TASK_ADDED) {
                    Item item = addItem(uuid);
                    item.getItemProperty("uuid").setValue(uuid);
                    item.getItemProperty("name").setValue(
                            newTask.getTaskConfiguration().getName());
                    item.getItemProperty("type").setValue(
                            newTask.getTaskConfiguration()
                                    .getImplementationName());
                    item.getItemProperty(TASK_SUBMISSON_DATE).setValue(
                            newTask.getSubmitDate());
                    item.getItemProperty("General State").setValue(
                            newTask.getTaskConfiguration().getGeneralState());
                    item.getItemProperty("ZkDataVersion").setValue(
                            newTask.getZkDataVersion());
                } else if (event.getType() == ExecutorModelEventType.TASK_UPDATED) {
                    getContainerProperty(uuid, "name").setValue(
                            newTask.getTaskConfiguration().getName());
                    getContainerProperty(uuid, "type").setValue(
                            newTask.getTaskConfiguration()
                                    .getImplementationName());
                    getContainerProperty(uuid, "General State").setValue(
                            newTask.getTaskConfiguration().getGeneralState());
                    getContainerProperty(uuid, "ZkDataVersion").setValue(
                            newTask.getZkDataVersion());
                    for (Map.Entry<String, Long> entry : newTask.getCounters()
                            .entrySet()) {
                        LOG.debug("Entry: {}", entry);
                        Collection propertyIds = getContainerPropertyIds();
                        if (propertyIds.contains(entry.getKey())) {
                            getContainerProperty(uuid, entry.getKey())
                                    .setValue(entry.getValue());
                        } else {
                            addContainerProperty(entry.getKey(), Long.class,
                                    entry.getValue());
                            propertyAdded = true;
                        }
                    }
                }
                if (propertyAdded) {
                    fireContainerPropertySetChange();
                }
            }
        }
    }
}
