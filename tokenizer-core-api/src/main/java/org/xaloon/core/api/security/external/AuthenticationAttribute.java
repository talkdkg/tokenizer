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
package org.xaloon.core.api.security.external;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author vytautas r.
 */
public class AuthenticationAttribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final String type;
	private boolean required = false;
	private int count = 1;
	private final List<String> values;

	/**
	 * Construct.
	 * 
	 * @param name
	 * @param value
	 */
	public AuthenticationAttribute(String name, String value) {
		this.name = name;
		type = null;

		if (!StringUtils.isEmpty(value)) {
			values = new ArrayList<String>();
			values.add(value);
		} else {
			values = null;
		}

	}

	/**
	 * Construct.
	 * 
	 * @param name
	 * @param type
	 * @param values
	 */
	public AuthenticationAttribute(String name, String type, List<String> values) {
		this.name = name;
		this.type = type;
		this.values = values;
	}

	/**
	 * @return true if attribute is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return type identifier
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return first value
	 */
	public String getValue() {
		if (values != null && !values.isEmpty()) {
			return values.get(0);
		}
		return null;
	}

	/**
	 * @return all values
	 */
	public List<String> getValues() {
		return values;
	}
}
