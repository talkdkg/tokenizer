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
package org.xaloon.core.jpa.storage.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.xaloon.core.jpa.model.AbstractEntity;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_FILE_STORAGE")
public class JpaFileStorage extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Lob
	@Column(name = "BINARY_DATA")
	private byte[] file;

	/**
	 * @return lazily fetched file
	 */
	public byte[] getFile() {
		return file;
	}

	/**
	 * @param file
	 */
	public void setFile(byte[] file) {
		this.file = file;
	}
}
