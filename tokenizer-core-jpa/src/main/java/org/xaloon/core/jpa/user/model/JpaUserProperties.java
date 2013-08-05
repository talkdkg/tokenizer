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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.xaloon.core.jpa.model.MappableKeyValue;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_USER_PROPERTY")
public class JpaUserProperties extends MappableKeyValue {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private JpaUser user;

    /**
     * Gets user.
     * 
     * @return user
     */
    public JpaUser getUser() {
        return user;
    }

    /**
     * Sets user.
     * 
     * @param user
     *            user
     */
    public void setUser(JpaUser user) {
        this.user = user;
    }
}
