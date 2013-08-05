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
import java.util.UUID;

import com.vaadin.data.util.converter.Converter;

public class StringToUuidConverter implements Converter<String, UUID> {

    private static final long serialVersionUID = 1L;

    public UUID convertToModel(String value, final Locale locale) throws ConversionException {
        if (value == null)
            return null;
        value = value.trim();
        return UUID.fromString(value);
    }

    public String convertToPresentation(final UUID value, final Locale locale) throws ConversionException {
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

    @Override
    public UUID convertToModel(String value, Class<? extends UUID> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String convertToPresentation(UUID value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }
}
