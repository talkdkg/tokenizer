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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.MyVaadinUI;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;

public class TaskList extends Table implements
        Container.PropertySetChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TaskList.class);
    private TaskContainer taskContainer = null;

    public TaskList(final MyVaadinUI app) {
        // setSizeFull();
        taskContainer = new TaskContainer(app);
        setContainerDataSource(taskContainer);
        setSelectable(true);
        setImmediate(true);
        setNullSelectionAllowed(false);
        // addListener(app);
        taskContainer.addListener((Container.PropertySetChangeListener) this);
    }

    @Override
    public void containerPropertySetChange(
            final Container.PropertySetChangeEvent event) {
        disableContentRefreshing();
        super.containerPropertySetChange(event);
        Collection<?> containerPropertyIds = getContainerDataSource()
                .getContainerPropertyIds();
        setVisibleColumns(containerPropertyIds.toArray());
        resetPageBuffer();
        enableContentRefreshing(true);
    }
}
