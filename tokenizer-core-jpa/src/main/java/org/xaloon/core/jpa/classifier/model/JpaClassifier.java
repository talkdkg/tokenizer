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
package org.xaloon.core.jpa.classifier.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.xaloon.core.api.classifier.Classifier;
import org.xaloon.core.jpa.model.AbstractEntity;

/**
 * http://www.xaloon.org
 * 
 * @author vytautas r.
 * @since 1.3
 */
@Cacheable
@Entity
@Table(name = "XAL_CLASSIFIER", uniqueConstraints = @UniqueConstraint(columnNames = { "CL_TYPE" }))
public class JpaClassifier extends AbstractEntity implements Classifier {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Column(name = "CL_TYPE", nullable = false)
    private String type;

    @Column(name = "CL_NAME", nullable = false)
    private String name;

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @see ClassifierType
     * @return type of classifier
     */
    public String getType() {
        return type;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of classifier
     */
    public String getName() {
        return name;
    }

    @Override
    @PrePersist
    public void beforeCreate() {
        super.beforeCreate();
        setType(getType().toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("[%s] type=%s, name=%s", this.getClass().getSimpleName(), getType(), getName());
    }
}
