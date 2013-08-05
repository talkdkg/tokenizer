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
package org.tokenizer.ui.base.data;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import com.vaadin.data.util.converter.Converter;

/**
 * Implements a Converter to handle the Joda DateTime type
 * 
 */
public class DateTimeConverter implements Converter<Date, DateTime> {

    public DateTime convertToModel(Date value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        return new DateTime(value);

    }

    public Date convertToPresentation(DateTime value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value.toDate();
    }

    @Override
    public Class<DateTime> getModelType() {
        return DateTime.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }

    @Override
    public DateTime convertToModel(Date value, Class<? extends DateTime> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date convertToPresentation(DateTime value, Class<? extends Date> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }

}
