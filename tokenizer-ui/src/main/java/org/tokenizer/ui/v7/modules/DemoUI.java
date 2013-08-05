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

import javax.inject.Inject;

import uk.co.q3c.v7.base.navigate.V7Navigator;
import uk.co.q3c.v7.base.ui.ScopedUI;

import com.vaadin.annotations.Push;
import com.vaadin.data.util.converter.ConverterFactory;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.VerticalLayout;

@Push(PushMode.AUTOMATIC)
public class DemoUI extends ScopedUI implements Broadcaster.BroadcastListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private VerticalLayout layout;

    @Inject
    protected DemoUI(final V7Navigator navigator, final ErrorHandler errorHandler, final ConverterFactory converterFactory) {
        super(navigator, errorHandler, converterFactory);
    }

    @Override
    protected String pageTitle() {
        return "Tokenizer: Vertical Search";
    }

    @Override
    protected AbstractOrderedLayout screenLayout() {
        if (layout == null) {
            Banner banner = new Banner();
            layout = new VerticalLayout(banner, getViewDisplayPanel());
        }
        return layout;
    }

    @Override
    protected void init(final VaadinRequest request) {
        Broadcaster.register(this);

        // UI.getCurrent().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

        // The background thread that updates clock times once every second.
        // new Timer().scheduleAtFixedRate(new TimerTask() {
        // @Override
        // public void run() {
        // final Date currentTime = getCurrentUtcTime();
        // for (final PushClock pushClock : getActiveClocks()) {
        // try {
        // pushClock.getUI().access(new Runnable() {
        // @Override
        // public void run() {
        // pushClock.updateTime(currentTime);
        // }
        // });
        // } catch (final UIDetachedException e) {
        // // Ignore
        // }
        // }
        // }
        // }, new Date(), 1000);

        super.init(request);
    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @Override
    public void receiveBroadcast(final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                   //
            }
        });
    }

}
