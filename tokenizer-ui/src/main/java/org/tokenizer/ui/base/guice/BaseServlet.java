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
package org.tokenizer.ui.base.guice;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tokenizer.executor.master.Executor;
import org.tokenizer.executor.master.ExecutorMaster;
import org.tokenizer.executor.worker.ExecutorWorker;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;

@Singleton
public class BaseServlet extends VaadinServlet implements SessionInitListener {

	/**
	 * Cannot use constructor injection. Container expects servlet to have no-arg public constructor
	 */
	@Inject
	private UIProvider basicProvider;

	@Inject
	private ExecutorMaster executor;
	
    @Inject
    private ExecutorWorker worker;

    @Override
	protected void servletInitialized() {
		getService().addSessionInitListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		event.getSession().addUIProvider(basicProvider);
	}

}