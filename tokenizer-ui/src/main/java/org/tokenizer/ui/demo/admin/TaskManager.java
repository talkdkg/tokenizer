package org.tokenizer.ui.demo.admin;

import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.demo.AbstractScreen;

import com.vaadin.ui.Component;

public class TaskManager extends AbstractScreen {

    @Override
    protected Component get() {
        return new TaskInfoComponent();

    }

}
