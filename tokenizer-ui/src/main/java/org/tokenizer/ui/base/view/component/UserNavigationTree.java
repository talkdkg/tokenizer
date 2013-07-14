/*
 * Copyright 2013 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.ui.base.view.component;

import java.util.List;

import javax.inject.Inject;

import org.tokenizer.ui.base.guice.uiscope.UIScoped;
import org.tokenizer.ui.base.navigate.Sitemap;
import org.tokenizer.ui.base.navigate.SitemapNode;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.i18n.CurrentLocale;
import org.tokenizer.ui.i18n.I18NKeys;


import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

/**
 * A navigation tree for users to find their way around the site. Uses {@link Sitemap} as the site structure. This is
 * naturally a {@link UIScoped} class, as it makes sense for one instance to be in use per browser tab
 * 
 * @author David Sowerby 17 May 2013
 * 
 */
@UIScoped
public class UserNavigationTree extends Tree {

	private final CurrentLocale currentLocale;
	private final Sitemap sitemap;
	private int maxLevel = -1;
	private int level;
	private final V7Navigator navigator;

	@Inject
	protected UserNavigationTree(Sitemap sitemap, CurrentLocale currentLocale, V7Navigator navigator) {
		super();
		this.sitemap = sitemap;
		this.currentLocale = currentLocale;
		this.navigator = navigator;
		setImmediate(true);
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		addValueChangeListener(this);
		loadNodes();

	}

	private void loadNodes() {
		this.removeAllItems();
		List<SitemapNode> nodeList = sitemap.getRoots();

		for (SitemapNode node : nodeList) {
			level = 1;
			loadNode(null, node);
		}
	}

	private void loadNode(SitemapNode parentNode, SitemapNode childNode) {
		this.addItem(childNode);
		I18NKeys<?> key = (I18NKeys<?>) childNode.getLabelKey();

		String caption = key.getValue(currentLocale.getLocale());
		this.setItemCaption(childNode, caption);
		setParent(childNode, parentNode);

		SitemapNode newParentNode = childNode;
		level++;

		if ((maxLevel < 0) || (level <= maxLevel)) {
			List<SitemapNode> children = sitemap.getChildren(newParentNode);
			if (children.size() == 0) {
				// no children, visual tree should not allow expanding the node
				setChildrenAllowed(newParentNode, false);
			}
			for (SitemapNode child : children) {
				loadNode(newParentNode, child);
			}

		} else {
			// no children, visual tree should not allow expanding the node
			setChildrenAllowed(newParentNode, false);
		}

	}

	/**
	 * Returns true if the {@code node} is a leaf as far as this {@link UserNavigationTree} is concerned. It may be a
	 * leaf here, but not in the {@link #sitemap}, depending on the setting of {@link #maxLevel}
	 * 
	 * @param node
	 * @return
	 */
	public boolean isLeaf(SitemapNode node) {
		return !areChildrenAllowed(node);
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * Set the maximum level or depth of the tree you want to be visible. 0 is not allowed, and is ignored. Set to < 0
	 * if you want this tree to display the full {@link #sitemap}
	 * 
	 * @param level
	 */
	public void setMaxLevel(int maxLevel) {
		if (maxLevel != 0) {
			this.maxLevel = maxLevel;
			loadNodes();
		}
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		if (getValue() != null) {
			String url = sitemap.url((SitemapNode) getValue());
			navigator.navigateTo(url);
		}
	}

}
