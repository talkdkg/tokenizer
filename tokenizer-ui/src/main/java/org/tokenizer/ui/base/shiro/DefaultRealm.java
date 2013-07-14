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

import java.util.Set;

import javax.inject.Inject;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.google.common.collect.ImmutableSet;

public class DefaultRealm extends AuthorizingRealm {

	private final LoginAttemptLog loginAttemptLog;

	@Inject
	protected DefaultRealm(LoginAttemptLog loginAttemptLog, CredentialsMatcher matcher) {
		super(matcher);
		this.loginAttemptLog = loginAttemptLog;
		setCachingEnabled(false);
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String username = upToken.getUsername();

		if (username == null) {
			throw new AccountException("user name cannot be null");
		}

		String password = String.copyValueOf(upToken.getPassword());

		if (password.equals("password")) {
			loginAttemptLog.recordSuccessfulAttempt(upToken);
			return new SimpleAuthenticationInfo(username, password, this.getName());
		} else {
			loginAttemptLog.recordFailedAttempt(upToken);
			return null;
		}

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		String username = (String) principals.fromRealm(getName()).iterator().next();
		Set roleNames = ImmutableSet.of();
		if (username != null) {
			roleNames = ImmutableSet.of("foo", "goo");
		}
		return new SimpleAuthorizationInfo(roleNames);
	}

	@Override
	public String getName() {
		return "V7 Default Realm";
	}
}
