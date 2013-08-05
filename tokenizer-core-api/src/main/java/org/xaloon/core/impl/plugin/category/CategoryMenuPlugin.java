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
package org.xaloon.core.impl.plugin.category;

import javax.inject.Named;

import org.xaloon.core.api.plugin.AbstractPluginBean;
import org.xaloon.core.api.plugin.PluginType;
import org.xaloon.core.impl.plugin.tree.AbstractTreePlugin;
import org.xaloon.core.impl.plugin.tree.MenuItem;

/**
 * Category plugin which contains category tree in memory. Category tree is constructed while
 * {@link CategoryMenuPluginRegistryListener} is invoked.
 * It should be created on application startup and do not change in runtime. This plugin will be used in administration
 * plugin to show plugin
 * hierarchy by category
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */
@Named
public class CategoryMenuPlugin extends AbstractTreePlugin<AbstractPluginBean, MenuItem> {
    private static final long serialVersionUID = 1L;

    /**
     * Plugin type is set to hidden by default.
     */
    public CategoryMenuPlugin() {
        setType(PluginType.HIDDEN);
    }
}
