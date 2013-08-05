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
package org.xaloon.core.api.plugin.resource;

import java.io.Serializable;

import org.xaloon.core.api.plugin.Plugin;


/**
 * Resource repository listener will be called when resource will be stored into repository.
 * 
 * @author vytautas r.
 */
public interface ResourceRepositoryListener extends Serializable {

	/**
	 * Method is called before saving property into repository
	 * 
	 * @param plugin
	 *            plugin which contains modified property
	 * @param propertyKey
	 *            property key which will store value
	 * @param value
	 *            value which is modified
	 */
	void onBeforeSaveProperty(Plugin plugin, String propertyKey, Object value);

	/**
	 * Method is called after saving property into repository
	 * 
	 * @param plugin
	 *            plugin which contains modified property
	 * @param propertyKey
	 *            property key which will store value
	 */
	void onAfterSaveProperty(Plugin plugin, String propertyKey);

}
