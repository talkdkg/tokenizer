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
package org.xaloon.core.api.classifier.search;

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.search.SearchRequest;

/**
 * @author vytautas r.
 */
public class ClassifierItemSearchRequest extends SearchRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String classifierType;

	private String classifierItemCode;

	private String classifierItemName;

	private String parentClassifierItemCode;

	private boolean ignoreParentCode;

	private String orderBy;

	/**
	 * Gets orderBy.
	 * 
	 * @return orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * Sets orderBy.
	 * 
	 * @param orderBy
	 *            orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Gets ignoreParentCode.
	 * 
	 * @return ignoreParentCode
	 */
	public boolean isIgnoreParentCode() {
		return ignoreParentCode;
	}

	/**
	 * Sets ignoreParentCode.
	 * 
	 * @param ignoreParentCode
	 *            ignoreParentCode
	 */
	public void setIgnoreParentCode(boolean ignoreParentCode) {
		this.ignoreParentCode = ignoreParentCode;
	}

	/**
	 * @return classifier type
	 */
	public String getClassifierType() {
		return classifierType;
	}

	/**
	 * @param classifierType
	 * @return current instance
	 */
	public ClassifierItemSearchRequest setClassifierType(String classifierType) {
		this.classifierType = classifierType;
		return this;
	}

	/**
	 * @return classifier item code
	 */
	public String getClassifierItemCode() {
		return classifierItemCode;
	}

	/**
	 * @param classifierItemCode
	 * @return current instance
	 */
	public ClassifierItemSearchRequest setClassifierItemCode(String classifierItemCode) {
		this.classifierItemCode = classifierItemCode;
		return this;
	}

	/**
	 * @return parent classifier item code
	 */
	public String getParentClassifierItemCode() {
		return parentClassifierItemCode;
	}

	/**
	 * @param parentClassifierItemCode
	 * @return current instance
	 */
	public ClassifierItemSearchRequest setParentClassifierItemCode(String parentClassifierItemCode) {
		this.parentClassifierItemCode = parentClassifierItemCode;
		return this;
	}

	/**
	 * @return true if no parameters are selected
	 */
	public boolean isParentSelection() {
		return StringUtils.isEmpty(parentClassifierItemCode) && StringUtils.isEmpty(classifierItemCode);
	}

	/**
	 * Gets classifierItemName.
	 * 
	 * @return classifierItemName
	 */
	public String getClassifierItemName() {
		return classifierItemName;
	}

	/**
	 * Sets classifierItemName.
	 * 
	 * @param classifierItemName
	 *            classifierItemName
	 */
	public void setClassifierItemName(String classifierItemName) {
		this.classifierItemName = classifierItemName;
	}
}
