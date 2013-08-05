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

import java.util.List;
import java.util.Map;

import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.security.LoginService;
import org.xaloon.core.api.user.dao.UserDao;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface UserFacade extends UserDao, LoginService {
	/**
	 * 
	 */
	String EMAIL_VALIDATION_ERROR = "EMAIL_VALIDATION_ERROR";

	/**
	 * @param user
	 * @param password
	 * @param active
	 * @param alias
	 * @return activation key
	 */
	<T extends User> String registerUser(T user, String password, boolean active, KeyValue<String, String> alias);

	/**
	 * Generates and sends new password to requested email if found
	 * 
	 * @param email
	 * @return null if operation completed successfully, otherwise error code, which might be localized
	 */
	String sendNewPassword(String email);

	List<UserSearchResult> findCombinedUsers(Map<String, String> filter, long first, long count);
}
