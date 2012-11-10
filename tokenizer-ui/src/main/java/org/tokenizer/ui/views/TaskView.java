package org.tokenizer.ui.views;

import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.ui.VerticalSplitPanel;

public class TaskView extends VerticalSplitPanel {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(TaskView.class);

    private static final long serialVersionUID = 1L;
    private MyVaadinApplication app;
    
    private TaskList taskList;
    private TaskForm taskForm;

    public TaskView(MyVaadinApplication app) {

        this.app = app;
        this.taskList = new TaskList(app);
        this.taskForm = new TaskForm(app);

        setCaption("Tokenizer: Tasks");
        setSizeFull();

        setFirstComponent(taskList);
        setSecondComponent(taskForm);
        setSplitPosition(40);

    }

    public TaskList getTaskList() {
        return taskList;
    }

    public TaskForm getTaskForm() {
        return taskForm;
    }

    
    
}
