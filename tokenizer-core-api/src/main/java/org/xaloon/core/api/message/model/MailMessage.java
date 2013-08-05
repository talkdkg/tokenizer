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
package org.xaloon.core.api.message.model;

import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface MailMessage extends TextMessage {
	/**
	 * @return true if message was read
	 */
	boolean isRead();

	/**
	 * @param messageRead
	 */
	void setRead(boolean messageRead);

	/**
	 * @return recipient of message
	 */
	User getToUser();

	/**
	 * @param toUser
	 */
	void setToUser(User toUser);
}
