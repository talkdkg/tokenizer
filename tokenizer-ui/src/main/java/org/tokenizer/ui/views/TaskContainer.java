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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class TaskContainer extends IndexedContainer {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(TaskContainer.class);
    MyVaadinApplication app;

    public TaskContainer(MyVaadinApplication app) {
        super();
        this.app = app;
        ExecutorModelListener listener = new MyExecutorModelListener();
        Collection<TaskInfoBean> tasks = MyVaadinApplication.getModel()
                .getTasks(listener);
        addContainerProperty("name", String.class, "");
        addContainerProperty("type", String.class, "");
        // addContainerProperty("taskConfiguration.tld", String.class, "");
        addContainerProperty("General State", String.class, "");
        addContainerProperty("ZkDataVersion", Long.class, 0);
        synchronized (app) {
            for (TaskInfoBean task : tasks) {
                Item item = addItem(task.getTaskConfiguration().getName());
                item.getItemProperty("name").setValue(
                        task.getTaskConfiguration().getName());
                item.getItemProperty("type").setValue(
                        task.getTaskConfiguration().getClass().getSimpleName());
                // item.getItemProperty("taskConfiguration.tld").setValue(
                // task.getTaskConfiguration().getTld());
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
                LOG.debug("BeanItem: {}", item);
            }
            // fireContainerPropertySetChange();
        }
    }

    private class MyExecutorModelListener implements ExecutorModelListener {
        @Override
        public void process(final ExecutorModelEvent event) {
            LOG.debug("Event::: {}", event.getType());
            synchronized (app) {
                String taskName = event.getTaskDefinitionName();
                TaskInfoBean newTask;
                boolean propertyAdded = false;
                Collection propertyIds = getContainerPropertyIds();
                try {
                    newTask = MyVaadinApplication.getModel().getTask(taskName);
                } catch (TaskNotFoundException e) {
                    /*
                     * this will also handle
                     * ExecutorModelEventType.TASK_DEFINITION_REMOVED
                     */
                    removeItem(taskName);
                    return;
                }
                if (event.getType() == ExecutorModelEventType.TASK_ADDED) {
                    Item item = addItem(taskName);
                    item.getItemProperty("name").setValue(
                            newTask.getTaskConfiguration().getName());
                    item.getItemProperty("type").setValue(
                            newTask.getTaskConfiguration().getClass()
                                    .getSimpleName());
                    // item.getItemProperty("taskConfiguration.tld").setValue(
                    // newTask.getTaskConfiguration().getTld());
                    item.getItemProperty("General State").setValue(
                            newTask.getTaskConfiguration().getGeneralState());
                    item.getItemProperty("ZkDataVersion").setValue(
                            newTask.getZkDataVersion());
                } else if (event.getType() == ExecutorModelEventType.TASK_UPDATED) {
                    getContainerProperty(taskName, "name").setValue(
                            newTask.getTaskConfiguration().getName());
                    getContainerProperty(taskName, "type").setValue(
                            newTask.getClass().getSimpleName());
                    // getContainerProperty(taskName, "taskConfiguration.tld")
                    // .setValue(newTask.getTaskConfiguration().getTld());
                    getContainerProperty(taskName, "General State").setValue(
                            newTask.getTaskConfiguration().getGeneralState());
                    for (Map.Entry<String, Long> entry : newTask.getCounters()
                            .entrySet()) {
                        LOG.debug("Entry: {}", entry);
                        if (propertyIds.contains(entry.getKey())) {
                            getContainerProperty(taskName, entry.getKey())
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
