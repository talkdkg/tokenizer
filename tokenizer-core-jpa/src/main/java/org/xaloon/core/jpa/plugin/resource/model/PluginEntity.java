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
package org.xaloon.core.jpa.plugin.resource.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.xaloon.core.jpa.model.AbstractEntity;

/**
 * @author vytautas r.
 */
@Cacheable
@Entity
@Table(name = "XAL_PLUGIN_ENTITY", uniqueConstraints = { @UniqueConstraint(columnNames = { "PLUGIN_KEY" }) })
public class PluginEntity extends AbstractEntity {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Column(name = "PLUGIN_ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "PLUGIN_DATA", nullable = false)
    @Lob
    private String pluginData;

    @Column(name = "PLUGIN_KEY", nullable = false)
    private String pluginKey;

    /**
     * Gets enabled.
     * 
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets enabled.
     * 
     * @param enabled
     *            enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return xml representation of plugin bean data
     */
    public String getPluginData() {
        return pluginData;
    }

    /**
     * @param pluginData
     */
    public void setPluginData(String pluginData) {
        this.pluginData = pluginData;
    }

    /**
     * @return plugin id
     */
    public String getPluginKey() {
        return pluginKey;
    }

    /**
     * @param pluginKey
     */
    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    @Override
    public String toString() {
        return String.format("[%s] pluginKey=%s, enabled=%b", this.getClass().getSimpleName(), getPluginKey(),
                isEnabled());
    }
}
