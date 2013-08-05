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
package org.xaloon.core.jpa.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.xaloon.core.api.audit.annotation.Auditable;
import org.xaloon.core.api.bookmark.Bookmarkable;

/**
 * Contains bookmarkable path of entity.
 * 
 * @author vytautas r.
 * @since 1.5
 */
@MappedSuperclass
public abstract class BookmarkableEntity extends AbstractEntity implements Bookmarkable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "PATH", nullable = false)
	@Auditable
	private String path;

	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return bookmarkable path of the entity
	 */
	public String getPath() {
		return path;
	}
}
