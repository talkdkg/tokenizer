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
package org.xaloon.core.jpa.audit.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.xaloon.core.api.audit.model.AuditEntity;
import org.xaloon.core.api.audit.model.AuditEntityItem;
import org.xaloon.core.jpa.model.AbstractEntity;

/**
 * @author vytautas r.
 */
@Cacheable
@Entity
@Table(name = "XAL_AUDIT_ENTITY_ITEM")
public class JpaAuditEntityItem extends AbstractEntity implements AuditEntityItem {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUDIT_ENTITY_ID", referencedColumnName = "ID")
    private JpaAuditEntity auditEntity;

    @Column(name = "FIELD_NAME", length = 255)
    private String name;

    @Column(name = "FIELD_VALUE", length = 2000)
    private String value;

    private transient boolean key;

    /**
     * Gets auditEntity.
     * 
     * @return auditEntity
     */
    public JpaAuditEntity getAuditEntity() {
        return auditEntity;
    }

    /**
     * Sets auditEntity.
     * 
     * @param auditEntity
     *            auditEntity
     */
    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = (JpaAuditEntity) auditEntity;
    }

    /**
     * Gets name.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets value.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     * 
     * @param value
     *            value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets key.
     * 
     * @return key
     */
    public boolean isKey() {
        return key;
    }

    /**
     * Sets key.
     * 
     * @param key
     *            key
     */
    public void setKey(boolean key) {
        this.key = key;
    }
}
