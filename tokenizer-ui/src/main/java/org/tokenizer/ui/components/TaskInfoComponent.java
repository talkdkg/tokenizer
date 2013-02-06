package org.tokenizer.ui.components;

import java.util.Collection;
import java.util.UUID;

import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.MyVaadinUI;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TaskInfoComponent extends CustomComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskInfoComponent.class);
    private final MyVaadinUI app;
    Collection<TaskInfoBean> tasks;
    private VerticalLayout mainLayout;
    ComponentContainer classicRobotTaskConfigurationComponentContainer;
    FieldGroup classicRobotTaskConfigurationFieldGroup;

    public TaskInfoComponent(final MyVaadinUI app) {
        this.app = app;
        ExecutorModelListener listener = new MyExecutorModelListener();
        tasks = MyVaadinUI.getModel().getTasks(listener);
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private void buildMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.addComponent(buildTable());
        classicRobotTaskConfigurationComponentContainer = new GridLayout(3, 1);
        classicRobotTaskConfigurationFieldGroup = new FieldGroup();
        // fieldGroup = new BeanFieldGroup<ClassicRobotTaskConfiguration>(
        // ClassicRobotTaskConfiguration.class);
        buildClassicRobotTaskConfigurationForm(
                classicRobotTaskConfigurationFieldGroup,
                classicRobotTaskConfigurationComponentContainer);
        mainLayout
                .addComponent(classicRobotTaskConfigurationComponentContainer);
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
                showTask((TaskInfoBean) table.getValue());
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
        fieldGroup.bind(state, "state");
        fieldGroup.bind(host, "host");
        fieldGroup.bind(followRedirects, "followRedirects");
        fieldGroup.bind(followExternal, "followExternal");
        fieldGroup.bind(agentName, "agentName");
        fieldGroup.bind(emailAddress, "emailAddress");
        fieldGroup.bind(webAddress, "webAddress");
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

    private void showTask(TaskInfoBean task) {
        if (task == null) {
            // task = new TaskInfoBean(UUID.randomUUID());
        }
        LOG.warn("ZZZ Task: " + task.getTaskConfiguration());
        // BeanItem<TaskInfoBean> item = new BeanItem<TaskInfoBean>(task);
        // item.expandProperty("taskConfiguration", "name", "state", "host");
        BeanItem<TaskConfiguration> item = new BeanItem<TaskConfiguration>(
                task.getTaskConfiguration());
        classicRobotTaskConfigurationFieldGroup.setItemDataSource(item);
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
}
