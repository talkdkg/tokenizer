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

import org.tokenizer.ui.demo.view.DemoViewBase;
import org.tokenizer.ui.demo.view.components.FooterBar;
import org.tokenizer.ui.demo.view.components.HeaderBar;


import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ChameleonTheme;

public class DefaultErrorView extends DemoViewBase implements ErrorView {

	@Inject
	protected DefaultErrorView(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		Button button = addNavButton("take me home", home);
		button.addStyleName(ChameleonTheme.BUTTON_TALL);
		getViewLabel().addStyleName("warning");

	}

	@Override
	public void processParams(List<String> params) {
		String s = "This is the ErrorView and would say something like \""
				+ this.getScopedUI().getV7Navigator().getNavigationState() + " is not a valid uri\"";
		getViewLabel().setValue(s);
	}

}
