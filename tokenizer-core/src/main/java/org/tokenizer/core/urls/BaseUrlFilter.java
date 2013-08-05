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
package org.tokenizer.core.urls;

import org.tokenizer.core.datum.UrlDatum;

/**
 * Filter urls
 */
public abstract class BaseUrlFilter {

	/**
	 * Return true if we should filter out (remove) the datum
	 */
	public abstract boolean isRemove(UrlDatum datum);

}
