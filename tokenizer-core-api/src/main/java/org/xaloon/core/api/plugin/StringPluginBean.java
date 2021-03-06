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
package org.xaloon.core.api.plugin;

/**
 * Simple value holder for plugin
 * 
 * @author vytautas r.
 * 
 */
public class StringPluginBean extends AbstractPluginBean {
    private static final long serialVersionUID = 1L;

    private String value;

    /**
     * @return plugin bean parameter value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            sets plugin bean parameter value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
