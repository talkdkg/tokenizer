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
package org.tokenizer.ui.widgets;

import org.tokenizer.ui.MyVaadinApplication;
import org.tokenizer.ui.data.TaskVO;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Form;

public class TaskForm extends Form implements ClickListener {

    private static final long serialVersionUID = 1L;

    private Button save = new Button("Save", (ClickListener) this);
    private Button cancel = new Button("Cancel", (ClickListener) this);
    private Button edit = new Button("Edit", (ClickListener) this);

    private MyVaadinApplication app;
    private TaskVO newTask = null;
    private boolean newTaskMode = false;

    private final ComboBox cities = new ComboBox("City");

    
    public void addTask() {
        newTask = new TaskVO();

        setItemDataSource(new BeanItem(newTask));
        newTaskMode = true;
        setReadOnly(false);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        // TODO Auto-generated method stub

    }

}
