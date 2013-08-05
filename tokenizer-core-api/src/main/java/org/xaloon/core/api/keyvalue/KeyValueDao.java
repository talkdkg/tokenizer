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

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;

import org.xaloon.core.api.exception.CreateClassInstanceException;

/**
 * @author vytautas r.
 * @param <K>
 * @param <V>
 * @param <R>
 */
@Named
public interface KeyValueDao<K, V, R extends KeyValue<K, V>> extends Serializable {
	/**
	 * @param key
	 * @param parsedValue
	 * @return created instance of KeyValue
	 * @throws CreateClassInstanceException
	 */
	R newKeyValue(K key, V parsedValue) throws CreateClassInstanceException;

	/**
	 * @param key
	 * @param parsedValue
	 * @return instance of KeyValue if found in storage. null is returned otherwise
	 */
	R findInStorage(K key, V parsedValue);

	/**
	 * 
	 * @param key
	 * 
	 * @param randomLinkCountToSelect
	 *            how many values to select - maximum available
	 * @return list of KeyValue
	 */
	List<R> findRandomValues(String key, int randomLinkCountToSelect);
}
