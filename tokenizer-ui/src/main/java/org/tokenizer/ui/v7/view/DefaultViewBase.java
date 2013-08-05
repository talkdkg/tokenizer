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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;

public abstract class DefaultViewBase implements V7View {
    private static Logger log = LoggerFactory.getLogger(DefaultViewBase.class);

    protected HorizontalSplitPanel layout;
    private final UserNavigationTree navtree;
    private final V7Navigator navigator;

    @Inject
    protected DefaultViewBase(V7Navigator navigator, UserNavigationTree navtree) {
        super();
        this.navtree = navtree;
        this.navigator = navigator;
        buildUI();
    }

    protected void buildUI() {
        layout = new HorizontalSplitPanel();
        layout.setSplitPosition(200f, Unit.PIXELS);
        layout.setFirstComponent(navtree);
        layout.setLocked(true);
    }

    @Override
    public void enter(V7ViewChangeEvent event) {
        log.debug("entered view: " + this.getClass().getSimpleName() + " with uri: " + navigator.getNavigationState());
        List<String> params = navigator.getNavigationParams();
        processParams(params);
    }

    @Override
    public Component getUiComponent() {
        return layout;
    }

    protected abstract void processParams(List<String> params);

    public V7Navigator getNavigator() {
        return navigator;
    }
}
