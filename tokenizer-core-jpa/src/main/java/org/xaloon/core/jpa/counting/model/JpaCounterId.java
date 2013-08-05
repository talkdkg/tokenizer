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
package org.xaloon.core.jpa.counting.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author vytautas r.
 */
@Embeddable
public class JpaCounterId implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Column(name = "COUNTER_GROUP_ID", nullable = false)
    private String counterGroup;

    @Column(name = "ENTITY_ID", nullable = false)
    private Long entityId;

    @Column(name = "CATEGORY_ID", nullable = false)
    private Long categoryId;

    /**
     * Gets counterGroup.
     * 
     * @return counterGroup
     */
    public String getCounterGroup() {
        return counterGroup;
    }

    /**
     * Sets counterGroup.
     * 
     * @param counterGroup
     *            counterGroup
     */
    public void setCounterGroup(String counterGroup) {
        this.counterGroup = counterGroup;
    }

    /**
     * Gets entityId.
     * 
     * @return entityId
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets entityId.
     * 
     * @param entityId
     *            entityId
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets categoryId.
     * 
     * @return categoryId
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets categoryId.
     * 
     * @param categoryId
     *            categoryId
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JpaCounterId)) {
            return false;
        }
        JpaCounterId entity = (JpaCounterId) obj;

        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getCounterGroup(), entity.getCounterGroup());
        equalsBuilder.append(getCategoryId(), entity.getCategoryId());
        equalsBuilder.append(getEntityId(), entity.getEntityId());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getCounterGroup());
        hashCodeBuilder.append(getCategoryId());
        hashCodeBuilder.append(getEntityId());
        return hashCodeBuilder.hashCode();
    }
}
