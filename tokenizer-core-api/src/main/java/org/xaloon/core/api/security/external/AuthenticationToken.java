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
import java.util.List;


/**
 * @author vytautas r.
 */
public class AuthenticationToken implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;

	private final String message;

	private final List<AuthenticationAttribute> attributes;

	private boolean authenticated = false;

	private String loginType;

	private Object details;

	/**
	 * Construct.
	 * 
	 * @param name
	 * @param attributes
	 * 
	 */
	public AuthenticationToken(String name, List<AuthenticationAttribute> attributes) {
		this.name = name;
		this.attributes = attributes;
		authenticated = true;
		message = null;
	}

	/**
	 * Construct.
	 * 
	 * @param name
	 * @param message
	 * @param attributes
	 * 
	 */
	public AuthenticationToken(String name, String message) {
		this.name = name;
		this.message = message;
		attributes = null;
		authenticated = false;
	}

	/**
	 * @return login type
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * Sets loginType.
	 * 
	 * @param loginType
	 *            loginType
	 */
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return attributes
	 */
	public List<AuthenticationAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return true if authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets details.
	 * 
	 * @return details
	 */
	public Object getDetails() {
		return details;
	}

	/**
	 * Sets details.
	 * 
	 * @param details
	 *            details
	 */
	public void setDetails(Object details) {
		this.details = details;
	}
}
