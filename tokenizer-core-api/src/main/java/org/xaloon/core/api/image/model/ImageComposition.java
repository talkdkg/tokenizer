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
package org.xaloon.core.api.image.model;

import org.xaloon.core.api.persistence.Persistable;

public interface ImageComposition extends Persistable {
	/**
	 * @return the object
	 */
	public Album getObject();

	/**
	 * @param object
	 *            the object to set
	 */
	void setObject(Album object);

	/**
	 * @return the image
	 */
	Image getImage();

	/**
	 * @param image
	 *            the image to set
	 */
	void setImage(Image image);
}
