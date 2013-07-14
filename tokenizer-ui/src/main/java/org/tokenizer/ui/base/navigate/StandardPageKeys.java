/*
 * Copyright 2013 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.ui.base.navigate;

import java.util.Locale;
import java.util.ResourceBundle;

import org.tokenizer.ui.i18n.I18NKeys;


public enum StandardPageKeys implements I18NKeys<StandardPageLabels> {

	publicHome, // The home page for non-authenticated users
	secureHome, // The home page for authenticated users
	login, // the login page
	logout, // the page to go to after logging out
	resetAccount, // page for the user to request an account reset
	unlockAccount, // the page to go to for the user to request their account be unlocked
	refreshAccount, // the page to go to for the user to refresh their account after credentials have expired
	requestAccount, // the page to go to for the user to request an account (Equivalent to 'register')
	enableAccount // the page to go to for the user to request that their account is enabled
	;

	private static String pageDefault(StandardPageKeys key) {
		switch (key) {
		case publicHome:
			return "public/home";
		case secureHome:
			return "secure/home";
		case login:
			return "public/login";
		case logout:
			return "public/logout";
		case resetAccount:
			return "public/reset-account";
		case unlockAccount:
			return "public/unlock-account";
		case refreshAccount:
			return "public/refresh-account";
		case requestAccount:
			return "public/request-account";
		case enableAccount:
			return "public/enable-account";
		default:
			return "unknown";
		}
	}

	@Override
	public StandardPageLabels getBundle(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(StandardPageLabels.class.getName(), locale);
		return (StandardPageLabels) bundle;
	}

	@Override
	public String getValue(Locale locale) {
		return getBundle(locale).getValue(this);
	}

	@Override
	public boolean isNullKey() {
		return false;
	}
}
