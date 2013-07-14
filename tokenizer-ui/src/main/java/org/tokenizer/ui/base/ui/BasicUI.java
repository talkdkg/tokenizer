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
package org.tokenizer.ui.base.ui;

import javax.inject.Inject;

import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.demo.view.components.FooterBar;
import org.tokenizer.ui.demo.view.components.HeaderBar;
import org.tokenizer.ui.demo.view.components.InfoBar;


import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

// @PreserveOnRefresh
@Theme("chameleon")
public class BasicUI extends ScopedUI {

	private final HeaderBar headerBar;

	private final FooterBar footerBar;

	private final InfoBar infoBar;

	@Inject
	protected BasicUI(HeaderBar headerBar, FooterBar footerBar, InfoBar infoBar, V7Navigator navigator,
			ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super(navigator, errorHandler, converterFactory);
		this.footerBar = footerBar;
		this.headerBar = headerBar;
		this.infoBar = infoBar;

	}

	@Override
	protected AbstractOrderedLayout screenLayout() {
		return new VerticalLayout(headerBar, infoBar, getViewDisplayPanel(), footerBar);
	}

	public HeaderBar getHeaderBar() {
		return headerBar;
	}

	public FooterBar getFooterBar() {
		return footerBar;
	}

	@Override
	protected String pageTitle() {
		return "V7";
	}

}