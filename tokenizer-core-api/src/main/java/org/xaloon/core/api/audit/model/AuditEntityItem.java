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

import org.xaloon.core.api.persistence.Persistable;

/**
 * @author vytautas r.
 */
public interface AuditEntityItem extends Persistable {
    /**
     * Gets auditEntity.
     * 
     * @return auditEntity
     */
    AuditEntity getAuditEntity();

    /**
     * Sets auditEntity.
     * 
     * @param auditEntity
     *            auditEntity
     */
    void setAuditEntity(AuditEntity auditEntity);

    /**
     * Gets name.
     * 
     * @return name
     */
    String getName();

    /**
     * Sets name.
     * 
     * @param name
     *            name
     */
    void setName(String name);

    /**
     * Gets value.
     * 
     * @return value
     */
    String getValue();

    /**
     * Sets value.
     * 
     * @param value
     *            value
     */
    void setValue(String value);

    /**
     * Gets key.
     * 
     * @return key
     */
    boolean isKey();

    /**
     * Sets key.
     * 
     * @param key
     *            key
     */
    void setKey(boolean key);
}
