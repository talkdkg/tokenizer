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

import java.util.HashMap;
import java.util.Map;

import org.xaloon.core.api.plugin.AbstractPlugin;
import org.xaloon.core.api.plugin.AbstractPluginBean;

/**
 * Base tree plugin, which holds object hierarchy in memory while application is running. It should be used for dynamic
 * menu panel
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 * 
 * @param <T>
 *            Concrete data object in tree, which contains business information.
 * @param <K>
 */

public abstract class AbstractTreePlugin<K extends AbstractPluginBean, T extends Comparable<T>> extends
        AbstractPlugin<K> {
    private static final long serialVersionUID = 1L;

    /**
     * Whole tree of menu items
     */
    private final GenericTreeNode<T> tree = new GenericTreeNode<T>();

    /**
     * Tree nodes by concrete url
     */
    private final Map<String, GenericTreeNode<T>> treeNodesByUrl = new HashMap<String, GenericTreeNode<T>>();

    /**
     * Tree nodes by concrete context
     */
    private final Map<String, GenericTreeNode<T>> treeNodesByContext = new HashMap<String, GenericTreeNode<T>>();

    /**
     * Returns whole tree of business tree items
     * 
     * @return object of root tree node
     */
    public GenericTreeNode<T> getTree() {
        return tree;
    }

    /**
     * Returns all tree node map, which contains nodes by concrete url
     * 
     * @return map container of tree nodes
     */
    public Map<String, GenericTreeNode<T>> getTreeNodesByUrl() {
        return treeNodesByUrl;
    }

    /**
     * Returns all tree node map, which contains nodes by concrete context
     * 
     * @return map container of tree nodes
     */
    public Map<String, GenericTreeNode<T>> getTreeNodesByContext() {
        return treeNodesByContext;
    }
}
