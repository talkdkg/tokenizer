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
