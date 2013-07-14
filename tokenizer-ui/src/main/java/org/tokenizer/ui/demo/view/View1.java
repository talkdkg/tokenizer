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

import javax.inject.Inject;

import org.tokenizer.ui.demo.view.components.FooterBar;
import org.tokenizer.ui.demo.view.components.HeaderBar;


public class View1 extends DemoViewBase {

	@Inject
	protected View1(FooterBar footerBar, HeaderBar headerBar) {
		super(footerBar, headerBar);
		addNavButton("view 2", view2);
		addNavButton("view 2 with parameters", view2 + "/id=22");
		addNavButton("home", home);
		addNavButton("home with parameters", home + "/id=2/age=15");
		addNavButton("invalid uri", "view3/id=22");
	}

}
