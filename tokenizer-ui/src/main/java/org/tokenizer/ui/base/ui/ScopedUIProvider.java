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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.base.guice.uiscope.UIKey;
import org.tokenizer.ui.base.guice.uiscope.UIKeyProvider;
import org.tokenizer.ui.base.guice.uiscope.UIScope;
import org.tokenizer.ui.base.guice.uiscope.UIScoped;


import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * A Vaadin UI provider which supports the use of Guice scoped UI (see {@link UIScoped}). If you do not need UIScope,
 * then just extend from UIProvider directly
 * 
 * Subclasses should implement getUIClass(UIClassSelectionEvent event) to provide logic for selecting the UI class.
 * <p>
 * <b>Note:</b>Do not try and inject any {@link UIScoped} dependencies
 * 
 * @author David Sowerby
 * 
 */
public abstract class ScopedUIProvider extends UIProvider {
	private static Logger log = LoggerFactory.getLogger(ScopedUIProvider.class);
	private final UIKeyProvider uiKeyProvider;
	private final Map<String, Provider<UI>> uiProMap;
	private final Injector injector;

	@Inject
	protected ScopedUIProvider(Injector injector, Map<String, Provider<UI>> uiProMap, UIKeyProvider uiKeyProvider) {
		super();
		this.uiKeyProvider = uiKeyProvider;
		this.uiProMap = uiProMap;
		this.injector = injector;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		return createInstance(event.getUIClass());

	}

	public UI createInstance(Class<? extends UI> uiClass) {
		UIKey uiKey = uiKeyProvider.get();
		// hold the key while UI is created
		CurrentInstance.set(UIKey.class, uiKey);
		// and set up the scope
		UIScope.getCurrent().startScope(uiKey);

		// create the UI
		Provider<UI> uiProvider = uiProMap.get(uiClass.getName());
		if (uiProvider == null) {
			throw new UIProviderException("No provider has been specified for " + uiClass.getName());
		}
		ScopedUI ui = (ScopedUI) uiProvider.get();
		ui.setInstanceKey(uiKey);
		log.debug("returning instance of " + ui.getClass().getName() + " with key " + uiKey);
		return ui;
	}

}
