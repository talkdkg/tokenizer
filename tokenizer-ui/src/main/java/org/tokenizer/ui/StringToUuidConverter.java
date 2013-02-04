package org.tokenizer.ui;

import java.util.Locale;
import java.util.UUID;

import com.vaadin.data.util.converter.Converter;

public class StringToUuidConverter implements Converter<String, UUID> {

    private static final long serialVersionUID = 1L;

    @Override
    public UUID convertToModel(String value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        value = value.trim();
        return UUID.fromString(value);
    }

    @Override
    public String convertToPresentation(final UUID value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        return value.toString();
    }

    @Override
    public Class<UUID> getModelType() {
        return UUID.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
