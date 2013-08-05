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

import org.xaloon.core.api.keyvalue.KeyValue;

/**
 * @author vytautas r.
 */
public interface FileDescriptorDao extends Serializable {

	/**
	 * @param fileDescriptor
	 * @return reference to saved file descriptor
	 */
	FileDescriptor save(FileDescriptor fileDescriptor);

	/**
	 * @param fileDescriptor
	 * @param fileIdentifier
	 * @return reference to saved file descriptor
	 */
	FileDescriptor save(FileDescriptor fileDescriptor, KeyValue<String, String> fileIdentifier);

	/**
	 * @param path
	 * @return file descriptor if exists
	 */
	FileDescriptor getFileDescriptorByPath(String path);

	/**
	 * @return concrete implementation of file descriptor
	 */
	FileDescriptor newFileDescriptor();

	/**
	 * @param fileDescriptor
	 *            file to delete
	 */
	void delete(FileDescriptor fileDescriptor);

	/**
	 * Loads file descriptor entity by it's unique identifier
	 * 
	 * @param id
	 *            unique identifier to use for loading
	 * @return instance of selected {@link FileDescriptor}
	 */
	FileDescriptor getFileDescriptorById(Long id);
}
