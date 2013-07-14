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

import org.tokenizer.ui.base.data.V7DefaultConverterFactory;
import org.tokenizer.ui.base.navigate.DefaultV7Navigator;
import org.tokenizer.ui.base.navigate.StrictURIFragmentHandler;
import org.tokenizer.ui.base.navigate.URIFragmentHandler;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.shiro.LoginStatusMonitor;
import org.tokenizer.ui.base.view.component.LoginStatusPanel;
import org.tokenizer.ui.demo.ui.SideBarUI;


import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;

public abstract class V7UIModule extends AbstractModule {

	@Override
	protected void configure() {
		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);

		bind(WebBrowser.class).toProvider(BrowserProvider.class);

		bindUIProvider();
		addUIBindings(mapbinder);
		bindNavigator();
		bindURIHandler();
		bindConverterFactory();
		bindLoginStatusMonitor();
	}

	private void bindConverterFactory() {
		bind(ConverterFactory.class).to(V7DefaultConverterFactory.class);
	}

	/**
	 * Override to bind your choice of LoginStatusMonitor
	 */
	protected void bindLoginStatusMonitor() {
		bind(LoginStatusMonitor.class).to(LoginStatusPanel.class);
	}

	/**
	 * Override to bind your ScopedUIProvider implementation
	 */
	protected abstract void bindUIProvider();

	/**
	 * Override to bind your choice of URI handler
	 */
	protected void bindURIHandler() {
		bind(URIFragmentHandler.class).to(StrictURIFragmentHandler.class);
	}

	protected void bindNavigator() {
		bind(V7Navigator.class).to(DefaultV7Navigator.class);
	}

	/**
	 * Override with your UI bindings
	 * 
	 * @param mapbinder
	 */
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
		mapbinder.addBinding(BasicUI.class.getName()).to(BasicUI.class);
		mapbinder.addBinding(SideBarUI.class.getName()).to(SideBarUI.class);
	}

}
