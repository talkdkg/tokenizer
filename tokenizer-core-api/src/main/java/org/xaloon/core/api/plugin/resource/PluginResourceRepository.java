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
package org.xaloon.core.api.plugin.resource;

import java.io.Serializable;

import org.xaloon.core.api.plugin.AbstractPluginBean;
import org.xaloon.core.api.plugin.Plugin;

/**
 * Plugin configuration storage interface for plugin enabling/disabling feature, changing configuration parameters.
 * 
 * @author vytautas r.
 * @version 1.1, 10/06/10
 * @since 1.3
 */
public interface PluginResourceRepository extends Serializable {
    /**
     * Deletes plugin resource information from storage
     * 
     * @param plugin
     *            parameter which configuration should be deleted
     */
    void delete(Plugin plugin);

    /**
     * Returns plugin configuration bean for specified plugin
     * 
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @param plugin
     *            plugin object to lookup
     * @return concrete configuration plugin bean
     */
    <T extends AbstractPluginBean> T getPluginBean(Plugin plugin);

    /**
     * Sets new or updates current plugin configuration for specified plugin
     * 
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @param plugin
     *            plugin, which contains updatable configuration
     * @param pluginBean
     *            configuration, which should be stored into resource repository
     */
    <T extends AbstractPluginBean> void setPluginBean(Plugin plugin, T pluginBean);

    /**
     * Changes visibility of plugin.
     * 
     * @param plugin
     *            plugin object to check
     * @param enabled
     *            true - to enable plugin, false - to disable plugin
     */
    void setEnabled(Plugin plugin, boolean enabled);

    /**
     * Checks if plugin is enabled/disabled
     * 
     * @param plugin
     *            plugin object to lookup
     * @return true if plugin is enabled, false - otherwise
     */
    boolean isEnabled(Plugin plugin);
}
