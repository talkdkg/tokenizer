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
package org.xaloon.core.api.user.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface UserDao extends Serializable {
	/**
	 * @param user
	 * @return persisted instance
	 */
	<T extends User> T save(T user);

	/**
	 * @param username
	 * @return user instance found in database
	 */
	<T extends User> T getUserByUsername(String username);

	/**
	 * @param email
	 * @return user instance found in database
	 */
	<T extends User> T getUserByEmail(String email);

	/**
	 * @return new instance of user
	 */
	<T extends User> T newUser();

	/**
	 * Creates new anonymous user entity
	 * 
	 * @return new instance of anonymous user
	 */
	<T extends User> T newAnonymousUser();

	/**
	 * Creates new anonymous user entity from provided details
	 * 
	 * @param currentUser
	 * @return new instance of anonymous user
	 */
	<T extends User> T newAnonymousUser(T currentUser);

	/**
	 * Concatenates first and last name into single string and returns it as a full user name
	 * 
	 * @param username
	 *            username to lookup for a full name
	 * @return concatenated first and last name for provided username
	 */
	String getFullNameForUser(String username);

	String formatFullName(String firstName, String lastName);

	/**
	 * Returns sublist of system users
	 * 
	 * @param filter
	 *            filter properties to search
	 * @param first
	 *            starting position
	 * @param count
	 *            how many users to fetch
	 * @return sublist of users starting first and max count
	 */
	List<User> findUsers(Map<String, String> filter, long first, long count);

	/**
	 * Returns total count of users in the system if no additional filter added
	 * 
	 * @param filter
	 *            additional properties to search
	 * @return integer value for total existing users in system
	 */
	Long count(Map<String, String> filter);

	/**
	 * Removes user from the system
	 * 
	 * @param username
	 * @return true if user was successfully removed from {@link User} table
	 */
	boolean deleteUser(String username);
}
