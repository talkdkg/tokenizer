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
package org.xaloon.core.api.plugin.comment;

import org.xaloon.core.api.bookmark.Bookmarkable;
import org.xaloon.core.api.message.model.Message;

/**
 * @author vytautas r.
 */
public interface Comment extends Message, Bookmarkable {
	/**
	 * This is entity id which comment was written on
	 * 
	 * @return entity id
	 */
	Long getEntityId();

	/**
	 * @param entityId
	 */
	void setEntityId(Long entityId);

	/**
	 * Several systems may have the same object id's. This is to identify unique system
	 * 
	 * @return component enumeration
	 */
	Long getCategoryId();

	/**
	 * @param categoryId
	 */
	void setCategoryId(Long categoryId);


	/**
	 * Check if comment is enabled or disabled due to spam
	 * 
	 * @return true if comment is enabled and visible to end users
	 */
	boolean isEnabled();

	/**
	 * @param enabled
	 */
	void setEnabled(boolean enabled);

	/**
	 * Gets inappropriate.
	 * 
	 * @return inappropriate
	 */
	boolean isInappropriate();

	/**
	 * Sets inappropriate.
	 * 
	 * @param inappropriate
	 *            inappropriate
	 */
	void setInappropriate(boolean inappropriate);
}
