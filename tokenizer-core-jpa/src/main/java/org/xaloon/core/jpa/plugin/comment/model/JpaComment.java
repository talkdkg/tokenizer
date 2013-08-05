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
package org.xaloon.core.jpa.plugin.comment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.xaloon.core.api.plugin.comment.Comment;
import org.xaloon.core.jpa.message.model.JpaMessage;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_COMMENT")
public class JpaComment extends JpaMessage implements Comment {
    private static final long serialVersionUID = 1L;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "ENTITY_ID", nullable = false)
    private Long entityId;

    @Column(name = "CATEGORY_ID", nullable = false)
    private Long categoryId;

    @Column(name = "PATH")
    private String path;

    @Column(name = "INAPPROPRIATE")
    private boolean inappropriate;

    /**
     * Gets inappropriate.
     * 
     * @return inappropriate
     */
    public boolean isInappropriate() {
        return inappropriate;
    }

    /**
     * Sets inappropriate.
     * 
     * @param inappropriate
     *            inappropriate
     */
    public void setInappropriate(boolean inappropriate) {
        this.inappropriate = inappropriate;
    }

    /**
     * @return path where comment was posted
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
