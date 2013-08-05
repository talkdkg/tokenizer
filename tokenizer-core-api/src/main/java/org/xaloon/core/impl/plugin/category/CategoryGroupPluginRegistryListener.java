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

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.path.DelimiterEnum;
import org.xaloon.core.api.plugin.Plugin;
import org.xaloon.core.api.plugin.PluginType;
import org.xaloon.core.impl.plugin.tree.AbstractTreePluginRegistryListener;
import org.xaloon.core.impl.plugin.tree.GenericTreeNode;

/**
 * 
 * @author vytautas r.
 * 
 */
@Named
public class CategoryGroupPluginRegistryListener extends AbstractTreePluginRegistryListener {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private CategoryGroupPlugin categoryGroupPlugin;

    private void insertIntoTree(String category, GenericTreeNode<Plugin> child, Plugin plugin) {
        if (!StringUtils.isEmpty(category)) {
            GenericTreeNode<Plugin> parentNode = getCategoryGroupPlugin().getTreeNodesByContext().get(category);
            if (parentNode == null) {
                parentNode = new GenericTreeNode<Plugin>();
                getCategoryGroupPlugin().getTreeNodesByContext().put(category, parentNode);
            }
            parentNode.addChild(child);
            insertIntoTree(category.substring(0, category.lastIndexOf(DelimiterEnum.SLASH.value())), child, plugin);
        }
        else {
            getCategoryGroupPlugin().getTree().addChild(child);
        }
    }

    private GenericTreeNode<Plugin> createMenuItem(Plugin plugin) {
        GenericTreeNode<Plugin> resultNode = new GenericTreeNode<Plugin>();
        resultNode.setData(plugin);

        return resultNode;
    }

    public void onAfterPluginRegister(Plugin registeredPlugin) {
        if (PluginType.HIDDEN.equals(registeredPlugin.getType())) {
            return;
        }
        GenericTreeNode<Plugin> child = createMenuItem(registeredPlugin);
        insertIntoTree(registeredPlugin.getCategory(), child, registeredPlugin);
    }

    /**
     * If somehow DI is not used properly then we try to inject using default xaloon solution
     * 
     * @return instance of {@link CategoryGroupPlugin}
     */
    public CategoryGroupPlugin getCategoryGroupPlugin() {
        if (categoryGroupPlugin == null) {
            categoryGroupPlugin = getPluginRegistry().lookup(CategoryGroupPlugin.class);
        }
        return categoryGroupPlugin;
    }
}
