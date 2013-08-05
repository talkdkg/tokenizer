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
package org.xaloon.core.impl.plugin;

import org.xaloon.core.api.plugin.PluginBeanSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author vytautas r.
 */
public class DefaultPluginBeanSerializer implements PluginBeanSerializer {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** Static converter to XML and back */
    private static final XStream XSTREAM = new XStream(new DomDriver());

    @Override
    public <T> String serialize(T pluginBean) {
        return XSTREAM.toXML(pluginBean);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(String pluginBeanData) {
        try {
            return (T) XSTREAM.fromXML(pluginBeanData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
