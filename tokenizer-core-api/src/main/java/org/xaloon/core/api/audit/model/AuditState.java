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

/**
 * Default available audit states.
 * 
 * @author vytautas r.
 * 
 */
public enum AuditState {
	/** indicates, that object is created **/
	CREATE,

	/** indicates, that object is updated **/
	UPDATE,

	/** indicates, that object is deleted **/
	DELETE,

	/** ndicates, that object is created or updated **/
	CREATE_OR_UPDATE,

	/** if there is no listed object state **/
	OTHER
}
