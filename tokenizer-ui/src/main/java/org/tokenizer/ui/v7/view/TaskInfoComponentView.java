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

import uk.co.q3c.util.ID;
import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.view.ViewBase;

public class TaskInfoComponentView extends ViewBase {
    
    @Inject
    protected TaskInfoComponentView(V7Navigator navigator, TaskInfoComponent component) {
        super(navigator);
        component.setSizeFull();
        rootComponent = component;
        rootComponent.setId(ID.getId(this, rootComponent));
    }
    
    @Override
    protected void buildView() {}
    
    @Override
    protected void processParams(List<String> params) {}
    
    protected void setIds() {
//        rootComponent.setId(ID.getId(this, rootComponent));
    }

}
