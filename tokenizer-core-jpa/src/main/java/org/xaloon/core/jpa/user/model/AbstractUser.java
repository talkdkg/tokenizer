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
package org.xaloon.core.jpa.user.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.xaloon.core.api.user.model.User;
import org.xaloon.core.jpa.model.AbstractEntity;

/**
 * @author vytautas r.
 */
@Cacheable
@Entity
@Table(name = "XAL_USER_ABSTRACT")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "USER_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractUser extends AbstractEntity implements User {
    private static final long serialVersionUID = 1L;

    @Column(name = "WEBSITE")
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
