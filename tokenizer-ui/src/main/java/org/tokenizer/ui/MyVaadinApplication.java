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
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.ui.views.CrawledContentView;
import org.tokenizer.ui.views.TaskContainer;
import org.tokenizer.ui.views.TaskView;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
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
    private static ApplicationContext applicationContext = null;

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
        // repository = Configuration.get().getBeanLocatorAdapter()
        // .getBean("crawlerRepository", CrawlerHBaseRepository.class);
        // model = Configuration.get().getBeanLocatorAdapter()
        // .getBean("executorModel", WritableExecutorModel.class);
        applicationContext = MyVaadinServlet.getApplicationContext();
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");
        repository = (CrawlerRepository) applicationContext
                .getBean("crawlerRepository");
        buildMainLayout();
    }

    private void buildMainLayout() {
        window = new Window("Vertical Search");
        setMainWindow(window);
        setTheme("runo");
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(createToolbar());
        layout.addComponent(horizontalSplit);
        layout.setExpandRatio(horizontalSplit, 1);
        horizontalSplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);
        // horizontalSplit.setFirstComponent(tree);
        getMainWindow().setContent(layout);
        // setMainComponent(getListView());
    }

    // private Button newContact = new Button("Add contact");
    // private Button search = new Button("Search");
    private final Button tasks = new Button("Show Tasks");
    private final Button newTask = new Button("Add New Task");
    private final Button crawledContent = new Button("Crawled Content");

    public HorizontalLayout createToolbar() {
        HorizontalLayout lo = new HorizontalLayout();
        // lo.addComponent(newContact);
        // lo.addComponent(search);
        // search.addListener((Button.ClickListener) this);
        // newContact.addListener((Button.ClickListener) this);
        lo.addComponent(tasks);
        lo.addComponent(newTask);
        lo.addComponent(crawledContent);
        tasks.addListener((Button.ClickListener) this);
        newTask.addListener((Button.ClickListener) this);
        crawledContent.addListener((Button.ClickListener) this);
        return lo;
    }

    private synchronized void setMainComponent(final Component c) {
        horizontalSplit.setSecondComponent(c);
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
        // if (source == search) {
        // showSearchView();
        // } else if (source == newContact) {
        // addNewContact();
        // }
        if (source == tasks) {
            showTaskView();
        } else if (source == newTask) {
            addNewTask();
        } else if (source == crawledContent) {
            showCrawledContentView();
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == getTaskView().getTaskList()) {
            Item item = getTaskView().getTaskList().getItem(
                    getTaskView().getTaskList().getValue());
            getTaskView().getTaskForm().setItemDataSource(item);
        }
    }

    private void showCrawledContentView() {
        setMainComponent(getCrawledContentView());
        // removeComponent();
        // container.requestRepaint(); // uncomment this to solve problem !
        // requestRepaint();
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

    CrawledContentView crawledContentView = null;

    public CrawledContentView getCrawledContentView() {
        if (crawledContentView == null) {
            crawledContentView = new CrawledContentView(this);
        }
        return crawledContentView;
    }
}