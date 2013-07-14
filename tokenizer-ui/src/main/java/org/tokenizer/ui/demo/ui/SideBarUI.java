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
package org.tokenizer.ui.demo.ui;

import javax.inject.Inject;

import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.ui.BasicUI;
import org.tokenizer.ui.demo.view.components.FooterBar;
import org.tokenizer.ui.demo.view.components.HeaderBar;
import org.tokenizer.ui.demo.view.components.InfoBar;


import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ChameleonTheme;

@PreserveOnRefresh
public class SideBarUI extends BasicUI {

	private TextArea textArea;

	@Inject
	protected SideBarUI(HeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(headerBar, footerBar, infoBar, navigator, errorHandler, converterFactory);
	}

	@Override
	protected void doLayout() {
		// viewArea is the layout where Views will be placed

		HorizontalLayout centreSection = new HorizontalLayout();
		centreSection.setSizeFull();

		Panel sideBarPanel = new Panel("Sidebar");
		sideBarPanel.addStyleName(ChameleonTheme.PANEL_BUBBLE);
		VerticalLayout panelLayout = new VerticalLayout();
		sideBarPanel.setContent(panelLayout);
		textArea = new TextArea();
		textArea.setValue("This sidebar does nothing,except demonstrate the use of two UIs.  See the javadoc for "
				+ DemoUIProvider.class.getSimpleName());
		textArea.setWidth("150px");
		panelLayout.addComponent(textArea);
		sideBarPanel.setSizeUndefined();
		sideBarPanel.setHeight("100%");

		centreSection.addComponent(sideBarPanel);
		centreSection.addComponent(getViewDisplayPanel());

		VerticalLayout screenLayout = new VerticalLayout(getHeaderBar(), centreSection, getFooterBar());
		screenLayout.setSizeFull();
		screenLayout.setExpandRatio(centreSection, 1);
		centreSection.setExpandRatio(getViewDisplayPanel(), 1);
		setContent(screenLayout);
	}

	public TextArea getTextArea() {
		return textArea;
	}

}