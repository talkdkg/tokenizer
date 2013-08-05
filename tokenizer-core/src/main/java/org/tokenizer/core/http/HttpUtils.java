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
package org.tokenizer.core.http;

import org.apache.tika.mime.MediaType;

public class HttpUtils {

	public static String DEFAULT_CHARSET = "UTF-8";

	public static String getMimeTypeFromContentType(String contentType) {
		String result = "";
		MediaType mt = MediaType.parse(contentType);
		if (mt != null) {
			result = mt.getType() + "/" + mt.getSubtype();
		}

		return result;
	}

	public static String getCharsetFromContentType(String contentType) {
		String result = DEFAULT_CHARSET;
		MediaType mt = MediaType.parse(contentType);
		if (mt != null) {
			String charset = mt.getParameters().get("charset");
			if (charset != null) {
				result = charset;
			}
		}

		return result;
	}
}
