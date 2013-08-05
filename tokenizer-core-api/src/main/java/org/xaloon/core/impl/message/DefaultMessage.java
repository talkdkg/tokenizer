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
package org.xaloon.core.impl.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xaloon.core.api.message.model.Message;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public class DefaultMessage implements Message {
    private static final long serialVersionUID = 1L;

    private Date createDate;

    private User fromUser;

    private String subject;

    private String message;

    private Date updateDate = new Date();

    private List<FileDescriptor> attachments = new ArrayList<FileDescriptor>();

    /**
     * @see org.xaloon.core.message.Message#getCreateDate()
     */
    @Override
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @see org.xaloon.core.message.Message#setCreateDate(java.util.Date)
     */
    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @see org.xaloon.core.message.Message#getFromUser()
     */
    @Override
    public User getFromUser() {
        return fromUser;
    }

    /**
     * @see org.xaloon.core.message.Message#setFromUser(org.xaloon.core.api.user.model.User)
     */
    @Override
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    /**
     * @see org.xaloon.core.message.Message#getMessage()
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * @see org.xaloon.core.message.Message#setMessage(java.lang.String)
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @see org.xaloon.core.message.Message#getAttachments()
     */
    @Override
    public List<FileDescriptor> getAttachments() {
        return attachments;
    }

    @Override
    public Long getId() {
        throw new RuntimeException("Not persistable instance!");
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void setId(Long id) {
        throw new RuntimeException("Not persistable instance!");
    }

    /**
     * Gets updateDate.
     * 
     * @return updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets updateDate.
     * 
     * @param updateDate
     *            updateDate
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
