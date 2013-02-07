package org.tokenizer.ui.components;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskConcurrentModificationException;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskModelException;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.TaskUpdateException;
import org.tokenizer.executor.model.api.TaskValidityException;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.MyVaadinUI;
import org.tokenizer.util.zookeeper.ZkLockException;

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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TaskInfoComponent extends CustomComponent {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskInfoComponent.class);
    private final MyVaadinUI app;
    Collection<TaskInfoBean> tasks;
    private HorizontalLayout mainLayout;
    FieldGroup taskConfigurationFieldGroup;
    Component taskFieldsComponent = new GridLayout();
    Component formControls;
    Button edit;
    Button save;
    Button discard;
    TaskInfoBean currentTask;

    public TaskInfoComponent(final MyVaadinUI app) {
        this.app = app;
        ExecutorModelListener listener = new MyExecutorModelListener();
        tasks = MyVaadinUI.getModel().getTasks(listener);
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private void buildMainLayout() {
        mainLayout = new HorizontalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.addComponent(buildTable());
        mainLayout.addComponent(taskFieldsComponent);
        buildFormControls();
    }

    private Component buildTable() {
        // setContainerDataSource(taskContainer);
        // addListener(app);
        // taskContainer.addListener((Container.PropertySetChangeListener)
        // this);
        final Table table = new Table(null);
        setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        updateTableData(table);
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.warn("value changed...");
                currentTask = (TaskInfoBean) table.getValue();
                setReadOnly(true);
            }
        });
        return table;
    }

    private void buildClassicRobotTaskConfigurationForm(FieldGroup fieldGroup,
            ComponentContainer componentContainer) {
        TextField name = new TextField("Name:");
        ComboBox state = new ComboBox("Execution State:");
        state.setNewItemsAllowed(false);
        state.setNullSelectionAllowed(false);
        state.addItem(TaskGeneralState.START_REQUESTED);
        state.addItem(TaskGeneralState.STOP_REQUESTED);
        state.addItem(TaskGeneralState.DELETE_REQUESTED);
        TextField host = new TextField("Host:");
        CheckBox followRedirects = new CheckBox("Follow redirects:");
        CheckBox followExternal = new CheckBox("Follow external outlinks:");
        TextField agentName = new TextField("Robot name:");
        TextField emailAddress = new TextField("Robot Email:");
        TextField webAddress = new TextField("Robot description URL:");
        componentContainer.addComponent(name);
        componentContainer.addComponent(state);
        componentContainer.addComponent(host);
        componentContainer.addComponent(followRedirects);
        componentContainer.addComponent(followExternal);
        componentContainer.addComponent(agentName);
        componentContainer.addComponent(emailAddress);
        componentContainer.addComponent(webAddress);
        fieldGroup.bind(name, "name");
        fieldGroup.bind(state, "generalState");
        fieldGroup.bind(host, "host");
        fieldGroup.bind(followRedirects, "followRedirects");
        fieldGroup.bind(followExternal, "followExternal");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
        if (formControls == null)
            formControls = buildFormControls();
        componentContainer.addComponent(formControls);
    }

    private ComponentContainer buildTaskInfoBeanComponent(TaskInfoBean task) {
        ComponentContainer componentContainer = new GridLayout(1, 1);
        TextField uuid = new TextField("UUID");
        uuid.setValue(task.getUuid().toString());
        uuid.setReadOnly(true);
        componentContainer.addComponent(uuid);
        TextField name = new TextField("Name");
        name.setValue(task.getName());
        name.setReadOnly(true);
        componentContainer.addComponent(name);
        TextField state = new TextField("State");
        state.setValue(task.getGeneralState().toString());
        state.setReadOnly(true);
        componentContainer.addComponent(state);
        TextField zkDataVersion = new TextField("Version");
        zkDataVersion.setValue(Integer.toString(task.getZkDataVersion()));
        zkDataVersion.setReadOnly(true);
        componentContainer.addComponent(zkDataVersion);
        Map<String, Long> counters = task.getCounters();
        for (Map.Entry<String, Long> entry : counters.entrySet()) {
            TextField field = new TextField(entry.getKey());
            field.setValue(entry.getValue().toString());
            field.setReadOnly(true);
            componentContainer.addComponent(field);
        }
        if (formControls == null)
            formControls = buildFormControls();
        componentContainer.addComponent(formControls);
        return componentContainer;
    }

    private void updateTableData(Table table) {
        BeanItemContainer<TaskInfoBean> container = new BeanItemContainer<TaskInfoBean>(
                TaskInfoBean.class, tasks);
        table.setContainerDataSource(container);
        // table.sort(new Object[] { "name", "implementationName" },
        // new boolean[] { true, true });
        table.setVisibleColumns(new String[] { "name", "implementationName",
                "submitDate", "generalState", "zkDataVersion" });
        table.setColumnHeaders(new String[] { "Name", "Implementation", "Date",
                "State", "Version" });
    }

    private Component buildFormControls() {
        ComponentContainer formControls = new HorizontalLayout();
        edit = new Button("Edit", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                setReadOnly(false);
            }
        });
        save = new Button("Save", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    taskConfigurationFieldGroup.commit();
                    update(currentTask);
                    setReadOnly(true);
                } catch (CommitException e) {
                    LOG.error("", e);
                }
            }
        });
        discard = new Button("Discard", new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
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
        return formControls;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        edit.setVisible(readOnly);
        save.setVisible(!readOnly);
        discard.setVisible(!readOnly);
        ComponentContainer newComponent;
        if (readOnly) {
            newComponent = buildTaskInfoBeanComponent(currentTask);
        } else {
            taskConfigurationFieldGroup = new FieldGroup();
            newComponent = new GridLayout(1, 1);
            buildClassicRobotTaskConfigurationForm(taskConfigurationFieldGroup,
                    newComponent);
            BeanItem<TaskConfiguration> item = new BeanItem<TaskConfiguration>(
                    currentTask.getTaskConfiguration());
            taskConfigurationFieldGroup.setItemDataSource(item);
        }
        mainLayout.replaceComponent(taskFieldsComponent, newComponent);
        taskFieldsComponent = newComponent;
    }

    private class MyExecutorModelListener implements ExecutorModelListener {

        @Override
        public void process(final ExecutorModelEvent event) {
            LOG.debug("Event::: {}", event.getType());
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
                // removeItem(uuid);
                return;
            }
            if (event.getType() == ExecutorModelEventType.TASK_ADDED) {
            } else if (event.getType() == ExecutorModelEventType.TASK_UPDATED) {
            }
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
