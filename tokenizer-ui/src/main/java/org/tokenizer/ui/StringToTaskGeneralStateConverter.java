/*
 * Copyright 2013 Tokenizer Inc.
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
package org.tokenizer.ui;

import java.util.Locale;

import org.tokenizer.executor.model.api.TaskGeneralState;

import com.vaadin.data.util.converter.Converter;

public class StringToTaskGeneralStateConverter implements
        Converter<String, TaskGeneralState> {

    private static final long serialVersionUID = 1L;

    @Override
    public TaskGeneralState convertToModel(String value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        value = value.trim();
        if (value.equals("DELETE_REQUESTED"))
            return TaskGeneralState.DELETE_REQUESTED;
        if (value.equals("START_REQUESTED"))
            return TaskGeneralState.START_REQUESTED;
        if (value.equals("STOP_REQUESTED"))
            return TaskGeneralState.STOP_REQUESTED;
        throw new RuntimeException("Can't convert " + value
                + " into TaskGeneralState enum");
    }

    @Override
    public String convertToPresentation(final TaskGeneralState value,
            final Locale locale) throws ConversionException {
        if (value == null)
            return null;
        return value.toString();
    }

    @Override
    public Class<TaskGeneralState> getModelType() {
        return TaskGeneralState.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
