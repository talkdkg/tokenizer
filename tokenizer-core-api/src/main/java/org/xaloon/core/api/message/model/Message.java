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

import java.util.Date;
import java.util.List;

import org.xaloon.core.api.persistence.Persistable;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface Message extends Persistable {

	/**
	 * @return message creating date
	 */
	Date getCreateDate();

	/**
	 * @param messageCreateDate
	 */
	void setCreateDate(Date messageCreateDate);

	/**
	 * @return user who sent the message
	 */
	User getFromUser();

	/**
	 * @param fromUser
	 */
	void setFromUser(User fromUser);

	/**
	 * @return message body
	 */
	String getMessage();

	/**
	 * @param messageBody
	 */
	void setMessage(String messageBody);

	/**
	 * @return list of attachments for this message
	 */
	List<? extends FileDescriptor> getAttachments();
}
