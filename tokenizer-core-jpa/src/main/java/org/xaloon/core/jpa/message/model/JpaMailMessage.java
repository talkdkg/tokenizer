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
package org.xaloon.core.jpa.message.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.xaloon.core.api.message.model.MailMessage;
import org.xaloon.core.api.user.model.User;
import org.xaloon.core.jpa.user.model.JpaUser;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_MAIL_MESSAGE")
@DiscriminatorValue("MAIL_MESSAGE")
public class JpaMailMessage extends JpaTextMessage implements MailMessage {
    private static final long serialVersionUID = 1L;

    @Column(name = "IS_READ", nullable = false)
    private boolean read;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "TO_USER_ID", referencedColumnName = "ID")
    private JpaUser toUser;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public User getToUser() {
        return toUser;
    }

    /**
     * @param toUser
     */
    public void setToUser(JpaUser toUser) {
        this.toUser = toUser;
    }

    @Override
    public void setToUser(User toUser) {
        this.toUser = (JpaUser) toUser;
    }
}
