/*
 * Copyright 2013 Tokenizer Inc.
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
package org.tokenizer.ui.v7.view;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.engine.twitter.TweetCollectorTaskConfiguration;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskExistsException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.AbstractTaskConfiguration;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.RssFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SimpleMultithreadedFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsLinkedPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsPageFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsCrawlerTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.WeblogsSubscriberTaskConfiguration;
import org.tokenizer.ui.v7.modules.Broadcaster;
import org.tokenizer.util.zookeeper.ZkLockException;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TaskInfoComponent extends CustomComponent implements Broadcaster.BroadcastListener {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskInfoComponent.class);
    private static final int COMMON_FIELD_WIDTH = 48;
    Collection<TaskInfoBean> tasks;
    FieldGroup taskConfigurationFieldGroup;
    Component taskFieldsComponent = new FormLayout();
    HorizontalLayout formControls;
    Component tableControls;
    Button edit;
    Button save;
    Button discard;
    TaskInfoBean currentTask;
    TaskInfoBean newTask;
    BeanItemContainer<TaskInfoBean> container;
    boolean newTaskMode = false;

    private Table table;

    WritableExecutorModel model;
    CrawlerRepository repository;

    private VerticalLayout mainLayout;

    @Inject
    public TaskInfoComponent(final WritableExecutorModel model, final CrawlerRepository repository) {
        this.model = model;
        this.repository = repository;

        ExecutorModelListener listener = new MyExecutorModelListener();
        tasks = model.getTasks(listener);
        buildMainLayout();
        setCompositionRoot(mainLayout);

        buildFormControls();
    }

    private void buildMainLayout() {

        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);

        tableControls = buildTableControls();
        // this.addComponent(viewLabel());
        mainLayout.addComponent(tableControls);
        mainLayout.addComponent(buildTable());
        mainLayout.addComponent(taskFieldsComponent);

    }

    private Component buildTable() {
        table = new Table(null);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        updateTableData(table);
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                currentTask = (TaskInfoBean) table.getValue();
                setReadOnly(true);
            }
        });
        return table;
    }

    private void buildClassicRobotTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField host = new TextField("Host:");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        CheckBox followRedirects = new CheckBox("Follow redirects:");
        CheckBox followExternal = new CheckBox("Follow external outlinks:");
        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextArea urlFilterConfig = new TextArea("URL Filter");
        urlFilterConfig.setRows(20);
        urlFilterConfig.setColumns(30);
        componentContainer.addComponent(host);
        componentContainer.addComponent(followRedirects);
        componentContainer.addComponent(followExternal);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);
        componentContainer.addComponent(urlFilterConfig);
        fieldGroup.bind(host, "host");
        fieldGroup.bind(followRedirects, "followRedirects");
        fieldGroup.bind(followExternal, "followExternal");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        fieldGroup.bind(urlFilterConfig, "urlFilterConfig");
        componentContainer.addComponent(formControls);
    }

    private void buildHtmlSplitterTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField host = new TextField("Host");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField xpath = new TextField("XPath");
        xpath.setWidth(80, Unit.EM);
        TextField splitAttemptCounter = new TextField("Split Attempt Counter");
        splitAttemptCounter.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(host);
        componentContainer.addComponent(xpath);
        componentContainer.addComponent(splitAttemptCounter);
        fieldGroup.bind(host, "host");
        fieldGroup.bind(xpath, "xpath");
        fieldGroup.bind(splitAttemptCounter, "splitAttemptCounter");
        componentContainer.addComponent(formControls);
    }

    private void buildMessageParserTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField host = new TextField("Host");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField topicXPath = new TextField("topicXPath");
        topicXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField authorXPath = new TextField("authorXPath");
        authorXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField ageXPath = new TextField("ageXPath");
        ageXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField sexXPath = new TextField("sexXPath");
        sexXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField titleXPath = new TextField("titleXPath");
        titleXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField contentXPath = new TextField("contentXPath");
        contentXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField dateXPath = new TextField("dateXPath");
        dateXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField userRatingXPath = new TextField("userRatingXPath");
        userRatingXPath.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField parseAttemptCounter = new TextField("parseAttemptCounter");
        parseAttemptCounter.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(host);
        componentContainer.addComponent(topicXPath);
        componentContainer.addComponent(authorXPath);
        componentContainer.addComponent(ageXPath);
        componentContainer.addComponent(sexXPath);
        componentContainer.addComponent(titleXPath);
        componentContainer.addComponent(contentXPath);
        componentContainer.addComponent(dateXPath);
        componentContainer.addComponent(userRatingXPath);
        componentContainer.addComponent(parseAttemptCounter);
        fieldGroup.bind(host, "host");
        fieldGroup.bind(topicXPath, "topicXPath");
        fieldGroup.bind(authorXPath, "authorXPath");
        fieldGroup.bind(ageXPath, "ageXPath");
        fieldGroup.bind(sexXPath, "sexXPath");
        fieldGroup.bind(titleXPath, "titleXPath");
        fieldGroup.bind(contentXPath, "contentXPath");
        fieldGroup.bind(dateXPath, "dateXPath");
        fieldGroup.bind(userRatingXPath, "userRatingXPath");
        fieldGroup.bind(parseAttemptCounter, "parseAttemptCounter");
        componentContainer.addComponent(formControls);
    }

    private void buildTweetCollectorTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextArea keywords = new TextArea("keywords");
        keywords.setRows(40);
        keywords.setColumns(30);
        componentContainer.addComponent(keywords);
        fieldGroup.bind(keywords, "keywords");
        componentContainer.addComponent(formControls);
    }

    private void
        buildBaseTaskConfigurationForm(final FieldGroup fieldGroup, final ComponentContainer componentContainer) {
        TextField name = new TextField("Name:");
        name.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        ComboBox state = new ComboBox("Execution State:");
        state.setNewItemsAllowed(false);
        state.setNullSelectionAllowed(false);
        state.addItem(TaskGeneralState.START_REQUESTED);
        state.addItem(TaskGeneralState.STOP_REQUESTED);
        state.addItem(TaskGeneralState.DELETE_REQUESTED);
        componentContainer.addComponent(name);
        componentContainer.addComponent(state);
        fieldGroup.bind(name, "name");
        fieldGroup.bind(state, "generalState");
    }

    private void buildWeblogsSubscriberTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField url = new TextField("URL:");
        url.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField delay = new TextField("Delay:");
        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(url);
        componentContainer.addComponent(delay);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);
        fieldGroup.bind(url, "url");
        fieldGroup.bind(delay, "delay");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        componentContainer.addComponent(formControls);
    }

    private void buildWeblogsCrawlerTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField url = new TextField("URL:");
        url.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField delay = new TextField("Delay:");
        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(url);
        componentContainer.addComponent(delay);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);
        fieldGroup.bind(url, "url");
        fieldGroup.bind(delay, "delay");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        componentContainer.addComponent(formControls);
    }

    private void buildSitemapsFetcherTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);

        TextField host = new TextField("Host:");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        componentContainer.addComponent(host);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);

        fieldGroup.bind(host, "host");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");

        componentContainer.addComponent(formControls);

    }

    private void buildSitemapsPageFetcherTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);

        TextField host = new TextField("Host:");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        componentContainer.addComponent(host);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);

        fieldGroup.bind(host, "host");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");

        componentContainer.addComponent(formControls);

    }

    private void buildSitemapsLinkedPageFetcherTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);

        TextField host = new TextField("Host:");
        host.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);

        componentContainer.addComponent(host);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);

        fieldGroup.bind(host, "host");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");

        componentContainer.addComponent(formControls);

    }

    TextField uuid;
    TextField name;
    TextField state;
    TextField zkDataVersion;
    TreeMap<String, TextField> countersTextFields;

    private ComponentContainer buildTaskInfoBeanComponent(final TaskInfoBean task) {
        ComponentContainer componentContainer = new FormLayout();
        uuid = new TextField("UUID");
        uuid.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        uuid.setValue(task.getUuid().toString());
        uuid.setReadOnly(true);
        componentContainer.addComponent(uuid);
        name = new TextField("Name");
        name.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        name.setValue(task.getName());
        name.setReadOnly(true);
        componentContainer.addComponent(name);
        state = new TextField("State");
        state.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        state.setValue(task.getGeneralState().toString());
        state.setReadOnly(true);
        componentContainer.addComponent(state);
        zkDataVersion = new TextField("Version");
        zkDataVersion.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        zkDataVersion.setValue(Integer.toString(task.getZkDataVersion()));
        zkDataVersion.setReadOnly(true);
        componentContainer.addComponent(zkDataVersion);
        Map<String, Long> counters = task.getCounters();
        countersTextFields = new TreeMap<String, TextField>();
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            TextField field = new TextField(entry.getKey());
            field.setValue(entry.getValue().toString());
            field.setReadOnly(true);
            field.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
            // componentContainer.addComponent(field);
            countersTextFields.put(entry.getKey(), field);
        }
        for (Map.Entry<String, TextField> entry : countersTextFields.entrySet()) {
            componentContainer.addComponent(entry.getValue());
        }

        componentContainer.addComponent(formControls);
        return componentContainer;
    }

    private void updateTableData(final Table table) {
        container = new BeanItemContainer<TaskInfoBean>(TaskInfoBean.class, tasks);
        table.setContainerDataSource(container);
        // table.sort(new Object[] { "name", "implementationName" },
        // new boolean[] { true, true });
        table.setVisibleColumns(new String[] { "name", "implementationName", "submitDate", "generalState",
            "zkDataVersion" });
        table.setColumnHeaders(new String[] { "Name", "Implementation", "Date", "State", "Version" });
    }

    private void buildFormControls() {
        formControls = new HorizontalLayout();
        edit = new Button("Edit", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                setReadOnly(false);
            }
        });

        save = new Button("Save", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                try {
                    taskConfigurationFieldGroup.commit();
                    if (newTaskMode) {
                        update(newTask);
                    }
                    else {
                        update(currentTask);
                    }
                    setReadOnly(true);
                }
                catch (CommitException e) {
                    LOG.error("", e);
                }
            }
        });
        discard = new Button("Discard", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                taskConfigurationFieldGroup.discard();
                setReadOnly(true);
            }
        });
        edit.setVisible(true);
        save.setVisible(false);
        discard.setVisible(false);
        formControls.addComponent(edit);
        formControls.addComponent(save);
        formControls.addComponent(discard);

    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        super.setReadOnly(readOnly);
        edit.setVisible(readOnly);
        save.setVisible(!readOnly);
        discard.setVisible(!readOnly);
        ComponentContainer newComponent;
        if (!newTaskMode & readOnly) {
            newComponent = buildTaskInfoBeanComponent(currentTask);
        }
        else if (newTaskMode) {
            taskConfigurationFieldGroup = new FieldGroup();
            newComponent = new FormLayout();
            buildSelectTaskImplementationForm(taskConfigurationFieldGroup, newComponent);
        }
        else {
            taskConfigurationFieldGroup = new FieldGroup();
            newComponent = new FormLayout();
            if (currentTask.getTaskConfiguration() instanceof HtmlSplitterTaskConfiguration) {
                buildHtmlSplitterTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof MessageParserTaskConfiguration) {
                buildMessageParserTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof TweetCollectorTaskConfiguration) {
                buildTweetCollectorTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof ClassicRobotTaskConfiguration) {
                buildClassicRobotTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof WeblogsSubscriberTaskConfiguration) {
                buildWeblogsSubscriberTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof RssFetcherTaskConfiguration) {
            }
            else if (currentTask.getTaskConfiguration() instanceof SimpleMultithreadedFetcherTaskConfiguration) {
            }
            else if (currentTask.getTaskConfiguration() instanceof SitemapsFetcherTaskConfiguration) {
                buildSitemapsFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof SitemapsPageFetcherTaskConfiguration) {
                buildSitemapsPageFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof SitemapsLinkedPageFetcherTaskConfiguration) {
                buildSitemapsLinkedPageFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof WeblogsCrawlerTaskConfiguration) {
                buildWeblogsCrawlerTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }
            else if (currentTask.getTaskConfiguration() instanceof WeblogsParserTaskConfiguration) {
                buildWeblogsParserTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
            }

            BeanItem<AbstractTaskConfiguration> item = new BeanItem<AbstractTaskConfiguration>(currentTask.getTaskConfiguration());
            taskConfigurationFieldGroup.setItemDataSource(item);
        }
        // LOG.debug("Replacing task info component...");
        mainLayout.replaceComponent(taskFieldsComponent, newComponent);
        taskFieldsComponent = newComponent;
    }

    private class MyExecutorModelListener implements ExecutorModelListener {

        @Override
        public void process(final ExecutorModelEvent event) {

            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    LOG.debug("Event: {}", event.getType());
                    UUID uuid = event.getUuid();
                    TaskInfoBean task;
                    boolean propertyAdded = false;
                    try {
                        task = model.getTask(uuid);
                    }
                    catch (TaskNotFoundException e) {
                        /*
                         * this will also handle
                         * ExecutorModelEventType.TASK_DEFINITION_REMOVED
                         */
                        TaskInfoBean o = new TaskInfoBean(uuid);
                        container.removeItem(o);
                        return;
                    }


                    if (event.getType() == ExecutorModelEventType.TASK_ADDED) {
                    }
                    else if (event.getType() == ExecutorModelEventType.TASK_UPDATED) {
                    }

                    for (TaskInfoBean t : tasks) {
                        // TODO: do we need that?
                        if (task.equals(t) && isReadOnly()) {
                            t = task;
                        }
                    }
                    if (task.equals(currentTask) && isReadOnly()) {

                        currentTask = task;
                        setReadOnly(true);
                    }

                    task.makeMutable();
                    table.getContainerProperty(task, "zkDataVersion").setValue(task.getZkDataVersion());
                    table.getContainerProperty(task, "generalState").setValue(task.getGeneralState());
                    table.getContainerProperty(task, "name").setValue(task.getName());
                    
                }
            });

        }

    }

    private TaskInfoBean update(final TaskInfoBean task) {
        if (newTaskMode) {
            try {
                model.addTask(task);
            }
            catch (TaskExistsException e) {
                LOG.error(e.getMessage());
            }
            catch (TaskModelException e) {
                LOG.error(e.getMessage());
            }
            catch (TaskValidityException e) {
                LOG.error(e.getMessage());
            }
            return task;
        }
        UUID uuid = task.getUuid();
        String lock = lockTask(uuid);
        if (lock == null) {
            return null;
        }
        TaskInfoBean mutableTask = getMutableTask(uuid);
        if (mutableTask == null) {
            return null;
        }
        boolean changes = false;
        if (task.getTaskConfiguration() != null
            && !task.getTaskConfiguration().equals(mutableTask.getTaskConfiguration())) {
            mutableTask.setTaskConfiguration(task.getTaskConfiguration());
            changes = true;
        }
        if (task.getTaskConfiguration().getGeneralState() != null
            && task.getTaskConfiguration().getGeneralState() != mutableTask.getTaskConfiguration().getGeneralState()) {
            mutableTask.getTaskConfiguration().setGeneralState(task.getTaskConfiguration().getGeneralState());
            changes = true;
        }
        if (changes) {
            updateTask(mutableTask, lock);
            LOG.debug("Task definition updated: {}", uuid);
        }
        else {
            LOG.debug("Task already matches the specified settings, did not update it.");
        }
        unlockTask(lock, mutableTask);
        return mutableTask;
    }

    public String lockTask(final UUID uuid) {
        String lock = null;
        try {
            lock = model.lockTaskInternal(uuid, false);
        }
        catch (ZkLockException e) {
            LOG.error("", e);
        }
        catch (TaskNotFoundException e) {
            LOG.warn("Task not found: {}", uuid);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (KeeperException e) {
            LOG.error("", e);
        }
        catch (TaskModelException e) {
            LOG.error(e.getMessage());
        }
        return lock;
    }

    public void unlockTask(final String lock, final TaskInfoBean mutableTask) {
        boolean ignoreMissing =
            mutableTask.getTaskConfiguration().getGeneralState() != null
                && mutableTask.getTaskConfiguration().getGeneralState() == TaskGeneralState.DELETE_REQUESTED;
        try {
            model.unlockTask(lock, ignoreMissing);
        }
        catch (ZkLockException e) {
            LOG.error("", e);
        }
    }

    public TaskInfoBean getMutableTask(final UUID uuid) {
        TaskInfoBean mutableTask = null;
        try {
            mutableTask = model.getMutableTask(uuid);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (KeeperException e) {
            LOG.error("", e);
        }
        catch (TaskNotFoundException e) {
            LOG.error("", e);
        }
        return mutableTask;
    }

    public void updateTask(final TaskInfoBean mutableTask, final String lock) {
        try {
            model.updateTask(mutableTask, lock);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (KeeperException e) {
            LOG.error("", e);
        }
        catch (TaskNotFoundException e) {
            LOG.warn("Task not found: {}", mutableTask);
        }
        catch (TaskConcurrentModificationException e) {
            LOG.error("", e);
        }
        catch (ZkLockException e) {
            LOG.error("", e);
        }
        catch (TaskUpdateException e) {
            LOG.error("", e);
        }
        catch (TaskValidityException e) {
            LOG.error("", e);
        }
    }

    private Component buildTableControls() {
        ComponentContainer componentContainer = new HorizontalLayout();
        Button add = new Button("Add", new Button.ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                addTask();
            }
        });
        Button delete = new Button("Delete", new Button.ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
            }
        });
        componentContainer.addComponent(add);
        componentContainer.addComponent(delete);
        return componentContainer;
    }

    public void addTask() {
        UUID uuid = UUID.randomUUID();
        newTask = new TaskInfoBean(uuid);
        newTaskMode = true;
        setReadOnly(false);
    }

    private void buildSelectTaskImplementationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        ComboBox type = new ComboBox("Type");
        type.setNewItemsAllowed(false);
        type.setNullSelectionAllowed(false);
        type.setImmediate(true);
        type.addItem("");
        type.addItem("ClassicRobotTask");
        type.addItem("SitemapsFetcherTask");
        type.addItem("SitemapsPageFetcherTask");
        type.addItem("SitemapsLinkedPageFetcherTask");
        type.addItem("HtmlSplitterTask");
        type.addItem("MessageParserTask");
        type.addItem("TweetCollectorTask");
        type.addItem("WeblogsSubscriberTask");
        type.addItem("WeblogsCrawlerTask");
        type.addItem("WeblogsParserTask");
        type.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final ValueChangeEvent event) {
                String selected = (String) event.getProperty().getValue();
                System.out.println(selected);
                ComponentContainer newComponent = new FormLayout();
                taskConfigurationFieldGroup = new FieldGroup();
                if ("ClassicRobotTask".equals(selected)) {
                    newTask.setTaskConfiguration(new ClassicRobotTaskConfiguration());
                    buildClassicRobotTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("SitemapsFetcherTask".equals(selected)) {
                    newTask.setTaskConfiguration(new SitemapsFetcherTaskConfiguration());
                    buildSitemapsFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("SitemapsPageFetcherTask".equals(selected)) {
                    newTask.setTaskConfiguration(new SitemapsPageFetcherTaskConfiguration());
                    buildSitemapsPageFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("SitemapsLinkedPageFetcherTask".equals(selected)) {
                    newTask.setTaskConfiguration(new SitemapsLinkedPageFetcherTaskConfiguration());
                    buildSitemapsLinkedPageFetcherTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("HtmlSplitterTask".equals(selected)) {
                    newTask.setTaskConfiguration(new HtmlSplitterTaskConfiguration());
                    buildHtmlSplitterTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("MessageParserTask".equals(selected)) {
                    newTask.setTaskConfiguration(new MessageParserTaskConfiguration());
                    buildMessageParserTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("TweetCollectorTask".equals(selected)) {
                    newTask.setTaskConfiguration(new TweetCollectorTaskConfiguration());
                    buildTweetCollectorTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("WeblogsSubscriberTask".equals(selected)) {
                    newTask.setTaskConfiguration(new WeblogsSubscriberTaskConfiguration());
                    buildWeblogsSubscriberTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("WeblogsCrawlerTask".equals(selected)) {
                    newTask.setTaskConfiguration(new WeblogsCrawlerTaskConfiguration());
                    buildWeblogsCrawlerTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }
                else if ("WeblogsParserTask".equals(selected)) {
                    newTask.setTaskConfiguration(new WeblogsParserTaskConfiguration());
                    buildWeblogsParserTaskConfigurationForm(taskConfigurationFieldGroup, newComponent);
                }

                BeanItem<AbstractTaskConfiguration> item = new BeanItem<AbstractTaskConfiguration>(newTask.getTaskConfiguration());
                taskConfigurationFieldGroup.setItemDataSource(item);
                mainLayout.replaceComponent(taskFieldsComponent, newComponent);
                taskFieldsComponent = newComponent;
            }
        });
        componentContainer.addComponent(type);
        fieldGroup.bind(type, "type");
    }

    private void buildWeblogsParserTaskConfigurationForm(final FieldGroup fieldGroup,
        final ComponentContainer componentContainer) {
        buildBaseTaskConfigurationForm(fieldGroup, componentContainer);
        TextField url = new TextField("URL:");
        url.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField delay = new TextField("Delay:");
        TextField agentName = new TextField("Robot name:");
        agentName.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField emailAddress = new TextField("Robot Email:");
        emailAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        TextField webAddress = new TextField("Robot description URL:");
        webAddress.setWidth(COMMON_FIELD_WIDTH, Unit.EM);
        componentContainer.addComponent(url);
        componentContainer.addComponent(delay);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);
        fieldGroup.bind(url, "url");
        fieldGroup.bind(delay, "delay");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        componentContainer.addComponent(formControls);
    }

    @Override
    public void receiveBroadcast(final String message) {
        // TODO Auto-generated method stub

    }

}
