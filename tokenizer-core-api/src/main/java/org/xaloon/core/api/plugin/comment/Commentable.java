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

import org.xaloon.core.api.counting.Countable;

/**
 * Interface is used to identify the object which is commentable
 * 
 * @author vytautas r.
 */
public interface Commentable extends Countable {
	/**
	 * Returns the author username of commentable object. This might be required to check if current user is the same as author of commentable object.
	 * 
	 * @return username who created commentable object.
	 */
	String getOwnerUsername();
}
