/*
 * Copyright (C) 2013 David Sowerby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.tokenizer.ui.v7.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.DefaultViewBase;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

import com.vaadin.ui.Label;

public class View1View extends DefaultViewBase implements V7View {
    private final Label label;

    @Inject
    protected View1View(V7Navigator navigator, UserNavigationTree navtree) {
        super(navigator, navtree);
        label = new Label(this.getClass().getName());
    }

    @Override
    protected void processParams(List<String> params) {
        // TODO Auto-generated method stub

    }

}
