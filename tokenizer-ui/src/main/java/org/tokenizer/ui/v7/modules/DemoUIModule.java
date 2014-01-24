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
package org.tokenizer.ui.v7.modules;

import uk.co.q3c.v7.base.ui.V7UIModule;

import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class DemoUIModule extends V7UIModule {
    @Override
    protected void bindUIProvider() {
        bind(UIProvider.class).to(DemoUIProvider.class);
    }

    @Override
    protected void addUIBindings(MapBinder<String, UI> mapbinder) {
        //super.addUIBindings(mapbinder);
        mapbinder.addBinding(DemoUI.class.getName()).to(DemoUI.class);
    }
}
