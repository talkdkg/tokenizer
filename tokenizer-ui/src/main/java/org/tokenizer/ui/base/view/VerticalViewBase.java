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

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.base.ui.ScopedUI;


import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public abstract class VerticalViewBase extends VerticalLayout implements V7View {
	private static Logger log = LoggerFactory.getLogger(VerticalViewBase.class);

	@Inject
	protected VerticalViewBase() {
		super();
	}

	@Override
	public void enter(V7ViewChangeEvent event) {
		log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: "
				+ event.getNavigator().getNavigationState());
		List<String> params = event.getNavigator().geNavigationParams();
		processParams(params);
	}

	/**
	 * typecasts and returns getUI()
	 * 
	 * @return
	 */

	public ScopedUI getScopedUI() {
		return (ScopedUI) getUI();
	}

	protected abstract void processParams(List<String> params);

	@Override
	public Component getUiComponent() {
		return this;
	}

}
