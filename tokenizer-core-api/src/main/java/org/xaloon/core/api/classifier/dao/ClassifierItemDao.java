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
package org.xaloon.core.api.classifier.dao;

import java.io.Serializable;
import java.util.List;

import org.xaloon.core.api.classifier.ClassifierItem;
import org.xaloon.core.api.classifier.search.ClassifierItemSearchRequest;

/**
 * @author vytautas r.
 */
public interface ClassifierItemDao extends Serializable {

    /**
     * Persists instance of classifier item into storage.
     * 
     * @param <T>
     * 
     * @param item
     *            instance to persist.
     * @return created item
     */
    <T extends ClassifierItem> T createClassifierItem(T item);

    /**
     * Counts how many classifier items are in storage by provided parameters
     * 
     * @param classifierItemSearchRequest
     *            parameters used in search query
     * @return total count of classifier items by provided parameters
     */
    Long count(ClassifierItemSearchRequest classifierItemSearchRequest);

    /**
     * @param <T>
     * @param classifierItemSearchRequest
     *            parameters used in search query
     * @return list of classifier items. empty list is returned if no classifier items found.
     */
    <T extends ClassifierItem> List<T> find(ClassifierItemSearchRequest classifierItemSearchRequest);

    /**
     * Loads classifier item by it's primary key
     * 
     * @param <T>
     * 
     * @param id
     *            unique key to load by
     * @return classifier item instance
     */
    <T extends ClassifierItem> T loadClassifierItemById(Long id);

    /**
     * Creates new empty instance of classifier item
     * 
     * @param <T>
     * 
     * @param classifierType
     *            classifier path to search for classifier
     * @param parentClassifierItemCode
     *            parent classifier item will be set if present
     * @return instance of classifier item
     */
    <T extends ClassifierItem> T newClassifierItem(String classifierType, String parentClassifierItemCode);

    /**
     * Searches storage for classifier by provided classifier item code
     * 
     * @param <T>
     * 
     * @param classifierItemSearchRequest
     *            parameters used in search query
     * 
     * @return classifier item instance found in storage. null is returned if not found.
     */
    <T extends ClassifierItem> T getClassifierItem(ClassifierItemSearchRequest classifierItemSearchRequest);

    /**
     * Returns classifier items of 2nd level and any lower levels if any
     * 
     * @param classifierType
     * @return list of classifier items
     */
    <T extends ClassifierItem> List<T> findLowerLevelItems(String classifierType);
}
