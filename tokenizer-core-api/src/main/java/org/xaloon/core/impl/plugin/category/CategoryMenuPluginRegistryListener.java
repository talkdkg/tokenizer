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

import org.xaloon.core.api.path.DelimiterEnum;
import org.xaloon.core.api.plugin.Plugin;
import org.xaloon.core.api.util.DefaultKeyValue;
import org.xaloon.core.impl.plugin.tree.AbstractTreePluginRegistryListener;
import org.xaloon.core.impl.plugin.tree.GenericTreeNode;
import org.xaloon.core.impl.plugin.tree.MenuItem;

/**
 * Category node observer is used to get notifications when plugin is inserted into plugin registry and create tree node from category string
 * parameter For example we have two category strings: "/admin/utils", "/admin/test" Then there will be a tree created with such nodes: admin / \
 * utils test
 * 
 * This tree later will be used in administration panel to represent graphical menu of plugin categories
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */
@Named
public class CategoryMenuPluginRegistryListener extends AbstractTreePluginRegistryListener {
	private static final long serialVersionUID = 1L;

	private CategoryMenuPlugin categoryMenuPlugin;

	/**
	 * Creates Category tree into memory by provided string. category example: "/admin/utils"
	 * 
	 * @param category
	 *            string to parse for tree
	 * @return returns current tree node item
	 */
	private GenericTreeNode<MenuItem> registerCategoryTree(String category) {
		// take most inner category and create child from it
		GenericTreeNode<MenuItem> childTreeNode = getCategoryMenuPlugin().getTreeNodesByUrl().get(category);
		if (childTreeNode == null) {
			childTreeNode = createTreeNode(category);
			getCategoryMenuPlugin().getTreeNodesByUrl().put(category, childTreeNode);

			if (category.lastIndexOf(DelimiterEnum.SLASH.value()) > 0) {
				// traverse to parent category and add child to new parent
				category = category.substring(0, category.lastIndexOf(DelimiterEnum.SLASH.value()));
				GenericTreeNode<MenuItem> parent = registerCategoryTree(category);
				if (parent != null) {
					parent.addChild(childTreeNode);
				}
			} else {
				// got to the top. add child to root tree node
				getCategoryMenuPlugin().getTree().addChild(childTreeNode);
			}
		}
		return childTreeNode;
	}

	private GenericTreeNode<MenuItem> createTreeNode(String category) {
		GenericTreeNode<MenuItem> childTreeNode = new GenericTreeNode<MenuItem>();
		MenuItem menuItem = new MenuItem();
		String subCategory = category;
		if (subCategory.startsWith(DelimiterEnum.SLASH.value())) {
			subCategory = subCategory.substring(1);
		}
		String[] categories = subCategory.split(DelimiterEnum.SLASH.value());

		String stringKey = category.substring(category.lastIndexOf(DelimiterEnum.SLASH.value()) + 1);
		// Add the last category as a parameter
		menuItem.getParameters().add(new DefaultKeyValue<String, String>(CategoryConstants.PAGE_NAMED_PARAMETER_PARENT_CATEGORY, stringKey));

		menuItem.setKey(stringKey);
		menuItem.setPageClass(CategoryMenuPluginRegistryListener.class);
		childTreeNode.setData(menuItem);
		return childTreeNode;
	}

	public void onAfterPluginRegister(Plugin registeredPlugin) {
		registerCategoryTree(registeredPlugin.getCategory());
	}

	/**
	 * If somehow DI is not used properly then we try to inject using default xaloon solution
	 * 
	 * @return instance of {@link CategoryGroupPlugin}
	 */
	public CategoryMenuPlugin getCategoryMenuPlugin() {
		if (categoryMenuPlugin == null) {
			categoryMenuPlugin = getPluginRegistry().lookup(CategoryMenuPlugin.class);
		}
		return categoryMenuPlugin;
	}
}
