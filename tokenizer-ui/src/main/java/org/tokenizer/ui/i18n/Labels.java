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
package org.tokenizer.ui.i18n;

import java.util.EnumMap;

/**
 * The base for the resource bundle of Labels. This is an arbitrary division of i18N keys & values, but is loosely
 * defined as containing those value which are short, contain no parameters and are typically used for captions and
 * labels. They can of course be used anywhere.
 * 
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class Labels extends EnumResourceBundle<LabelKeys> {

	private static final EnumMap<LabelKeys, String> map = new EnumMap<LabelKeys, String>(LabelKeys.class);
	// TODO make map unmodifiable
	static {
		map.put(LabelKeys.ok, "ok");
		map.put(LabelKeys.cancel, "cancel");
		map.put(LabelKeys.first_name, "first name");
		map.put(LabelKeys.last_name, "last name");
		map.put(LabelKeys.small, "small");

	}

	@Override
	public EnumMap<LabelKeys, String> getMap() {
		return map;
	}

}
