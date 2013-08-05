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
package org.xaloon.core.impl.plugin.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.plugin.AbstractPluginBean;

/**
 * Simple menu item class which is used as default implementation to create category and dynamic menu items
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */

public class MenuItem extends AbstractPluginBean implements Comparable<MenuItem>, Serializable {
	private static final long serialVersionUID = 1L;

	/** The unique identifier of menu item. Used for link name localization */
	private String key;

	/**
	 * Parameters of url
	 */
	private List<KeyValue<String, String>> parameters = new ArrayList<KeyValue<String, String>>();

	/**
	 * Concrete page class
	 */
	private Class<?> pageClass;

	/**
	 * Order of menu item in user interface
	 */
	private int order;


	/**
	 * @return order of this menu item
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @param pageClass
	 */
	public void setPageClass(Class<?> pageClass) {
		this.pageClass = pageClass;
	}

	/**
	 * @return page class of this menu item
	 */
	public Class<?> getPageClass() {
		return pageClass;
	}

	/**
	 * Sorting current menu item by provided order property
	 * <p>
	 * If order property id is not provided then order by name
	 */
	public int compareTo(MenuItem o) {
		if (o == null) {
			return 1;
		}
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		compareToBuilder.append(getOrder(), o.getOrder());
		compareToBuilder.append(getKey(), o.getKey());

		return compareToBuilder.toComparison();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof MenuItem)) {
			return false;
		}
		MenuItem menuItem = (MenuItem)obj;

		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(getOrder(), menuItem.getOrder());
		return equalsBuilder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(getOrder());
		return hashCodeBuilder.hashCode();
	}

	/**
	 * @param parameters
	 */
	public void setParameters(List<KeyValue<String, String>> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return parameters for this menu item
	 */
	public List<KeyValue<String, String>> getParameters() {
		return parameters;
	}

	/**
	 * @return unique identifier
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return String.format("Order:%d; PageClass: %s", order, pageClass.getName());
	}
}
