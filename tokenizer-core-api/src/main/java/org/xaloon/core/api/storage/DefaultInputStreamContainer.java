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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author vytautas r.
 */
public class DefaultInputStreamContainer extends AbstractInputStreamContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private InputStream is;

	/**
	 * Construct.
	 */
	public DefaultInputStreamContainer() {
		this(null);
	}

	/**
	 * Construct.
	 * 
	 * @param is
	 */
	public DefaultInputStreamContainer(InputStream is) {
		this(is, new InputStreamContainerOptions());
	}

	/**
	 * Construct.
	 * 
	 * @param is
	 * @param options
	 */
	public DefaultInputStreamContainer(InputStream is, InputStreamContainerOptions options) {
		super(options);
		this.is = is;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return is;
	}

	@Override
	public void close() {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return is == null;
	}
}
