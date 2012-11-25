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

import org.tokenizer.executor.model.configuration.TaskConfiguration;

import com.vaadin.ui.FormFieldFactory;

public class SitemapsFetcherTaskConfigurationForm extends
        TaskConfigurationFormBase {
    private static final long serialVersionUID = 1L;

    public SitemapsFetcherTaskConfigurationForm(
            TaskConfiguration taskConfiguration) {
        super(taskConfiguration);
    }

    @Override
    public Class<?> getType() {
        return SitemapsFetcherTaskConfigurationForm.class;
    }

    @Override
    protected FormFieldFactory getFormFieldFactory() {
        return new DefaultTaskConfigurationFieldFactory();
    }
}
