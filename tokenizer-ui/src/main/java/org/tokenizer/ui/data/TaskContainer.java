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
package org.tokenizer.ui.data;

import java.io.Serializable;
import java.util.Collection;

import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.impl.TaskInfoBean;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;

@SuppressWarnings("serial")
public class TaskContainer extends BeanContainer<String, TaskVO> implements
        Serializable, Property.ValueChangeListener {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskContainer.class);

    MyVaadinApplication app;

    public TaskContainer(MyVaadinApplication app)
            throws InstantiationException, IllegalAccessException {
        super(TaskVO.class);

        this.app = app;

        ExecutorModelListener listener = new MyExecutorModelListener();
        Collection<TaskInfoBean> taskDefinitions = MyVaadinApplication
                .getModel().getTaskDefinitions(listener);

        synchronized (app) {
            for (TaskInfoBean taskDefinition : taskDefinitions) {
                TaskVO taskVO = new TaskVO(taskDefinition);
                addItem(taskVO.getTaskName(), taskVO);
            }
        }
    }

    private class MyExecutorModelListener implements ExecutorModelListener {
        public void process(final ExecutorModelEvent event) {

            LOG.info("Event::: {}", event.getType());

            synchronized (app) {

                String taskName = event.getTaskDefinitionName();
                TaskInfoBean taskDefinition;
                try {
                    taskDefinition = MyVaadinApplication.getModel()
                            .getTaskDefinition(taskName);
                } catch (TaskNotFoundException e) {
                    /*
                     * this will also handle
                     * ExecutorModelEventType.TASK_DEFINITION_REMOVED
                     */
                    removeItem(taskName);
                    return;
                }

                if (event.getType() == ExecutorModelEventType.TASK_DEFINITION_ADDED) {
                    TaskVO taskVO = new TaskVO(taskDefinition);
                    addItem(taskVO.getTaskName(), taskVO);
                } else if (event.getType() == ExecutorModelEventType.TASK_DEFINITION_UPDATED) {

                    TaskVO newTaskVO = new TaskVO(taskDefinition);

                    BeanItem<TaskVO> beanItem = getItem(taskName);

                    TaskVO oldTaskVO = beanItem.getBean();

                    if (oldTaskVO.equals(newTaskVO))
                        return;

                    beanItem.getItemProperty("submitTime").setValue(
                            newTaskVO.getSubmitTime());
                    beanItem.getItemProperty("taskGeneralState").setValue(
                            newTaskVO.getTaskGeneralState());
                    beanItem.getItemProperty("taskBatchBuildState").setValue(
                            newTaskVO.getTaskBatchBuildState());
                    beanItem.getItemProperty("robotsRestrictedUrlCount")
                            .setValue(newTaskVO.getRobotsRestrictedUrlCount());
                    beanItem.getItemProperty("totalProcessedUrlCount")
                            .setValue(newTaskVO.getTotalProcessedUrlCount());
                    beanItem.getItemProperty("fetchedSuccessfullyUrlCount")
                            .setValue(
                                    newTaskVO.getFetchedSuccessfullyUrlCount());
                    beanItem.getItemProperty("fetchErrorsCount").setValue(
                            newTaskVO.getFetchErrorsCount());
                    beanItem.getItemProperty("otherErrorsCount").setValue(
                            newTaskVO.getOtherErrorsCount());
                    beanItem.getItemProperty("injectedUrlCount").setValue(
                            newTaskVO.getInjectedUrlCount());
                    beanItem.getItemProperty("totalSuccessfulMeanTime")
                            .setValue(newTaskVO.getTotalSuccessfulMeanTime());
                    beanItem.getItemProperty("averageSuccessfulMeanTime")
                            .setValue(newTaskVO.getAverageSuccessfulMeanTime());

                }

            }

        }
    }

}
