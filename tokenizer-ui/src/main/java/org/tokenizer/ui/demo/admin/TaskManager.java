package org.tokenizer.ui.demo.admin;

import org.tokenizer.ui.components.TaskInfoComponent;
import org.tokenizer.ui.demo.AbstractVaadinChartExample;

import com.vaadin.ui.Component;

public class TaskManager extends AbstractVaadinChartExample {

    @Override
    protected Component getChart() {
        return new TaskInfoComponent();

    }

}
