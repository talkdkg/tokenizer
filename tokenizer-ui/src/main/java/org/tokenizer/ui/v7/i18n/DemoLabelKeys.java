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
package org.tokenizer.ui.v7.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import uk.co.q3c.v7.i18n.I18NKey;

public enum DemoLabelKeys implements I18NKey<DemoLabels> {

    _nullkey_, Yes, No, View1, View2, Home, Private, Public, Reset_Account, Logout, Unlock_Account, Enable_Account, Login, Refresh_Account, Request_Account, TaskInfoComponent, UrlSearchComponent;

    @Override
    public DemoLabels getBundle(final Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(DemoLabels.class.getName(), locale);
        return (DemoLabels) bundle;
    }

    @Override
    public String getValue(final Locale locale) {
        String mapValue = getBundle(locale).getValue(this);
        if (mapValue == null) {
            return this.name().replace("_", " ");
        }
        else {
            return mapValue;
        }

    }

    @Override
    public boolean isNullKey() {
        return this.equals(_nullkey_);
    }
}
