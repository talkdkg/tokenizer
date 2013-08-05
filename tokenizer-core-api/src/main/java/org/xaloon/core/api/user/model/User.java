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
package org.xaloon.core.api.user.model;

import java.util.List;

import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.persistence.Persistable;
import org.xaloon.core.api.storage.FileDescriptor;

/**
 * @author vytautas r.
 */
public interface User extends Persistable {
	/**
	 * Who wrote the comment, empty if anonymous
	 * 
	 * @return username
	 */
	String getUsername();

	/**
	 * @param username
	 */
	void setUsername(String username);

	/**
	 * @return first name
	 */
	String getFirstName();

	/**
	 * @param firstName
	 */
	void setFirstName(String firstName);

	/**
	 * @return last name
	 */
	String getLastName();

	/**
	 * @param lastName
	 */
	void setLastName(String lastName);

	/**
	 * @return email
	 */
	String getEmail();

	/**
	 * @param email
	 */
	void setEmail(String email);

	/**
	 * @return timezone of this user
	 */
	String getTimezone();

	/**
	 * @param timezone
	 */
	void setTimezone(String timezone);

	/**
	 * @return signature
	 */
	String getSignature();

	/**
	 * @param signature
	 */
	void setSignature(String signature);

	/**
	 * @return true if user is from external system
	 */
	boolean isExternal();

	/**
	 * @param external
	 */
	void setExternal(boolean external);

	/**
	 * @return website of user
	 */
	String getWebsite();

	/**
	 * @param website
	 */
	void setWebsite(String website);

	/**
	 * @return photo image
	 */
	FileDescriptor getPhotoThumbnail();

	/**
	 * @param photoThumbnail
	 */
	void setPhotoThumbnail(FileDescriptor photoThumbnail);

	/**
	 * @return display name of user like first name + last name
	 */
	String getDisplayName();

	/**
	 * Gets custom list of user properties
	 * 
	 * @return properties key=value pair of String properties
	 */
	List<? extends KeyValue<String, String>> getProperties();

	/**
	 * Sets cutom list of user properties.
	 * 
	 * @param properties
	 *            list of properties as key=value pairs
	 */
	void setProperties(List<? extends KeyValue<String, String>> properties);

}
