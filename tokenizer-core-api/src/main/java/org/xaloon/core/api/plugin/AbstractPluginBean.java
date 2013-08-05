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

import java.io.Serializable;

/**
 * Abstract java bean which should contain configuration properties for concrete plugin. Every java bean should extend this class in order to be used
 * as configuration parameter container.
 * 
 * @author vytautas r.
 * @version 1.1, 09/28/10
 * @since 1.5
 */

public class AbstractPluginBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Any additional rules to check before setting plugin visibility. If plugin is not valid then it will not be visible by default.
	 * 
	 * @return additional rules to check for plugin validity.
	 */
	public boolean isValid() {
		return true;
	}
}
