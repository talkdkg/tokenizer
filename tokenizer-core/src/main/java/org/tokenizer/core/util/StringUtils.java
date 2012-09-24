/*
 * Copyright 2007-2012 Tokenizer Inc.
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
