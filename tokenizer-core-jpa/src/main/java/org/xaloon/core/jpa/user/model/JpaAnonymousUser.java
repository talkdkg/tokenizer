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
package org.xaloon.core.jpa.user.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.NotImplementedException;
import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.storage.FileDescriptor;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_USER_ANONYMOUS")
@DiscriminatorValue("1")
public class JpaAnonymousUser extends AbstractUser {
	private static final long serialVersionUID = 1L;

	@Column(name = "DISPLAY_NAME")
	private String displayName;

	@Column(name = "EMAIL")
	private String email;

	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getUsername() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setUsername(String username) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public String getFirstName() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setFirstName(String firstName) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public String getLastName() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setLastName(String lastName) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public String getTimezone() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setTimezone(String timezone) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public String getSignature() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setSignature(String signature) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public boolean isExternal() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setExternal(boolean external) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public FileDescriptor getPhotoThumbnail() {
		return null;
	}

	@Override
	public void setPhotoThumbnail(FileDescriptor photoThumbnail) {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public List<? extends KeyValue<String, String>> getProperties() {
		throw new NotImplementedException("This method is not supported!");
	}

	@Override
	public void setProperties(List<? extends KeyValue<String, String>> properties) {
		throw new NotImplementedException("This method is not supported!");
	}
}
