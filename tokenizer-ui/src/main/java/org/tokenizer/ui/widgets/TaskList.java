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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinApplication;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

public class TaskList extends Table { 
    //implements        Container.PropertySetChangeListener {
    
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TaskList.class);

    public TaskList(MyVaadinApplication app) {
        setSizeFull();
        setContainerDataSource(app.getTaskContainer());
        // setColumnHeaderMode(COLUMN_HEADER_MODE_EXPLICIT);
        // setVisibleColumns(PersonContainer.NATURAL_COL_ORDER);
        // setColumnHeaders(PersonContainer.COL_HEADERS_ENGLISH);
        setSelectable(true);
        setImmediate(true);
        
        addListener((Property.ValueChangeListener) app);
        
        
        //app.getTaskContainer().addListener(
        //        (Container.PropertySetChangeListener) this);

        
        
        /* We don't want to allow users to de-select a row */
        setNullSelectionAllowed(false);
        // setColumnCollapsingAllowed(true);
        // setColumnReorderingAllowed(true);
    }
}
