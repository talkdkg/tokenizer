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

	@Override
	public DateTime convertToModel(Date value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {

		return new DateTime(value);

	}

	@Override
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

}
