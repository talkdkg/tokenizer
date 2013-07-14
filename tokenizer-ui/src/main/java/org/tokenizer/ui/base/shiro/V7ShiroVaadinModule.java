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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorHandler;

public class V7ShiroVaadinModule extends AbstractModule {

	public V7ShiroVaadinModule() {
		super();
	}

	@Override
	protected void configure() {
		bindErrorHandler();
		bindUnauthenticatedHandler();
		bindUnauthorisedHandler();
		bindLoginExceptionsHandler();
	}

	/**
	 * error handler for the VaadinSession, needed to handle Shiro exceptions
	 */
	protected void bindErrorHandler() {
		bind(ErrorHandler.class).to(V7ErrorHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthorised action. If you have
	 * defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnauthorisedHandler() {
		bind(UnauthorizedExceptionHandler.class).to(DefaultUnauthorizedExceptionHandler.class);
	}

	/**
	 * the {@link DefaultErrorHandler} calls this handler in response to an attempted unauthenticated action. If you
	 * have defined your own ErrorHandler you may of course do something different
	 */
	protected void bindUnauthenticatedHandler() {
		bind(UnauthenticatedExceptionHandler.class).to(DefaultUnauthenticatedExceptionHandler.class);
	}

	/**
	 * The login process may raise a number of {@link ShiroException}s. This handler is called to manage those
	 * exceptions gracefully.
	 */
	protected void bindLoginExceptionsHandler() {
		bind(LoginExceptionHandler.class).to(DefaultLoginExceptionHandler.class);
	}

	@Provides
	V7SecurityManager providesSecurityManager() {
		return (V7SecurityManager) SecurityUtils.getSecurityManager();
	}

}
