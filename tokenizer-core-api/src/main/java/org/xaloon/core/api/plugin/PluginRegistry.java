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
import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.config.Configuration;
import org.xaloon.core.api.exception.CreateClassInstanceException;
import org.xaloon.core.api.plugin.resource.PluginResourceRepository;
import org.xaloon.core.api.util.ClassUtil;
import org.xaloon.core.api.util.TextUtil;

/**
 * 
 * Plugin registration in application memory. Plugins are registered every time application is started up.
 * 
 * <pre>
 * PluginRegistry registry = PluginRegistry.setPluginRegistry(new DefaultMemoryPluginRegistry());
 * </pre>
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */
public abstract class PluginRegistry implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginRegistry.class);

    private static final String PLUGIN_TEXT = " plugin";

    @Inject
    private PluginResourceRepository pluginResourceRepository;

    /**
     * Construct.
     */
    public PluginRegistry() {
    }

    /**
     * Returns concrete plugin from registry by it's class
     * 
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @param pluginClassToLookup
     *            class to lookup
     * @return concrete plugin instance
     * @throws IllegalArgumentException
     *             when parameter is not provided
     * @throws PluginNotFoundException
     *             when plugin was not found
     */
    public abstract <T> T lookup(Class<?> pluginClassToLookup);

    /**
     * Return all existing plugins from registry
     * 
     * @return list of plugins. empty list if not found
     */
    public abstract Collection<Plugin> getAllPlugins();

    /**
     * Complete registration of plugin
     * 
     * @param pluginToRegister
     *            to register
     * @return registered plugin
     */
    protected abstract Plugin onRegister(Plugin pluginToRegister);

    /**
     * Complete unregister of concrete plugin
     * 
     * @param plugin
     *            to unregister
     */
    protected abstract void onUnregister(Plugin plugin);

    /**
     * Registers plugin into global registry
     * 
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @param pluginToRegister
     *            to register
     * @return registered plugin
     * @throws CreateClassInstanceException
     *             if new instance of plugin bean could not be created
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractPluginBean> Plugin register(Plugin pluginToRegister) {
        if (pluginToRegister == null) {
            return null;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Registering plugin: " + pluginToRegister.getClass().getName());
        }
        // set plugin name to default if it is not provided
        if (StringUtils.isEmpty(pluginToRegister.getName())) {
            pluginToRegister.setName(TextUtil.parseName(pluginToRegister.getClass().getName() + PLUGIN_TEXT));
        }

        // set plugin Id to class name if Id is not provided
        if (StringUtils.isEmpty(pluginToRegister.getId())) {
            pluginToRegister.setId(pluginToRegister.getClass().getName());
        }

        // set plugin type to visible by default
        if (StringUtils.isEmpty(pluginToRegister.getType())) {
            pluginToRegister.setType(PluginType.VISIBLE);
        }

        // set default property values
        if (getPluginBean(pluginToRegister) == null) {
            try {
                Class<?> pluginBeanClass = ClassUtil.getClassGenericType(pluginToRegister.getClass(), 0,
                        AbstractPluginBean.class);
                if (!Plugin.class.getName().equals(pluginBeanClass.getName())) {
                    setPluginBean(pluginToRegister, (T) ClassUtil.newInstance(pluginBeanClass));
                    setEnabled(pluginToRegister, true);
                }
            } catch (CreateClassInstanceException e) {
                LOGGER.error("Could not create plugin bean instance for plugin: "
                        + pluginToRegister.getClass().getName(), e);
            }
        }

        Plugin result = onRegister(pluginToRegister);
        if (result != null && result.getCategory() != null) {
            Configuration.get().getPluginRegistryListenerCollection().onAfterPluginRegister(result);
        }
        return result;
    }

    /**
     * Unregister plugin from registry. Remove plugin information from plugin resource repository and then remove plugin
     * from registry.
     * 
     * @param plugin
     *            plugin object to remove from registry
     */
    public void unregister(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        getPluginResourceRepository().delete(plugin);
        onUnregister(plugin);
    }

    /**
     * Create instance of plugin administration panel
     * 
     * @param plugin
     *            plugin object for which plugin to create administration form
     * @param id
     *            wicket id to pass
     * @return administration panel instance
     * @throws CreateClassInstanceException
     *             on failure when creating object instance
     */
    public Object createAdministrationForm(Plugin plugin, String id) throws CreateClassInstanceException {
        Class<?> classToCreate = plugin.getAdministratorFormClass();
        if (classToCreate == null) {
            return null;
        }
        return ClassUtil.newInstance(classToCreate, new Class<?>[] { String.class }, new Object[] { id });
    }

    /**
     * Checks if plugin is enabled/disabled
     * 
     * @param plugin
     *            plugin object to lookup
     * @return true if plugin is enabled, false - otherwise
     */
    public boolean isEnabled(Plugin plugin) {
        return getPluginResourceRepository().isEnabled(plugin);
    }

    /**
     * Validate if plugin is enabled by it's class. Lookup for a plugin first and later check if it is enabled
     * 
     * @param pluginClass
     *            to validate
     * @return true if plugin is enabled or not found, false otherwise
     */
    public boolean isEnabled(Class<?> pluginClass) {
        try {
            Plugin plugin = lookup(pluginClass);
            return isEnabled(plugin);
        } catch (PluginNotFoundException e) {
            LOGGER.error("Plugin exception while trying to check if it is enabled.", e);

        }
        return false;
    }

    /**
     * Changes visibility of plugin.
     * 
     * @param plugin
     *            plugin, which contains updatable configuration
     * @param enabled
     */
    public void setEnabled(Plugin plugin, boolean enabled) {
        getPluginResourceRepository().setEnabled(plugin, enabled);
    }

    /**
     * Sets new or updates current plugin configuration for specified plugin
     * 
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @param plugin
     *            plugin, which contains updatable configuration
     * @param pluginBean
     *            configuration, which should be stored into resource repository
     * @return stored plugin bean
     */
    public <T extends AbstractPluginBean> T setPluginBean(Plugin plugin, T pluginBean) {
        getPluginResourceRepository().setPluginBean(plugin, pluginBean);
        return pluginBean;
    }

    /**
     * @param plugin
     *            plugin object to lookup
     * @param <T>
     *            concrete implementation of configuration bean, which extends {@link AbstractPluginBean}
     * @return concrete configuration plugin bean
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractPluginBean> T getPluginBean(Plugin plugin) {
        return (T) getPluginResourceRepository().getPluginBean(plugin);
    }

    /**
     * 
     * @param <T>
     *            plugin bean type
     * @param pluginClass
     *            plugin class to lookup
     * @return plugin bean instance
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractPluginBean> T getPluginBean(Class<?> pluginClass) {
        Plugin plugin = lookup(pluginClass);
        return (T) getPluginBean(plugin);
    }

    /**
     * If somehow DI is not used properly then we try to inject using default xaloon solution
     * 
     * @return instance of {@link PluginResourceRepository}
     */
    public PluginResourceRepository getPluginResourceRepository() {
        return pluginResourceRepository;
    }
}
