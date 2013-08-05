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
package org.xaloon.core.api.interceptor;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public class Signature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String methodName;

	@SuppressWarnings("rawtypes")
	private Class declaringType;

	private String declaringTypeName;

	private Object[] parameters;

	/**
	 * Gets parameters.
	 * 
	 * @return parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * Sets parameters.
	 * 
	 * @param parameters
	 *            parameters
	 * @return
	 */
	public Signature setParameters(Object[] parameters) {
		this.parameters = parameters;
		return this;
	}

	/**
	 * Gets declaringType.
	 * 
	 * @return declaringType
	 */
	@SuppressWarnings("rawtypes")
	public Class getDeclaringType() {
		return declaringType;
	}

	/**
	 * Sets declaringType.
	 * 
	 * @param declaringType
	 *            declaringType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Signature setDeclaringType(Class declaringType) {
		this.declaringType = declaringType;
		return this;
	}

	/**
	 * Gets declaringTypeName.
	 * 
	 * @return declaringTypeName
	 */
	public String getDeclaringTypeName() {
		return declaringTypeName;
	}

	/**
	 * Sets declaringTypeName.
	 * 
	 * @param declaringTypeName
	 *            declaringTypeName
	 * @return
	 */
	public Signature setDeclaringTypeName(String declaringTypeName) {
		this.declaringTypeName = declaringTypeName;
		return this;
	}

	/**
	 * Gets methodName.
	 * 
	 * @return methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Sets methodName.
	 * 
	 * @param methodName
	 *            methodName
	 * @return
	 */
	public Signature setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
}
