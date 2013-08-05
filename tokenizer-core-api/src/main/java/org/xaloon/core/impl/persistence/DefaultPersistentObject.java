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
package org.xaloon.core.impl.persistence;

import java.util.Date;

import org.xaloon.core.api.bookmark.Bookmarkable;
import org.xaloon.core.api.persistence.Persistable;

/**
 * Default entity in memory
 * 
 * @author vytautas r.
 */
public class DefaultPersistentObject implements Persistable, Bookmarkable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Long id;

    private Date createDate;

    private Date updateDate;

    private String path;

    /**
     * Gets id.
     * 
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     * 
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets createDate.
     * 
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets createDate.
     * 
     * @param createDate
     *            createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    /**
     * Gets path.
     * 
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path.
     * 
     * @param path
     *            path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
