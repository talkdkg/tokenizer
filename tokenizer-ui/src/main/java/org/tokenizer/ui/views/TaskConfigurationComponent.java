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

import org.tokenizer.executor.engine.twitter.TweetCollectorTaskConfiguration;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.HtmlSplitterTaskConfiguration;
import org.tokenizer.executor.model.configuration.RssFetcherTaskConfiguration;
import org.tokenizer.executor.model.configuration.SitemapsFetcherTaskConfiguration;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class TaskConfigurationComponent extends CustomComponent {
    private static final long serialVersionUID = 1L;
    private TaskConfigurationFormBase taskConfigurationField;
    private final VerticalLayout layout;

    public TaskConfigurationFormBase getTaskConfigurationField() {
        return taskConfigurationField;
    }

    public TaskConfigurationComponent() {
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(getAvailableTypes());
        setCompositionRoot(layout);
    }

    public ComboBox getAvailableTypes() {
        ComboBox type = new ComboBox("Type");
        type.setNewItemsAllowed(false);
        type.setNullSelectionAllowed(false);
        type.setImmediate(true);
        type.addListener(getTypeValueChangeListener());
        type.addItem("");
        type.addItem("ClassicRobotTask");
        type.addItem("HtmlSplitterTask");
        type.addItem("TweetCollectorTask");
        type.addItem("SitemapsFetcherTask");
        type.addItem("RssFetcherTask");
        return type;
    }

    /**
     * Creates value change listener for the table
     */
    private Property.ValueChangeListener getTypeValueChangeListener() {
        return new Property.ValueChangeListener() {
            private static final long serialVersionUID = 31L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                String selected = (String) event.getProperty().getValue();
                System.out.println(selected);
                if (taskConfigurationField != null) {
                    layout.removeComponent(taskConfigurationField);
                }
                if ("ClassicRobotTask".equals(selected)) {
                    taskConfigurationField = new ClassicRobotTaskConfigurationForm(
                            new ClassicRobotTaskConfiguration());
                } else if ("HtmlSplitterTask".equals(selected)) {
                    taskConfigurationField = new HtmlSplitterTaskConfigurationForm(
                            new HtmlSplitterTaskConfiguration());
                } else if ("TweetCollectorTask".equals(selected)) {
                    taskConfigurationField = new TweetCollectorTaskConfigurationForm(
                            new TweetCollectorTaskConfiguration());
                } else if ("RssFetcherTask".equals(selected)) {
                    taskConfigurationField = new RssFetcherTaskConfigurationForm(
                            new RssFetcherTaskConfiguration());
                } else if ("SitemapsFetcherTask".equals(selected)) {
                    taskConfigurationField = new SitemapsFetcherTaskConfigurationForm(
                            new SitemapsFetcherTaskConfiguration());
                }
                if (taskConfigurationField != null) {
                    layout.addComponent(taskConfigurationField);
                }
            }
        };
    }
}
