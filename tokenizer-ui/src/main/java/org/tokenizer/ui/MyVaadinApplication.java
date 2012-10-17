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
import org.tokenizer.ui.widgets.HelpWindow;
import org.tokenizer.ui.widgets.ListView;
import org.tokenizer.ui.widgets.NavigationTree;
import org.tokenizer.ui.widgets.PersonForm;
import org.tokenizer.ui.widgets.PersonListTable;
import org.tokenizer.ui.widgets.SearchView;
import org.tokenizer.ui.widgets.SharingOptions;
import org.tokenizer.ui.widgets.TaskForm;
import org.tokenizer.ui.widgets.TaskList;
import org.tokenizer.ui.widgets.TaskView;

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
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application implements
        Button.ClickListener, Property.ValueChangeListener, ItemClickListener {

    private Window window;

    private HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();

    private NavigationTree tree = new NavigationTree(this);
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

        applicationContext = MyVaadinServlet.getApplicationContext();

        repository = (CrawlerHBaseRepository) applicationContext
                .getBean("crawlerRepository");
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");

        buildMainLayout();
    }

    private void buildMainLayout() {

        window = new Window("Address Book Demo application");

        setMainWindow(window);

        setTheme("runo");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        layout.addComponent(createToolbar());
        layout.addComponent(horizontalSplit);

        /* Allocate all available extra space to the horizontal split panel */

        layout.setExpandRatio(horizontalSplit, 1);
        /*
         * Set the initial split position so we can have a 200 pixel menu to the
         * left
         */

        horizontalSplit.setSplitPosition(200, Sizeable.UNITS_PIXELS);

        horizontalSplit.setFirstComponent(tree);

        getMainWindow().setContent(layout);
        setMainComponent(getListView());

        // getMainWindow().addWindow(getHelpWindow());

        // push = new ICEPush();
        // layout.addComponent(push);

    }

    private Button newContact = new Button("Add contact");
    private Button search = new Button("Search");

    private Button tasks = new Button("Show Tasks");

    private Button newTask = new Button("Add New Task");

    public HorizontalLayout createToolbar() {

        HorizontalLayout lo = new HorizontalLayout();
        lo.addComponent(newContact);
        lo.addComponent(search);
        lo.addComponent(tasks);
        lo.addComponent(newTask);

        search.addListener((Button.ClickListener) this);

        newContact.addListener((Button.ClickListener) this);
        tasks.addListener((Button.ClickListener) this);
        newTask.addListener((Button.ClickListener) this);

        return lo;

    }

    private void setMainComponent(Component c) {
        horizontalSplit.setSecondComponent(c);
    }

    private PersonListTable personList = null;
    private PersonForm personForm = null;

    private ListView getListView() {
        if (listView == null) {
            personList = new PersonListTable(this);
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

    private PersonContainer dataSource = PersonContainer.createWithTestData();

    public PersonContainer getDataSource() {
        return dataSource;
    }

    private SearchView searchView = null;

    private SearchView getSearchView() {
        if (searchView == null) {
            searchView = new SearchView(this);
        }
        return searchView;
    }

    public void buttonClick(ClickEvent event) {
        final Button source = event.getButton();
        if (source == search) {
            showSearchView();
        } else if (source == newContact) {
            addNewContact();
        } else if (source == tasks) {
            showTaskView();
        } else if (source == newTask) {
            addNewTask();
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
    }

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

    private void addNewContact() {
        showListView();
        personForm.addContact();
    }

    private void addNewTask() {
        showTaskView();
        taskForm.addTask();
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

    }

    public void saveSearch(SearchFilter searchFilter) {
        tree.addItem(searchFilter);
        tree.setParent(searchFilter, NavigationTree.SEARCH);
        // mark the saved search as a leaf (cannot have children)
        tree.setChildrenAllowed(searchFilter, false);
        // make sure "Search" is expanded
        tree.expandItem(NavigationTree.SEARCH);
        // select the saved search
        tree.setValue(searchFilter);
    }

    private void showTaskView() {
        setMainComponent(getTaskView());
    }

    TaskView taskView;
    TaskList taskList;
    TaskForm taskForm;

    private TaskView getTaskView() {
        if (taskView == null) {
            taskList = new TaskList(this);
            taskForm = new TaskForm();
            taskView = new TaskView(taskList, taskForm);
        }
        return taskView;
    }

}