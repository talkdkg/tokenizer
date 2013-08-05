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

public abstract class BaseUrlNormalizer {

	/**
	 * Convert <url> into a normalized format, where unimportant differences between two URLs have been removed.
	 * 
	 * @param url
	 *            - URL to normalize. Might not be valid, e.g. missing a protocol
	 * @return - normalized URL. Still might not be valid, if input URL (for example) uses an unknown protocol and thus
	 *         no checks can be done.
	 */
	public abstract String normalize(String url);
}
