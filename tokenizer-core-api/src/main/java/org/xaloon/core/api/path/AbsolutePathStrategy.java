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
package org.xaloon.core.api.path;

import java.io.Serializable;

/**
 * @author vytautas r.
 * @param <T>
 */
public interface AbsolutePathStrategy<T> extends Serializable {
	/**
	 * @param descriptor
	 *            property descriptor, which will be used to generate absolute path
	 * @param generateUuid
	 *            true if unique identifier should be generated
	 * @param prefix
	 *            string will be added as part of absolute path
	 * @return absolute path string representation
	 */
	String generateAbsolutePath(T descriptor, boolean generateUuid, String prefix);

	/**
	 * @param descriptor
	 *            property descriptor, which will be used to generate absolute path
	 * @return absolute path string representation
	 */
	String generateAbsolutePath(T descriptor);
}
