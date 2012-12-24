package org.tokenizer.ui.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.ui.TabSheet;

public class TaskResultsTabSheet extends TabSheet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(TaskResultsTabSheet.class);
    private final MyVaadinApplication app;
    private final TaskForm taskForm;
    private final TaskOutputView taskOutputView;

    public TaskResultsTabSheet(final MyVaadinApplication app) {
        setHeight(100, UNITS_PERCENTAGE);
        this.app = app;
        taskForm = new TaskForm(app);
        taskOutputView = new TaskOutputView(app);
        addTab(taskForm, "Task Configuration");
        addTab(taskOutputView);
    }

    public TaskForm getTaskForm() {
        return taskForm;
    }
}
