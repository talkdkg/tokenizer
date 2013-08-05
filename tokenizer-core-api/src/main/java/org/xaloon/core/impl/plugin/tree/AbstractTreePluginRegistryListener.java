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
package org.xaloon.core.impl.plugin.tree;

import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.inject.ServiceLocator;
import org.xaloon.core.api.plugin.PluginRegistry;
import org.xaloon.core.api.plugin.PluginRegistryListener;
import org.xaloon.core.api.resource.StringResourceLoader;

/**
 * Base class for tree observer, which is usually used to create menu trees depending on requirements. This class
 * prepares general initialization of
 * observer and then passes observable object to subclass.
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 * 
 * @see AbstractTreePlugin
 */

public abstract class AbstractTreePluginRegistryListener implements PluginRegistryListener {
    private static final long serialVersionUID = 1L;

    /**
     * Resource loader for current application. It is injected via spring IoC
     */
    @Inject
    private StringResourceLoader stringResourceLoader;

    @Inject
    @Named("pluginRegistry")
    private PluginRegistry pluginRegistry;

    /**
     * Returns injected plugin registry instance
     * 
     * @return plugin registry
     */
    public PluginRegistry getPluginRegistry() {
        return pluginRegistry;
    }

    /**
     * Returns string resource loader which is used in current application
     * 
     * @return resource loader is injected via default DI. It should not be null in normal flow, otherwise try to load
     *         using {@link ServiceLocator}
     */
    public StringResourceLoader getStringResourceLoader() {
        return stringResourceLoader;
    }
}
