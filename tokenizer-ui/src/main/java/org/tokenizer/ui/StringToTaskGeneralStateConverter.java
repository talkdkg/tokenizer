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
