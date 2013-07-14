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

import org.apache.james.mime4j.dom.datetime.DateTime;
import org.joda.money.Money;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class V7DefaultConverterFactory extends DefaultConverterFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(Class<PRESENTATION> presentationType,
			Class<MODEL> modelType) {

		if (modelType == DateTime.class) {
			return (Converter<PRESENTATION, MODEL>) new DateTimeConverter();
		}

		if (modelType == Money.class) {
			return (Converter<PRESENTATION, MODEL>) new MoneyConverter();
		}

		return super.createConverter(presentationType, modelType);

	}

}
