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
package org.tokenizer.ui.base.shiro;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.tokenizer.ui.base.navigate.StandardPageKeys;
import org.tokenizer.ui.base.navigate.V7Navigator;
import org.tokenizer.ui.base.view.LoginView;


public class DefaultLoginExceptionHandler implements LoginExceptionHandler {
	// TODO i18N
	private final V7Navigator navigator;
	public static final String invalidLogin = "That username or password was not recognised";
	public static final String concurrent = "This account is already in use.  You must log out of that session before you can log in again.";

	@Inject
	protected DefaultLoginExceptionHandler(V7Navigator navigator) {
		this.navigator = navigator;
	}

	@Override
	public void unknownAccount(LoginView loginView, UsernamePasswordToken token) {
		loginView.setStatusMessage(invalidLogin);
	}

	@Override
	public void incorrectCredentials(LoginView loginView, UsernamePasswordToken token) {
		loginView.setStatusMessage(invalidLogin);
	}

	@Override
	public void expiredCredentials(LoginView loginView, UsernamePasswordToken token) {
		navigator.navigateTo(StandardPageKeys.refreshAccount);
	}

	@Override
	public void accountLocked(LoginView loginView, UsernamePasswordToken token) {
		navigator.navigateTo(StandardPageKeys.unlockAccount);
	}

	@Override
	public void excessiveAttempts(LoginView loginView, UsernamePasswordToken token) {
		navigator.navigateTo(StandardPageKeys.resetAccount);
	}

	@Override
	public void concurrentAccess(LoginView loginView, UsernamePasswordToken token) {
		loginView.setStatusMessage(concurrent);
	}

	@Override
	public void disabledAccount(LoginView loginView, UsernamePasswordToken token) {
		navigator.navigateTo(StandardPageKeys.enableAccount);
	}

}
