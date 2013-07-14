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

import org.tokenizer.ui.base.guice.uiscope.UIKey;
import org.tokenizer.ui.base.guice.uiscope.UIScope;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.view.V7View;
import org.tokenizer.ui.base.view.V7ViewHolder;
import org.tokenizer.ui.i18n.I18NKeys;

import uk.co.q3c.v7.base.config.V7ConfigurationException;

import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

public abstract class ScopedUI extends UI implements V7ViewHolder {

	private UIKey instanceKey;
	private UIScope uiScope;
	private final Panel viewDisplayPanel;
	private final V7Navigator navigator;
	private final ErrorHandler errorHandler;
	private AbstractOrderedLayout screenLayout;
	private final ConverterFactory converterFactory;
	private V7View view;

	protected ScopedUI(V7Navigator navigator, ErrorHandler errorHandler, ConverterFactory converterFactory) {
		super();
		this.errorHandler = errorHandler;
		this.navigator = navigator;
		this.converterFactory = converterFactory;
		viewDisplayPanel = new Panel();
		viewDisplayPanel.setSizeFull();
	}

	public void setInstanceKey(UIKey instanceKey) {
		this.instanceKey = instanceKey;
	}

	public UIKey getInstanceKey() {
		return instanceKey;
	}

	@Override
	public void detach() {
		if (uiScope != null) {
			uiScope.releaseScope(this.getInstanceKey());
		}
		super.detach();
	}

	/**
	 * The Vaadin navigator has been replaced by the V7Navigator, use {@link #getV7Navigator()} instead.
	 * 
	 * @see com.vaadin.ui.UI#getNavigator()
	 */
	@Override
	@Deprecated
	public Navigator getNavigator() {
		return null;
	}

	public V7Navigator getV7Navigator() {
		return navigator;
	}

	@Override
	public void setNavigator(Navigator navigator) {
		throw new MethodReconfigured("UI.setNavigator() not available, use injection instead");
	}

	// TODO fromView serves no purpose
	@Override
	public void changeView(V7View fromView, V7View toView) {
		Component content = toView.getUiComponent();
		content.setSizeFull();
		viewDisplayPanel.setContent(content);
		this.view = toView;
	}

	public Panel getViewDisplayPanel() {
		return viewDisplayPanel;
	}

	/**
	 * Make sure you call this from sub-class overrides
	 * 
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {

		getSession().setConverterFactory(converterFactory);

		// page isn't available during injected construction
		Page page = getPage();
		page.addUriFragmentChangedListener(navigator);
		setErrorHandler(errorHandler);
		page.setTitle(pageTitle());
		doLayout();
		// Navigate to the correct start point
		String fragment = getPage().getUriFragment();
		getV7Navigator().navigateTo(fragment);

	}

	/**
	 * Uses the {@link #screenLayout} defined by sub-class implementations of {@link #screenLayout()}, expands it to
	 * full size, and sets the View display panel to take up all spare space.
	 */
	protected void doLayout() {
		if (screenLayout == null) {
			screenLayout = screenLayout();
		}
		screenLayout.setSizeFull();
		if (screenLayout.getComponentIndex(getViewDisplayPanel()) < 0) {
			String msg = "Your implementation of screenLayout() must include getViewDisplayPanel().  AS a minimum this could be 'return new VerticalLayout(getViewDisplayPanel())'";
			throw new V7ConfigurationException(msg);
		}
		screenLayout.setExpandRatio(getViewDisplayPanel(), 1);
		setContent(screenLayout);
	}

	/**
	 * Override this to provide your screen layout. In order for Views to work one child component of this layout must
	 * be provided by {@link #getViewDisplayPanel()}. The simplest example would be
	 * {@code return new VerticalLayout(getViewDisplayPanel()}, which would set the View to take up all the available
	 * screen space. {@link BasicUI} is an example of a UI which contains a header and footer bar.
	 * 
	 * @return
	 */
	protected abstract AbstractOrderedLayout screenLayout();

	public V7View getView() {
		return view;
	}

	/**
	 * Override to provide a title for your UI page This will appear in your browser tab. If this needs to be an I18N
	 * title, use {@link I18NKeys#getValue(java.util.Locale)} (see also the documentation at
	 * https://sites.google.com/site/q3cjava/internationalisation-i18n)
	 * 
	 * @return
	 */
	protected abstract String pageTitle();

}