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
package org.xaloon.core.api.cache;

import java.io.Serializable;

/**
 * Simple cache interface for reading, storing and removing items from cache. It may wrap different implementations for custom solutions
 * 
 * @author vytautas r.
 * @version 1.1, 09/30/10
 * @since 1.3
 */

public interface Cache extends Serializable {

	/**
	 * Read object from cache
	 * 
	 * @param <T>
	 * @param key
	 *            to read cached object by
	 * @return cached object. null - if there is no such object by provided key
	 */
	<T> T readFromCache(String key);

	/**
	 * Stores object to cache
	 * 
	 * @param <T>
	 * @param key
	 *            to store object by
	 * @param value
	 *            to store into cache
	 */
	<T> void storeToCache(String key, T value);

	/**
	 * Removes object from cache
	 * 
	 * @param key
	 *            to remove object by
	 */
	void removeFromCache(String key);

	/**
	 * Removes everything from cache
	 */
	void clear();
}
