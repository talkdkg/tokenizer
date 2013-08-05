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
package org.xaloon.core.api.util;

import org.xaloon.core.api.keyvalue.KeyValue;

/**
 * Simple class to store key and value
 * 
 * @author vytautas r.
 * 
 * @param <T>
 *            key parameter type
 * @param <K>
 *            value parameter type
 */
public class DefaultKeyValue<T, K> implements KeyValue<T, K> {
	private static final long serialVersionUID = 1L;

	/** pair key property **/
	private T key;

	/** pair value property **/
	private K value;

	/** bookmarkable representation of value. optional **/
	private String path;

	/**
	 * Constructor with initial parameters passed
	 * 
	 * @param key
	 *            key parameter to set
	 * @param value
	 *            value parameter to set
	 */
	public DefaultKeyValue(T key, K value) {
		this.key = key;
		this.value = value;
		if (value != null && value instanceof String) {
			this.path = UrlUtil.encode(value.toString());
		}
	}

	/**
	 * Default constructor when parameters are filled later.
	 */
	public DefaultKeyValue() {
	}

	public T getKey() {
		return key;
	}

	/**
	 * @param key
	 *            key of type T to set
	 */
	public void setKey(T key) {
		this.key = key;
	}

	public K getValue() {
		return value;
	}

	/**
	 * @param value
	 *            value of type K to set
	 */
	public void setValue(K value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return (key == null) && (value == null);
	}

	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            bookmarkable value
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
