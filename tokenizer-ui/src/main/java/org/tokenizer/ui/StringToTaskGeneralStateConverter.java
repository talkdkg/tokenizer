/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.ui;

import java.util.Locale;

import org.tokenizer.executor.model.api.TaskGeneralState;

import com.vaadin.data.util.converter.Converter;

public class StringToTaskGeneralStateConverter implements
        Converter<String, TaskGeneralState> {

    private static final long serialVersionUID = 1L;

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

    @Override
    public TaskGeneralState convertToModel(String value, Class<? extends TaskGeneralState> targetType, Locale locale)
        throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String convertToPresentation(TaskGeneralState value, Class<? extends String> targetType, Locale locale)
        throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }
}
