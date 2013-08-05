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
package org.xaloon.core.api.plugin;

/**
 * Default plugin types used to determine which logic should be used depending on plugin type. Custom plugin types might be also used.
 * 
 * @author vytautas r.
 */
public interface PluginType {

	/**
	 * Visible type means when generating dynamic menu this plugin might be involved while generating menu item
	 */
	String VISIBLE = "VISIBLE";

	/**
	 * Hidden type should be used for system plugins, which are not visible to user and might be not configurable
	 */
	String HIDDEN = "HIDDEN";
}
