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
 * Simple key value plugin bean
 * 
 * @author vytautas r.
 * 
 */
public class KeyValuePluginBean extends StringPluginBean {
    private static final long serialVersionUID = 1L;

    private String key;

    /**
     * @return parameter name
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            sets parameter name
     */
    public void setKey(String key) {
        this.key = key;
    }
}
