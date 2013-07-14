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
package org.tokenizer.ui.base.view.component;

import java.util.ArrayList;
import java.util.List;

import org.tokenizer.ui.base.navigate.SitemapNode;
import org.tokenizer.ui.base.navigate.SitemapURIConverter;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.view.V7ViewChangeEvent;
import org.tokenizer.ui.base.view.V7ViewChangeListener;
import org.tokenizer.ui.i18n.CurrentLocale;
import org.tokenizer.ui.i18n.I18NKeys;
import org.tokenizer.ui.i18n.I18NListener;
import org.tokenizer.ui.i18n.I18NTranslator;


import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class Breadcrumb extends HorizontalLayout implements I18NListener, V7ViewChangeListener, Button.ClickListener {

	private final List<BreadcrumbStep> steps = new ArrayList<>();
	private final V7Navigator navigator;
	private final SitemapURIConverter converter;
	private final CurrentLocale currentLocale;

	protected Breadcrumb(V7Navigator navigator, SitemapURIConverter converter, CurrentLocale currentLocale) {
		this.navigator = navigator;
		navigator.addViewChangeListener(this);
		this.converter = converter;
		this.currentLocale = currentLocale;
		moveToNavigationState();
	}

	private void moveToNavigationState() {
		List<SitemapNode> nodeChain = converter.nodeChainForUri(navigator.getNavigationState(), true);
		int maxIndex = (nodeChain.size() > steps.size() ? nodeChain.size() : steps.size());
		for (int i = 0; i < maxIndex; i++) {
			// nothing left in chain
			if (i + 1 > nodeChain.size()) {
				// but steps still exist
				if (i < steps.size()) {
					steps.get(i).setVisible(false);
				}
			} else {
				// chain continues
				BreadcrumbStep step = null;
				// steps still exist, re-use
				if (i < steps.size()) {
					step = steps.get(i);
				} else {
					// create step
					step = new BreadcrumbStep();
					step.addClickListener(this);
					steps.add(step);
				}
				setupStep(step, nodeChain.get(i));
			}

		}
	}

	private void setupStep(BreadcrumbStep step, SitemapNode sitemapNode) {
		// TODO can label translate be removed? May be done in build of sitemap later

		step.setNode(sitemapNode);
		I18NKeys<?> key = (I18NKeys<?>) step.getNode().getLabelKey();
		sitemapNode.setLabel(key.getValue(currentLocale.getLocale()));

		step.setVisible(true);
		step.setNode(sitemapNode);

	}

	@Override
	public void localeChange(I18NTranslator translator) {
		for (BreadcrumbStep step : steps) {
			I18NKeys<?> key = (I18NKeys<?>) step.getNode().getLabelKey();
			step.setCaption(key.getValue(translator.getLocale()));
		}
	}

	@Override
	public boolean beforeViewChange(V7ViewChangeEvent event) {
		// do nothing
		return true;
	}

	@Override
	public void afterViewChange(V7ViewChangeEvent event) {
		moveToNavigationState();
	}

	@Override
	public void detach() {
		navigator.removeViewChangeListener(this);
		super.detach();

	}

	public List<BreadcrumbStep> getSteps() {
		return steps;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		BreadcrumbStep step = (BreadcrumbStep) event.getButton();
		navigator.navigateTo(step.getNode());

	}

}
