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
package org.xaloon.core.api.audit.model;

import java.util.List;

import org.xaloon.core.api.bookmark.Bookmarkable;
import org.xaloon.core.api.persistence.Persistable;

/**
 * @author vytautas r.
 */
public interface AuditEntity extends Persistable, Bookmarkable {
	/**
	 * Gets auditEntityItems.
	 * 
	 * @return auditEntityItems
	 */
	<T extends AuditEntityItem> List<T> getAuditEntityItems();

	/**
	 * Gets auditState.
	 * 
	 * @return auditState
	 */
	AuditState getAuditState();

	/**
	 * Sets auditState.
	 * 
	 * @param auditState
	 *            auditState
	 */
	void setAuditState(AuditState auditState);

	/**
	 * Gets auditableName.
	 * 
	 * @return auditableName
	 */
	String getAuditableName();

	/**
	 * Sets auditableName.
	 * 
	 * @param auditableName
	 *            auditableName
	 */
	void setAuditableName(String auditableName);
}
