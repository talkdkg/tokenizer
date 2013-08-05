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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vytautas r.
 */
public abstract class AbstractInputStreamContainer implements InputStreamContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInputStreamContainer.class);

	private InputStreamContainerOptions options;

	/**
	 * Construct.
	 * 
	 * @param options
	 */
	public AbstractInputStreamContainer(InputStreamContainerOptions options) {
		this.options = options;
	}

	public InputStreamContainerOptions getOptions() {
		return options;
	}

	public void setOptions(InputStreamContainerOptions options) {
		this.options = options;
	}

	@Override
	public byte[] asByteArray() {
		try {
			return IOUtils.toByteArray(getInputStream());
		} catch (IOException e) {
			LOGGER.error("Could not convert input stream into byte array", e);
		}
		return null;
	}
}
