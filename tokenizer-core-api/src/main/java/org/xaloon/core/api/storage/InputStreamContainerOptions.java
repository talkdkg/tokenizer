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
package org.xaloon.core.api.storage;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public class InputStreamContainerOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 200;

	private int height = 100;

	private boolean resize;

	public int getWidth() {
		return width;
	}

	public InputStreamContainerOptions setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public InputStreamContainerOptions setHeight(int height) {
		this.height = height;
		return this;
	}

	public boolean isResize() {
		return resize;
	}

	public InputStreamContainerOptions setResize(boolean resize) {
		this.resize = resize;
		return this;
	}


}
