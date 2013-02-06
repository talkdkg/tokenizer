package org.tokenizer.ui.components;

import java.util.Collection;
import java.util.UUID;

import org.tokenizer.executor.model.api.ExecutorModelEvent;
import org.tokenizer.executor.model.api.ExecutorModelEventType;
import org.tokenizer.executor.model.api.ExecutorModelListener;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.ui.MyVaadinUI;
import org.tokenizer.ui.views.TaskContainer;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
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
    private Table table;
    private GridLayout form;

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
        mainLayout.addComponent(buildForm());
    }

    private Component buildTable() {
        TaskContainer taskContainer = new TaskContainer(app);
        // setContainerDataSource(taskContainer);
        // addListener(app);
        // taskContainer.addListener((Container.PropertySetChangeListener)
        // this);
        table = new Table(null);
        setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        updateTableData();
        return table;
    }

    private Component buildForm() {
        form = new GridLayout(2, 3);
        TextField firstName = new TextField("First name:");
        TextField lastName = new TextField("Last name:");
        TextField phoneNumber = new TextField("Phone Number:");
        TextField email = new TextField("E-mail address:");
        DateField dateOfBirth = new DateField("Date of birth:");
        TextArea comments = new TextArea("Comments:");
        form.addComponent(firstName);
        form.addComponent(lastName);
        form.addComponent(phoneNumber);
        form.addComponent(email);
        form.addComponent(dateOfBirth);
        form.addComponent(comments);
        return form;
    }

    private void updateTableData() {
        BeanItemContainer<TaskInfoBean> container = new BeanItemContainer<TaskInfoBean>(
                TaskInfoBean.class, tasks);
        table.setContainerDataSource(container);
        table.sort(new Object[] { "name", "implementationName" },
                new boolean[] { true, true });
        table.setVisibleColumns(new String[] { "name", "implementationName",
                "submitDate", "generalState", "zkDataVersion" });
        table.setColumnHeaders(new String[] { "Name", "Implementation", "Date",
                "State", "Version" });
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
