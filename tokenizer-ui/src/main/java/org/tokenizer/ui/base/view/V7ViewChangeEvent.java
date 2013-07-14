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
package org.tokenizer.ui.base.view;

import org.tokenizer.ui.base.navigate.V7Navigator;

public class V7ViewChangeEvent {
	private final V7View oldView;
	private final V7View newView;
	private final String viewName;
	private final String parameters;
	private final V7Navigator navigator;

	public V7ViewChangeEvent(V7Navigator navigator, V7View oldView, V7View newView, String viewName, String parameters) {
		super();
		this.oldView = oldView;
		this.newView = newView;
		this.viewName = viewName;
		this.parameters = parameters;
		this.navigator = navigator;
	}

	public V7View getOldView() {
		return oldView;
	}

	public V7View getNewView() {
		return newView;
	}

	public String getViewName() {
		return viewName;
	}

	public String getParameters() {
		return parameters;
	}

	public V7Navigator getNavigator() {
		return navigator;
	}

}
