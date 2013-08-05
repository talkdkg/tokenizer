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
package org.xaloon.core.api.storage;

import java.util.Date;

import org.xaloon.core.api.bookmark.Bookmarkable;
import org.xaloon.core.api.persistence.Persistable;

/**
 * @author vytautas r.
 */
public interface FileDescriptor extends Bookmarkable, Persistable {
    /**
     * Returns the name of file, located in repository
     * 
     * @return string form of this file name
     */
    String getName();

    /**
     * Registers the name of selected file. It represents file name in repository.
     * 
     * @param name
     *            form of this file name
     */
    void setName(String name);

    /**
     * Returns the date when file was modified last time
     * 
     * @return date format of file modification
     */
    Date getUpdateDate();

    /**
     * Registers date, when the file was modified last time.
     * 
     * @param lastModified
     *            date format of last modification of this component
     */
    void setUpdateDate(Date lastModified);

    /**
     * Returns the file type
     * 
     * @return string form of file type
     */
    String getMimeType();

    /**
     * Registers the file type
     * 
     * @param mimeType
     *            string form of file type stored in this component
     */
    void setMimeType(String mimeType);

    /**
     * @return file location if was provided
     */
    String getLocation();

    /**
     * @param location
     *            where the file is or should be stored
     */
    void setLocation(String location);

    /**
     * Registers file size
     * 
     * @param size
     *            in bytes
     */
    void setSize(Long size);

    /**
     * Returns the size of this file
     * 
     * @return file size in bytes
     */
    Long getSize();

    /**
     * Returns unique identifier of physical file storage location
     * 
     * @return unique identifier how easily file can be found
     */
    String getIdentifier();

    /**
     * Sets unique identifier of physical file storage location
     * 
     * @param identifier
     *            string representation of physical storage location
     */
    void setIdentifier(String identifier);

    /**
     * Checks if file path starts with 'http'
     * 
     * @return true if file is loaded from external link
     */
    boolean isExternal();

    /**
     * Returns file storage service provider bean name. It might be used if mixes are used to store files
     * 
     * @return string representation of service provider bean name
     */
    String getFileStorageServiceProvider();

    /**
     * 
     * @param fileStorageServiceProvider
     */
    void setFileStorageServiceProvider(String fileStorageServiceProvider);

    /**
     * Transient fields
     */

    /**
     * @return temporary image holder
     */
    InputStreamContainer getImageInputStreamContainer();

    /**
     * @param imageInputStreamContainer
     */
    void setImageInputStreamContainer(InputStreamContainer imageInputStreamContainer);
}
