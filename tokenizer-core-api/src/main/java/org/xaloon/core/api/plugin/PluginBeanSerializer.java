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

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public interface PluginBeanSerializer extends Serializable {
    /**
     * @param <T>
     * @param pluginBean
     * @return string format of plugin bean data, usually XML formatted
     */
    <T> String serialize(T pluginBean);

    /**
     * @param <T>
     * @param pluginBeanData
     * @return instance of plugin bean, converted from parameter
     */
    <T> T deserialize(String pluginBeanData);
}
