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
package org.xaloon.core.api.security;

import java.io.Serializable;
import java.util.List;

import org.xaloon.core.api.security.model.SecurityEntity;
import org.xaloon.core.api.security.model.UserDetails;

/**
 * @author vytautas r.
 * @param <T>
 * @param <K>
 */
public interface AbstractAuthorityService<T extends SecurityEntity, K extends SecurityEntity> extends Serializable {
	/**
	 * Returns pageable list of existing security items
	 * 
	 * @param first
	 *            first security item to fetch
	 * @param count
	 *            max count of security items to fetch
	 * @return list of found entities
	 */
	List<T> getAuthorities(long first, long count);

	Long getCount();

	/**
	 * Returns new instance of security item implementation
	 * 
	 * @return new instance of security item implementation
	 */
	T newAuthority();

	void delete(T authority);

	/**
	 * Saves provided security item in storage
	 * 
	 * @param authority
	 *            security item to save
	 * @return
	 */
	T save(T authority);

	/**
	 * Returns existing security item or creates new one if there is no exising
	 * 
	 * @param authorityName
	 *            string security item anme to search for
	 * @return security item object found or created
	 */
	T findOrCreateAuthority(String authorityName);

	List<T> getAuthoritiesByUsername(String username);

	UserDetails revoke(UserDetails userDetails, T authority);

	/**
	 * Searches for a security item and returns the object if found. null is returned otherwise
	 * 
	 * @param name
	 *            security item name to search
	 * @return found security item object
	 */
	T getAuthorityByName(String name);

	T getAuthorityByPath(String path);

	UserDetails assignAuthoritiesByName(UserDetails userDetails, List<String> selections);

	UserDetails assignAuthorities(UserDetails userDetails, List<T> selections);

	T assignChildren(T parent, List<K> selections);

	T revokeChild(T parent, K authority);
}
