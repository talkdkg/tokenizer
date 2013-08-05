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
package org.xaloon.core.api.keyvalue;

import org.xaloon.core.api.bookmark.Bookmarkable;


/**
 * Simple interface used to represent key-value pair
 * 
 * @author vytautas r.
 * @param <T>
 *            key representation type
 * @param <K>
 *            value representation type
 */
public interface KeyValue<T, K> extends Bookmarkable {
	/**
	 * Returns key pair element
	 * 
	 * @return object of type T
	 */
	T getKey();

	/**
	 * Sets property key
	 * 
	 * @param key
	 */
	void setKey(T key);

	/**
	 * Returns value pair element
	 * 
	 * @return object of type K
	 */
	K getValue();

	/**
	 * Set key value
	 * 
	 * @param value
	 */
	void setValue(K value);

	/**
	 * Checks if key and value elements are empty
	 * 
	 * @return true if both key and value elements are null or empty
	 */
	boolean isEmpty();
}
