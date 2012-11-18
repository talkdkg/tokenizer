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
        setCaption("Distributed Executor: Tasks");
        setSizeFull();
        setFirstComponent(this.taskList);
        setSecondComponent(this.taskForm);
        setSplitPosition(40);
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public TaskForm getTaskForm() {
        return taskForm;
    }
}
