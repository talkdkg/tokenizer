/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.core.urls;

import org.tokenizer.core.datum.UrlDatum;

/**
 * Simple UrlFilter that uses a URL validator.
 */
public class SimpleUrlFilter extends BaseUrlFilter {

	private BaseUrlValidator _validator;

	public SimpleUrlFilter() {
		this(new SimpleUrlValidator());
	}

	public SimpleUrlFilter(BaseUrlValidator validator) {
		_validator = validator;
	}

	@Override
	public boolean isRemove(UrlDatum datum) {
		return !_validator.isValid(datum.getUrl());
	}

}
