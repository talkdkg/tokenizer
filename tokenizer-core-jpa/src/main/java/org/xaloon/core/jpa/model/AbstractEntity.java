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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.xaloon.core.api.persistence.Persistable;

/**
 * Base entity for JPA
 * 
 * @author vytautas r.
 */
@MappedSuperclass
public abstract class AbstractEntity implements Persistable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "UPDATE_DATE", updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    /**
     * @return unique identifier of entity
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return date when instance was created
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return date when instance was updated
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return true if entity is not persisted yet
     */
    public boolean isNew() {
        return (id == null);
    }

    @PreUpdate
    protected void beforeUpdate() {
        setUpdateDate(new Date());
    }

    @PrePersist
    protected void beforeCreate() {
        if (getCreateDate() == null) {
            Date cd = new Date();
            setCreateDate(cd);
            setUpdateDate(cd);
        }
    }
}
