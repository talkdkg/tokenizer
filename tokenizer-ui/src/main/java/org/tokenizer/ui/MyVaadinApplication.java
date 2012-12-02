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
import org.tokenizer.crawler.db.CrawlerHBaseRepository;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.ui.data.PersonContainer;
import org.tokenizer.ui.data.SearchFilter;
import org.tokenizer.ui.views.CrawledContentView;
import org.tokenizer.ui.views.TaskContainer;
import org.tokenizer.ui.views.TaskView;
import org.tokenizer.ui.widgets.HelpWindow;
import org.tokenizer.ui.widgets.ListView;
import org.tokenizer.ui.widgets.NavigationTree;
import org.tokenizer.ui.widgets.PersonForm;
import org.tokenizer.ui.widgets.PersonList;
import org.tokenizer.ui.widgets.SearchView;
import org.tokenizer.ui.widgets.SharingOptions;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * The Application's "main" class
 */
public class MyVaadinApplication extends Application implements
        Button.ClickListener, Property.ValueChangeListener, ItemClickListener {
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(MyVaadinApplication.class);
    private Window window;
    private final HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();
    private final NavigationTree tree = new NavigationTree(this);
    private ListView listView = null;
    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private static CrawlerHBaseRepository repository;
    private static WritableExecutorModel model;

    public static CrawlerHBaseRepository getRepository() {
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
        repository = (CrawlerHBaseRepository) applicationContext
                .getBean("crawlerRepository");
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");
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

    private synchronized void setMainComponent(Component c) {
        horizontalSplit.setSecondComponent(c);
    }

    private PersonList personList = null;
    private PersonForm personForm = null;

    private ListView getListView() {
        if (listView == null) {
            personList = new PersonList(this);
            personForm = new PersonForm(this);
            listView = new ListView(personList, personForm);
        }
        return listView;
    }

    private HelpWindow helpWindow = null;

    public HelpWindow getHelpWindow() {
        if (this.helpWindow == null) {
            this.helpWindow = new HelpWindow();
        }
        return this.helpWindow;
    }

    private SharingOptions sharingOptions = null;

    public SharingOptions getSharingOptions() {
        if (this.sharingOptions == null) {
            this.sharingOptions = new SharingOptions();
        }
        return this.sharingOptions;
    }

    private final PersonContainer dataSource = PersonContainer
            .createWithTestData();

    public PersonContainer getDataSource() {
        return dataSource;
    }

    private TaskContainer taskContainer = null;

    public synchronized TaskContainer getTaskContainer() {
        if (taskContainer == null) {
            taskContainer = new TaskContainer(this);
        }
        return this.taskContainer;
    }

    private SearchView searchView = null;

    private SearchView getSearchView() {
        if (searchView == null) {
            searchView = new SearchView(this);
        }
        return searchView;
    }

    @Override
    public void buttonClick(ClickEvent event) {
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
    public void valueChange(ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == personList) {
            Item item = personList.getItem(personList.getValue());
            if (item != personForm.getItemDataSource()) {
                personForm.setItemDataSource(item);
            }
        }
        if (property == getTaskView().getTaskList()) {
            Item item = getTaskView().getTaskList().getItem(
                    getTaskView().getTaskList().getValue());
            getTaskView().getTaskForm().setItemDataSource(item);
        }
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == tree) {
            Object itemId = event.getItemId();
            if (itemId != null) {
                if (NavigationTree.SHOW_ALL.equals(itemId)) {
                    // clear previous filters
                    getDataSource().removeAllContainerFilters();
                    showListView();
                } else if (NavigationTree.SEARCH.equals(itemId)) {
                    showSearchView();
                } else if (itemId instanceof SearchFilter) {
                    search((SearchFilter) itemId);
                }
            }
        }
    }

    private void showSearchView() {
        setMainComponent(getSearchView());
    }

    private void showListView() {
        setMainComponent(getListView());
    }

    private void showCrawledContentView() {
        setMainComponent(getCrawledContentView());
        // removeComponent();
        // container.requestRepaint(); // uncomment this to solve problem !
        // requestRepaint();
    }

    private void addNewContact() {
        showListView();
        personForm.addContact();
    }

    private void addNewTask() {
        getTaskView().getTaskForm().addTask();
        showTaskView();
    }

    public void search(SearchFilter searchFilter) {
        // clear previous filters
        getDataSource().removeAllContainerFilters();
        // filter contacts with given filter
        getDataSource().addContainerFilter(searchFilter.getPropertyId(),
                searchFilter.getTerm(), true, false);
        showListView();
        getMainWindow().showNotification(
                "Searched for " + searchFilter.getPropertyId() + "=*"
                        + searchFilter.getTerm() + "*, found "
                        + getDataSource().size() + " item(s).",
                Notification.TYPE_TRAY_NOTIFICATION);
        getMainWindow();
    }

    public void saveSearch(SearchFilter searchFilter) {
        tree.addItem(searchFilter);
        tree.setParent(searchFilter, NavigationTree.SEARCH);
        tree.setChildrenAllowed(searchFilter, false);
        tree.expandItem(NavigationTree.SEARCH);
        tree.setValue(searchFilter);
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