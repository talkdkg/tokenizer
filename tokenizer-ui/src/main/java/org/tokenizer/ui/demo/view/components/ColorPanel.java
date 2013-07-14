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
package org.tokenizer.ui.demo.view.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

/**
 * 
 * Allows the setting of background colour
 * 
 * @author David Sowerby 5 Jan 2013
 * 
 */
public class ColorPanel extends CssLayout {

	private String color = "#aeec31";

	@Override
	protected String getCss(Component c) {
		return "background: " + color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
