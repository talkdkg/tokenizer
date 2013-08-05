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

import java.util.EnumMap;

import uk.co.q3c.v7.i18n.EnumResourceBundle;

/**
 * The base for the resource bundle of Labels. This is an arbitrary division of
 * i18N keys & values, but is loosely defined as containing those value which
 * are short, contain no parameters and are typically used for captions and
 * labels. They can of course be used anywhere.
 * 
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class DemoLabels extends EnumResourceBundle<DemoLabelKeys> {

    private static final EnumMap<DemoLabelKeys, String> map = new EnumMap<DemoLabelKeys, String>(DemoLabelKeys.class);

    static {
        map.put(DemoLabelKeys.Yes, "yes");
        map.put(DemoLabelKeys.No, "no");

    }

    @Override
    public EnumMap<DemoLabelKeys, String> getMap() {
        return map;
    }

}
