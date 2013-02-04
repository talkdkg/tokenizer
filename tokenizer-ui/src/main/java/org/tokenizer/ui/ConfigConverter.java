package org.tokenizer.ui;

import java.util.Locale;

import org.tokenizer.core.util.MD5;

import com.vaadin.data.util.converter.Converter;

public class ConfigConverter implements Converter<String, byte[]> {

    private static final long serialVersionUID = 1L;

    @Override
    public byte[] convertToModel(String value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        value = value.trim();
        return MD5.hex2Byte(value);
    }

    @Override
    public String convertToPresentation(final byte[] value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        return value.toString();
    }

    @Override
    public Class<byte[]> getModelType() {
        return byte[].class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
