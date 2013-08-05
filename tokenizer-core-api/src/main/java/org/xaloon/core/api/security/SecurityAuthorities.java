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

/**
 * Default security permissions. Sometimes roles are used instead of permissions.
 * 
 * @author vytautas r.
 */
public interface SecurityAuthorities extends Serializable {
	/**
	 * These are default permissions
	 */

	/**
	 * Authority to create/edit the selected classifier/classifier item
	 */
	String CLASSIFIER_EDIT = "CLASSIFIER_EDIT";

	/**
	 * Authority to delete the selected classifier/classifier item
	 */
	String CLASSIFIER_DELETE = "CLASSIFIER_DELETE";


	/**
	 * These are the roles treated as permissions
	 */

	/**
	 * Default security role for login purposes
	 */
	String AUTHENTICATED_USER = "AUTHENTICATED_USER";

	/**
	 * System administrator role
	 */
	String SYSTEM_ADMINISTRATOR = "SYSTEM_ADMINISTRATOR";

	String ROLE_CLASSIFIER_ADMINISTRATOR = "Classifier administrator";

	String ROLE_SYSTEM_ADMINISTRATOR = "System administrator";

	String ROLE_REGISTERED_USER = "Registered user";
}
