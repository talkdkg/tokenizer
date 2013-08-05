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
package org.xaloon.core.api.bookmark;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public interface Bookmarkable extends Serializable {
	String PARAM_PATH = "path";

	/**
	 * Returns bookmarkable representation of value
	 * 
	 * @return encoded path of value string
	 */
	String getPath();

	/**
	 * Sets path for bookmarkable instance
	 * 
	 * @param path
	 */
	void setPath(String path);
}
