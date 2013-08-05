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

/**
 * 
 * 
 * @author David Sowerby 9 Feb 2013
 * 
 */
public class DemoLabels_de extends DemoLabels {

    private static final EnumMap<DemoLabelKeys, String> map = new EnumMap<DemoLabelKeys, String>(DemoLabelKeys.class);
    // TODO make map unmodifiable
    static {
        map.put(DemoLabelKeys.Yes, "ja");
        map.put(DemoLabelKeys.No, "nein");

    }

    @Override
    public EnumMap<DemoLabelKeys, String> getMap() {
        return map;
    }

}
