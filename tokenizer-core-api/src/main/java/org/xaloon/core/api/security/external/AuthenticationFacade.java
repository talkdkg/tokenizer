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
package org.xaloon.core.api.security.external;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * @author vytautas r.
 */
public interface AuthenticationFacade extends Serializable {
	/**
	 * 
	 */
	String LOGIN_TYPE_GOOGLE = "google";

	/**
	 * 
	 */
	String LOGIN_TYPE_LINKEDIN = "linkedin";

	/**
	 * 
	 */
	String LOGIN_TYPE_TWITTER = "twitter";

	/**
	 * 
	 */
	String LOGIN_TYPE_FACEBOOK = "facebook";

	/**
	 * 
	 */
	String LOGIN_TYPE_YAHOO = "yahoo";

	/**
	 * @param loginType
	 * @param requestUrl
	 */
	void beginConsumption(String loginType, String requestUrl);

	/**
	 * @param loginType
	 * @param absoluteRequestURL
	 * @return external authentication token
	 */
	AuthenticationToken endConsumption(String loginType, String absoluteRequestURL);

	/**
	 * @param namedKeys
	 * @return true if parameters contain any specific key defined as external system response
	 */
	boolean isResponseToEndConsumption(Set<String> namedKeys);

	/**
	 * @param loginType
	 * @return true if authentication consumer is enabled
	 */
	boolean isEnabled(String loginType);

	/**
	 * @return true if external authentication plugin is enabled
	 */
	boolean isPluginEnabled();

	/**
	 * @return list of available provider set
	 */
	List<String> getAvailableProviderSet();
}
