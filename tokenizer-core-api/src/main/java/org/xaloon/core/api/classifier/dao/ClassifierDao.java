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

import org.xaloon.core.api.classifier.Classifier;

/**
 * @author vytautas r.
 */
public interface ClassifierDao extends Serializable {

	/**
	 * @return total count of existing classifiers
	 */
	Long getCount();

	/**
	 * @param <T>
	 * @param first
	 *            start position to search for classifiers. -1,0 - search from start position
	 * @param count
	 *            maximum count of classifiers to return. -1 - return all
	 * @return list of classifier instances. empty list is returned if no classifiers found.
	 */
	<T extends Classifier> List<T> findClassifiers(long first, long count);

	/**
	 * Loads classifier by it's primary key
	 * 
	 * @param <T>
	 * 
	 * @param id
	 *            unique key to load by
	 * @return classifier instance
	 */
	<T extends Classifier> T loadClassifierById(Long id);

	/**
	 * Persists instance of classifier into storage.
	 * 
	 * @param <T>
	 * 
	 * @param item
	 *            instance to persist.
	 */
	<T extends Classifier> void createClassifier(T item);

	/**
	 * Creates new empty instance of classifier
	 * 
	 * @param <T>
	 * 
	 * @return instance of classifier
	 */
	<T extends Classifier> T newClassifier();

	/**
	 * Searches storage for classifier by provided type
	 * 
	 * @param <T>
	 * 
	 * @param type
	 *            string representation of classifier type used in query to search for classifier
	 * @return classifier instance found in storage. null is returned if not found.
	 */
	<T extends Classifier> T findClassifierByType(String type);
}
