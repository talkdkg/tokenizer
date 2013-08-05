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
package org.xaloon.core.jpa;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;

import org.xaloon.core.jpa.model.JpaKeyValue;

/**
 * @author vytautas r.
 */
@Named("keyValueDao")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaKeyValueDao extends AbstractKeyValueDao<JpaKeyValue> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Override
    protected Class<JpaKeyValue> getImplementationClass() {
        return JpaKeyValue.class;
    }
}
