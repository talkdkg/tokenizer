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

import org.tokenizer.core.util.MD5;

import com.vaadin.data.util.converter.Converter;

public class ConfigConverter implements Converter<String, byte[]> {

    private static final long serialVersionUID = 1L;

    public byte[] convertToModel(String value, final Locale locale)
            throws ConversionException {
        if (value == null)
            return null;
        value = value.trim();
        return MD5.hex2Byte(value);
    }

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

    @Override
    public byte[] convertToModel(String value, Class<? extends byte[]> targetType, Locale locale)
        throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String convertToPresentation(byte[] value, Class<? extends String> targetType, Locale locale)
        throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return null;
    }
}
