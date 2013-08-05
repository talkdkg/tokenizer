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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.inject.ServiceLocator;
import org.xaloon.core.api.security.AuthorityFacade;

/**
 * @author vytautas r.
 */
@Named
public class PluginLoader implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

    @Inject
    @Named("pluginRegistry")
    private PluginRegistry pluginRegistry;

    @Inject
    private AuthorityFacade authorityFacade;

    private List<Plugin> loadedPlugins;

    private final List<Plugin> userPlugins = new ArrayList<Plugin>();

    private final List<Plugin> hiddenPlugins = new ArrayList<Plugin>();

    /**
     * Loads system plugins and should be used as a first invocation
     */
    public void loadSystemPlugins() {
        if (loadedPlugins == null) {
            loadPlugins();
        }
        registerPlugins(hiddenPlugins);
    }

    private void registerPlugins(List<Plugin> pluginsToRegister) {
        for (Plugin plugin : pluginsToRegister) {
            try {
                pluginRegistry.lookup(plugin.getClass());
                LOGGER.warn(String.format("Plugin '%s' is already registered. Ignoring ...", plugin.getClass()
                        .getName()));
            } catch (PluginNotFoundException e) {
                pluginRegistry.register(plugin);
                authorityFacade.registerRoles(plugin);
            }
        }
    }

    /**
     * Loads user defined plugins
     */
    public void loadUserPlugins() {
        if (loadedPlugins == null) {
            loadPlugins();
        }
        registerPlugins(userPlugins);
    }

    private void loadPlugins() {
        loadedPlugins = ServiceLocator.get().getInstances(Plugin.class);
        for (Plugin plugin : loadedPlugins) {
            if (PluginType.HIDDEN.equals(plugin.getType())) {
                hiddenPlugins.add(plugin);
            }
            else {
                userPlugins.add(plugin);
            }
        }
    }
}
