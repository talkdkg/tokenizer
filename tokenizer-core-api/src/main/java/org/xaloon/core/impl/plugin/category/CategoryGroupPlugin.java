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
import org.xaloon.core.api.plugin.Plugin;
import org.xaloon.core.api.plugin.PluginType;
import org.xaloon.core.impl.plugin.tree.AbstractTreePlugin;

/**
 * Category grouping plugin, which stores plugins aggregated by plugin categories
 * 
 * @author vytautas r.
 */
@Named
public class CategoryGroupPlugin extends AbstractTreePlugin<AbstractPluginBean, Plugin> {
    private static final long serialVersionUID = 1L;

    /**
     * Plugin type is set to hidden by default.
     */
    public CategoryGroupPlugin() {
        setType(PluginType.HIDDEN);
    }
}
