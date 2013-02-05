package org.tokenizer.ui;

import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.executor.model.api.TaskGeneralState;
import org.tokenizer.executor.model.api.TaskInfoBean;
import org.tokenizer.executor.model.api.TaskNotFoundException;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.MessageParserTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.views.TaskView;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinUI extends UI implements Button.ClickListener,
        Property.ValueChangeListener {

    private static final long serialVersionUID = 1L;
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(MyVaadinUI.class);
    private final HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel();
    private Button showTaskInfoButton;
    private Button showTasksButton;
    private Button addNewTaskButton;
    private TaskInfoComponent taskInfoComponent;
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
    public void init(final VaadinRequest request) {
        // Use the factory globally in the application
        VaadinSession.getCurrent()
                .setConverterFactory(new MyConverterFactory());
        applicationContext = ApplicationContextProvider.getApplicationContext();
        model = (WritableExecutorModel) applicationContext
                .getBean("executorModel");
        repository = (CrawlerRepository) applicationContext
                .getBean("crawlerRepository");
        buildMainLayout();
    }

    private void buildMainLayout() {
        setSizeFull();
        layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(createToolbar());
        setContent(layout);
    }

    public HorizontalLayout createToolbar() {
        HorizontalLayout lo = new HorizontalLayout();
        showTaskInfoButton = new Button("Show Tasks (new UI)");
        showTasksButton = new Button("Show Tasks");
        addNewTaskButton = new Button("Add New Task");
        lo.addComponent(showTaskInfoButton);
        lo.addComponent(showTasksButton);
        lo.addComponent(addNewTaskButton);
        showTasksButton.addListener(this);
        addNewTaskButton.addListener(this);
        showTaskInfoButton.addListener(this);
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

    @Override
    public void buttonClick(final ClickEvent event) {
        final Button source = event.getButton();
        if (source == showTaskInfoButton) {
            if (taskInfoComponent == null) {
                taskInfoComponent = new TaskInfoComponent(this);
            }
            setMainComponent(taskInfoComponent);
        } else if (source == showTasksButton) {
            showTaskView();
        } else if (source == addNewTaskButton) {
            addNewTask();
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        Property property = event.getProperty();
        if (property == getTaskView().getTaskList()) {
            Object itemId = getTaskView().getTaskList().getValue();
            Item item = getTaskView().getTaskList().getItem(itemId);
            UUID uuid = (UUID) itemId;
            TaskConfiguration taskConfiguration;
            try {
                TaskInfoBean task = getModel().getTask(uuid);
                taskConfiguration = task.getTaskConfiguration();
                getTaskView().getTaskForm().setItemDataSource(item);
                // TaskInfoBean task = getModel().getTask(uuid);
                // taskConfiguration = task.getTaskConfiguration();
                // getTaskView().getTaskForm().setItemDataSource(
                // new BeanItem(task, new String[] { "uuid",
                // "taskConfiguration" }));
            } catch (TaskNotFoundException e) {
                return;
            }
            if (taskConfiguration instanceof ClassicRobotTaskConfiguration) {
                String host = ((ClassicRobotTaskConfiguration) taskConfiguration)
                        .getHost();
                setSelectedHost(host);
            } else if (taskConfiguration instanceof HtmlSplitterTaskConfiguration) {
                String host = ((HtmlSplitterTaskConfiguration) taskConfiguration)
                        .getHost();
                setSelectedHost(host);
            } else if (taskConfiguration instanceof MessageParserTaskConfiguration) {
                String host = ((MessageParserTaskConfiguration) taskConfiguration)
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
        LOG.debug("selectedHost: {}", selectedHost);
    }

    public int getSelectedHttpResponseCode() {
        return selectedHttpResponseCode;
    }

    public void setSelectedHttpResponseCode(final int selectedHttpResponseCode) {
        this.selectedHttpResponseCode = selectedHttpResponseCode;
    }

    class MyConverterFactory extends DefaultConverterFactory {

        @Override
        public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
                final Class<PRESENTATION> presentationType,
                final Class<MODEL> modelType) {
            // LOG.warn("Model type: {}", modelType.getCanonicalName());
            // Handle one particular type conversion
            if (String.class == presentationType && UUID.class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToUuidConverter();
            if (String.class == presentationType
                    && TaskGeneralState.class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToTaskGeneralStateConverter();
            if (String.class == presentationType && byte[].class == modelType)
                return (Converter<PRESENTATION, MODEL>) new StringToByteArrayConverter();
            if (TaskConfiguration.class == modelType)
                return null;
            // Default to the supertype
            return super.createConverter(presentationType, modelType);
        }
    }
}