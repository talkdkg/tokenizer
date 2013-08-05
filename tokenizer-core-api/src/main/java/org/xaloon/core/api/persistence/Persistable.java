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
package org.xaloon.core.api.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * @author vytautas r.
 */
public interface Persistable extends Serializable {
	/**
	 * @return unique object identifier
	 */
	Long getId();

	/**
	 * Unique identifier to set
	 * 
	 * @param id
	 */
	void setId(Long id);

	/**
	 * @return true if entity is not stored into database yet
	 */
	boolean isNew();

	/**
	 * @return date when instance was created
	 */
	Date getCreateDate();

	/**
	 * @param createDate
	 */
	void setCreateDate(Date createDate);

	/**
	 * @param updateDate
	 */
	void setUpdateDate(Date updateDate);

	/**
	 * @return date when instance was updated
	 */
	Date getUpdateDate();
}
