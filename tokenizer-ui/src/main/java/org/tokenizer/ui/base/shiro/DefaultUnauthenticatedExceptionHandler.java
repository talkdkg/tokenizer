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

import java.io.Serializable;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public class DefaultUnauthenticatedExceptionHandler implements UnauthenticatedExceptionHandler, Serializable {
	// TODO i18N
	@Override
	public void invoke() {
		Notification n = new Notification("Authentication", "You have not logged in", Notification.TYPE_ERROR_MESSAGE,
				false);
		n.show(Page.getCurrent());
	}

}
