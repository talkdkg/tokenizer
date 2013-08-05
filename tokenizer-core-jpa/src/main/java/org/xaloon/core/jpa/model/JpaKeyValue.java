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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_KEY_VALUE")
public class JpaKeyValue extends MappableKeyValue {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

}
