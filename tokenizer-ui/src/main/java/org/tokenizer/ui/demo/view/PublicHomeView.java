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
package org.tokenizer.ui.demo.view;

import java.util.List;

import javax.inject.Inject;

import org.tokenizer.ui.base.view.VerticalViewBase;


import com.vaadin.server.ThemeResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ChameleonTheme;

public class PublicHomeView extends VerticalViewBase implements ClickListener {

	private Embedded logo;

	@Inject
	protected PublicHomeView() {
		super();
		buildMainLayout();
	}

	private void buildMainLayout() {
		ThemeResource resource = new ThemeResource("html/homepage.html");
		BrowserFrame html = new BrowserFrame();
		html.setSource(resource);
		html.setWidth("100%");
		html.setHeight("100%");
		this.addComponent(html);
		GridLayout gl = new GridLayout(3, 1);
		Button btn = addNavButton("Enter", "public/view2");
		gl.addComponent(btn, 1, 0);
		gl.setWidth("100%");
		gl.setColumnExpandRatio(0, 1);
		gl.setColumnExpandRatio(2, 1);
		this.addComponent(gl);
		this.setSizeFull();
		setExpandRatio(html, 0.75f);
		setExpandRatio(gl, 0.25f);

	}

	@Override
	protected void processParams(List<String> params) {

	}

	protected Button addNavButton(String caption, String uri) {
		Button button = new Button(caption);
		button.setData(uri);
		button.setDescription(uri);
		button.addStyleName(ChameleonTheme.BUTTON_TALL);
		button.addClickListener(this);
		return button;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button btn = event.getButton();
		String uri = (btn.getData() == null) ? null : btn.getData().toString();
		this.getScopedUI().getV7Navigator().navigateTo(uri);
	}

}
