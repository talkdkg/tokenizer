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
package org.xaloon.core.api.user;

import java.io.Serializable;

/**
 * @author vytautas r.
 */
public class UserSearchResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;

	private String firstName;

	private String lastName;

	private String fullName;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;

	/**
	 * Gets fullName.
	 * 
	 * @return fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets fullName.
	 * 
	 * @param fullName
	 *            fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets username.
	 * 
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets username.
	 * 
	 * @param username
	 *            username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets firstName.
	 * 
	 * @return firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets firstName.
	 * 
	 * @param firstName
	 *            firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets lastName.
	 * 
	 * @return lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets lastName.
	 * 
	 * @param lastName
	 *            lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets accountNonExpired.
	 * 
	 * @return accountNonExpired
	 */
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	/**
	 * Sets accountNonExpired.
	 * 
	 * @param accountNonExpired
	 *            accountNonExpired
	 */
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	/**
	 * Gets accountNonLocked.
	 * 
	 * @return accountNonLocked
	 */
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * Sets accountNonLocked.
	 * 
	 * @param accountNonLocked
	 *            accountNonLocked
	 */
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	/**
	 * Gets credentialsNonExpired.
	 * 
	 * @return credentialsNonExpired
	 */
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	/**
	 * Sets credentialsNonExpired.
	 * 
	 * @param credentialsNonExpired
	 *            credentialsNonExpired
	 */
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	/**
	 * Gets enabled.
	 * 
	 * @return enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets enabled.
	 * 
	 * @param enabled
	 *            enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
