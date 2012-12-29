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
package org.tokenizer.ui;

import org.springframework.context.ApplicationContext;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.views.TaskContainer;
import org.tokenizer.ui.views.TaskView;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
public class MyVaadinApplication extends Application implements
        Button.ClickListener, Property.ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(MyVaadinApplication.class);
    private Window window;
    private final HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();
    VerticalLayout layout;
    private static ApplicationContext applicationContext = null;
    private String selectedHost = null;
    private int selectedHttpResponseCode = 200;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static CrawlerRepository repository;
    private static WritableExecutorModel model;

    public static CrawlerRepository getRepository() {
        return repository;
    }

    public static WritableExecutorModel getModel() {
        return model;
    }

    @Override
    public void init() {
        applicationContext = MyVaadinServlet.getApplicationContext();
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");
        repository = (CrawlerRepository) applicationContext
                .getBean("crawlerRepository");
        buildMainLayout();
    }

    private void buildMainLayout() {
        window = new Window("Vertical Search");
        window.setSizeFull();
        setMainWindow(window);
        setTheme("runo");
        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(createToolbar());
        getMainWindow().setContent(layout);
    }

    private final Button tasks = new Button("Show Tasks");
    private final Button newTask = new Button("Add New Task");

    public HorizontalLayout createToolbar() {
        HorizontalLayout lo = new HorizontalLayout();
        lo.addComponent(tasks);
        lo.addComponent(newTask);
        tasks.addListener((Button.ClickListener) this);
        newTask.addListener((Button.ClickListener) this);
        return lo;
    }

    Component current = null;

    private synchronized void setMainComponent(final Component c) {
        // horizontalSplit.setSecondComponent(c);
        if (current != null) {
            layout.removeComponent(current);
        }
        layout.addComponent(c);
        c.setSizeFull();
        layout.setExpandRatio(c, 1);
        current = c;
    }

    private TaskContainer taskContainer = null;

    public synchronized TaskContainer getTaskContainer() {
        if (taskContainer == null) {
            taskContainer = new TaskContainer(this);
        }
        return this.taskContainer;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Button source = event.getButton();
        if (source == tasks) {
            showTaskView();
        } else if (source == newTask) {
            addNewTask();
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == getTaskView().getTaskList()) {
            Object itemId = getTaskView().getTaskList().getValue();
            Item item = getTaskView().getTaskList().getItem(itemId);
            getTaskView().getTaskForm().setItemDataSource(item);
            TaskInfoBean taskInfoBean = (TaskInfoBean) itemId;
            TaskConfiguration taskConfiguration = taskInfoBean
                    .getTaskConfiguration();
            if (taskConfiguration instanceof ClassicRobotTaskConfiguration) {
                String host = ((ClassicRobotTaskConfiguration) taskConfiguration)
                        .getHost();
                setSelectedHost(host);
            } else if (taskConfiguration instanceof HtmlSplitterTaskConfiguration) {
                String host = ((ClassicRobotTaskConfiguration) taskConfiguration)
                        .getHost();
                setSelectedHost(host);
            } else if (taskConfiguration instanceof MessageParserTaskConfiguration) {
                String host = ((ClassicRobotTaskConfiguration) taskConfiguration)
                        .getHost();
                setSelectedHost(host);
            }
            getTaskView().getTaskOutputView().getUrlRecordList()
                    .getLazyQueryContainer().refresh();
        }
    }

    private void addNewTask() {
        getTaskView().getTaskForm().addTask();
        showTaskView();
    }

    private void showTaskView() {
        setMainComponent(getTaskView());
    }

    TaskView taskView;

    public TaskView getTaskView() {
        if (taskView == null) {
            taskView = new TaskView(this);
        }
        return taskView;
    }

    public String getSelectedHost() {
        return selectedHost;
    }

    public void setSelectedHost(final String selectedHost) {
        this.selectedHost = selectedHost;
        LOG.warn("selectedHost: {}", selectedHost);
    }

    public int getSelectedHttpResponseCode() {
        return selectedHttpResponseCode;
    }

    public void setSelectedHttpResponseCode(final int selectedHttpResponseCode) {
        this.selectedHttpResponseCode = selectedHttpResponseCode;
    }
}