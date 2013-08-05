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
package org.xaloon.core.impl.counting;

import org.xaloon.core.api.asynchronous.JobParameters;

/**
 * @author vytautas r.
 */
public class CounterJobParameters implements JobParameters {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String counterGroup;

    private Long entityId;

    private Long categoryId;

    private boolean increment = true;

    /**
     * Gets increment.
     * 
     * @return increment
     */
    public boolean isIncrement() {
        return increment;
    }

    /**
     * Sets increment.
     * 
     * @param increment
     *            increment
     */
    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

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
    public String toString() {
        return String.format("Counter group [%s], Category [%s], Entity Id [%s]", counterGroup, categoryId, entityId);
    }
}
