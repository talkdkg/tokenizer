package org.tokenizer.ui.views;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.ui.Form;

public abstract class AbstractTaskConfigurationField  extends CustomField {
    
    protected Form taskConfigurationForm;

    public Form getTaskConfigurationForm() {
        return taskConfigurationForm;
    }

    public void setTaskConfigurationForm(Form taskConfigurationForm) {
        this.taskConfigurationForm = taskConfigurationForm;
    }

    
    
    
    
}
