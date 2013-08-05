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

import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

public class SimpleUrlValidator extends BaseUrlValidator {

	private static final Pattern HTTP_PATTERN = Pattern.compile("^(http|https):");

	@Override
	public boolean isValid(String urlString) {
		if (!HTTP_PATTERN.matcher(urlString).find()) {
			return false;
		}

		try {
			URL url = new URL(urlString);
			String hostname = url.getHost();
			if ((hostname == null) || (hostname.length() == 0)) {
				return false;
			}

			URI uri = new URI(urlString);
			hostname = uri.getHost();
			if ((hostname == null) || (hostname.length() == 0)) {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
