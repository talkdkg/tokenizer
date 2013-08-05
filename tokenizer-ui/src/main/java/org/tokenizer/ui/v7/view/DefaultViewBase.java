/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
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
