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
package org.tokenizer.core.util;

import java.util.ArrayList;

public class StringUtils {

	// Do our own version of String.split(), which returns every section even if
	// it's empty. This then satisfies what we need, namely:
	//
	// "a=b" => "a" "b"
	// "" => ""
	// "=" => "" ""
	// "a=" => "a" ""
	// "a==" => "a" "" ""
	public static String[] splitOnChar(String str, char c) {
		ArrayList<String> result = new ArrayList<String>();

		int lastOffset = 0;
		int curOffset;
		while ((curOffset = str.indexOf(c, lastOffset)) != -1) {
			result.add(str.substring(lastOffset, curOffset));
			lastOffset = curOffset + 1;
		}

		result.add(str.substring(lastOffset));

		return result.toArray(new String[result.size()]);
	}

}
