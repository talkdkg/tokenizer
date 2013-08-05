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
package org.xaloon.core.api.tree;

import java.io.Serializable;
import java.util.List;

import org.xaloon.core.impl.plugin.tree.GenericTreeNode;

/**
 * Simple tree node interface
 * 
 * @author vytautas r.
 * @param <T>
 */
public interface TreeNode<T extends Comparable<T>> extends Serializable {
    /**
     * @return data object of tree node
     */
    T getData();

    /**
     * @param dataItem
     */
    void setData(T dataItem);

    /**
     * @return single parent node
     */
    TreeNode<T> getParent();

    /**
     * @param parentTreeNode
     */
    void setParent(TreeNode<T> parentTreeNode);

    /**
     * @return parent node list
     */
    List<TreeNode<T>> getParents();

    /**
     * @return children node list
     */
    List<GenericTreeNode<T>> getChildren();

    /**
     * Checks if children list has more than one item
     * 
     * @return true if children list is not empty and has more than one item
     */
    boolean hasMoreThanOneChildren();
}
