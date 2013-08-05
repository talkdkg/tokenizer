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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.xaloon.core.api.message.model.Message;
import org.xaloon.core.api.user.model.User;
import org.xaloon.core.jpa.model.AbstractEntity;
import org.xaloon.core.jpa.storage.model.JpaFileDescriptor;
import org.xaloon.core.jpa.user.model.AbstractUser;

/**
 * @author vytautas r.
 */
@MappedSuperclass
public abstract class JpaMessage extends AbstractEntity implements Message {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "FROM_USER_ID", referencedColumnName = "ID")
    private AbstractUser fromUser;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @ManyToMany
    @JoinTable(name = "XAL_MESSAGE_ATTACHMENTS", joinColumns = @JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID", referencedColumnName = "ID"))
    private List<JpaFileDescriptor> attachments = new ArrayList<JpaFileDescriptor>();

    public AbstractUser getFromUser() {
        return fromUser;
    }

    /**
     * @param fromUser
     */
    public void setFromUser(AbstractUser fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<JpaFileDescriptor> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments
     */
    public void setAttachments(List<JpaFileDescriptor> attachments) {
        this.attachments = attachments;
    }

    @Override
    public void setFromUser(User fromUser) {
        this.fromUser = (AbstractUser) fromUser;
    }
}
