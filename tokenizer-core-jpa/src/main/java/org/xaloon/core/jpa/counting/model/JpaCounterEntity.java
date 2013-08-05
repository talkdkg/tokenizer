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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "XAL_COUNTER")
public class JpaCounterEntity implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private JpaCounterId counterId;

    @Column(name = "VALUE_COUNT", nullable = false)
    private Long count;

    /**
     * Gets count.
     * 
     * @return count
     */
    public Long getCount() {
        return count;
    }

    /**
     * Sets count.
     * 
     * @param count
     *            count
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Gets counterId.
     * 
     * @return counterId
     */
    public JpaCounterId getCounterId() {
        return counterId;
    }

    /**
     * Sets counterId.
     * 
     * @param counterId
     *            counterId
     */
    public void setCounterId(JpaCounterId counterId) {
        this.counterId = counterId;
    }
}
