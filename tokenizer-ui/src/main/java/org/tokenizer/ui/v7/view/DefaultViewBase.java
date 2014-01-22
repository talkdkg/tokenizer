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
import uk.co.q3c.v7.base.view.ViewBase;
import uk.co.q3c.v7.base.view.component.UserNavigationTree;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;

public class DefaultViewBase extends ViewBase {

    private static Logger log = LoggerFactory.getLogger(DefaultViewBase.class);

    protected HorizontalSplitPanel layout;

    UserNavigationTree navtree;
    
    @Inject
    public DefaultViewBase(V7Navigator navigator, UserNavigationTree navtree) {
        super(navigator);
        this.navtree = navtree;
    }

    /**
    *****************************************************************************
    * Description.
    * 
    * @see uk.co.q3c.v7.base.view.ViewBase#buildView()
    *****************************************************************************
    */
    @Override
    protected void buildView()
    {
            layout = new HorizontalSplitPanel();
            layout.setSplitPosition(200f, Unit.PIXELS);
            layout.setFirstComponent(navtree);
            layout.setLocked(true);
    }

    /**
    *****************************************************************************
    * Description.
    * 
    * @param params
    * @see uk.co.q3c.v7.base.view.ViewBase#processParams(java.util.List)
    *****************************************************************************
    */
    @Override
    protected void processParams(List<String> params)
    {
        // TODO Auto-generated method stub
        
    }

 
}
