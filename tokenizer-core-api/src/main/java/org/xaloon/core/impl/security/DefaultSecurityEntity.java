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
package org.xaloon.core.impl.security;

import org.xaloon.core.api.security.model.Authority;
import org.xaloon.core.api.util.UrlUtil;
import org.xaloon.core.impl.persistence.DefaultPersistentObject;

/**
 * Default authority in memory
 * 
 * @author vytautas r.
 */
public class DefaultSecurityEntity extends DefaultPersistentObject implements Authority {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	/**
	 * Construct.
	 * 
	 * @param name
	 */
	public DefaultSecurityEntity(String name) {
		this.name = name;
		setPath(UrlUtil.encode(name));
	}

	/**
	 * Gets name.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
