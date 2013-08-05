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

import org.xaloon.core.api.config.ListenerCollection;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public class UserListenerCollection extends ListenerCollection<UserListener> implements UserListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onBeforeDelete(final User userToBeDeleted) {
		notify(new Notifier<UserListener>() {
			public void notify(UserListener listener) {
				listener.onBeforeDelete(userToBeDeleted);
			}
		});
	}
}
