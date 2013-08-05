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
package org.xaloon.core.api.security.model;

import java.util.List;

import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.persistence.Persistable;

/**
 * @author vytautas r.
 */
public interface UserDetails extends Persistable {
	/**
	 * @return username
	 */
	String getUsername();

	/**
	 * @return encrypted password
	 */
	String getPassword();

	/**
	 * @return account non expired
	 */
	boolean isAccountNonExpired();

	/**
	 * @param flag
	 *            new flag to set
	 */
	void setAccountNonExpired(boolean flag);

	/**
	 * @return account non locked
	 */
	boolean isAccountNonLocked();

	/**
	 * @param flag
	 *            new flag to set
	 */
	void setAccountNonLocked(boolean flag);

	/**
	 * @return credentials non expired
	 */
	boolean isCredentialsNonExpired();

	/**
	 * @param flag
	 *            new flag to set
	 */
	void setCredentialsNonExpired(boolean flag);

	/**
	 * @return enabled
	 */
	boolean isEnabled();

	/**
	 * @param flag
	 *            new flag to set
	 */
	void setEnabled(boolean flag);

	/**
	 * @return list of available user aliases.
	 */
	List<? extends KeyValue<String, String>> getAliases();

	<T extends SecurityGroup> List<T> getGroups();

	<T extends SecurityRole> List<T> getRoles();

	<T extends Authority> List<T> getAuthorities();
}
