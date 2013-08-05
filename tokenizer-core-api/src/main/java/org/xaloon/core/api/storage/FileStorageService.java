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
import java.util.Map;

import org.xaloon.core.api.keyvalue.KeyValue;

/**
 * @author vytautas r.
 */
public interface FileStorageService extends Serializable {
	/**
	 * user email as a parameter
	 */
	String PARAMETER_USER_EMAIL = "userEmail";

	/**
	 * external token for picasa services usage
	 */
	String PARAMETER_USER_TOKEN = "userToken";

	/**
	 * JPA file storage service implementation facade name
	 */
	String FILE_STORAGE_SERVICE_JPA = "jpaFileStorageService";

	/**
	 * Stores actual input stream into storage
	 * 
	 * @param fileDescriptor
	 * @param inputStreamContainer
	 * @return unique identifier where file is stored. key - path, value - identifier. usually they are the same, but depending on implementation key
	 *         might be http link to the external image
	 */
	KeyValue<String, String> storeFile(FileDescriptor fileDescriptor, InputStreamContainer inputStreamContainer);

	/**
	 * @param fileDescriptor
	 * @param inputStreamContainer
	 * @param additionalProperties
	 * @return unique identifier where file is stored. key - path, value - identifier. usually they are the same, but depending on implementation key
	 *         might be http link to the external image
	 */
	KeyValue<String, String> storeFile(FileDescriptor fileDescriptor, InputStreamContainer inputStreamContainer,
		Map<String, Object> additionalProperties);

	/**
	 * Returns file from storage as byte array
	 * 
	 * @param identifier
	 * @return actual byte array of file
	 */
	byte[] getByteArrayByIdentifier(String identifier);

	/**
	 * Deletes from storage by unique identifier
	 * 
	 * @param uniqueIdentifier
	 * @return true if file was deleted
	 */
	boolean delete(String uniqueIdentifier);

	/**
	 * Returns the name of implementation class, the same as in @Named() attribute
	 * 
	 * @return
	 */
	String getName();

}
