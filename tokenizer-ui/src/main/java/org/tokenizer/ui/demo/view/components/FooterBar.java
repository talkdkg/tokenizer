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

import javax.inject.Inject;

import org.tokenizer.ui.base.guice.uiscope.UIScoped;


import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ChameleonTheme;

@UIScoped
public class FooterBar extends HorizontalLayout {

	private final Label infoLabel;
	private final Label msgLabel;

	@Inject
	protected FooterBar() {
		super();
		setSpacing(true);
		infoLabel = new Label();
		infoLabel
				.setValue("  Because the footer bar is @UIScoped, it can be injected into any component to show user messages.  Last user message:   ");
		msgLabel = new Label();
		msgLabel.addStyleName(ChameleonTheme.LABEL_H3);

		Label padLabel = new Label(" ");
		padLabel.setWidth("20px");
		Label padLabel2 = new Label(" ");
		padLabel2.setWidth("20px");

		addComponent(infoLabel);
		// addComponent(padLabel);
		addComponent(msgLabel);
	}

	public void setUserMessage(String msg) {
		msgLabel.setValue(msg);
	}

	public String getUserMessage() {
		return msgLabel.getValue();
	}

}
