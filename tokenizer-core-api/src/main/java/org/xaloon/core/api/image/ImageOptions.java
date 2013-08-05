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
package org.xaloon.core.api.image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xaloon.core.api.image.model.Album;
import org.xaloon.core.api.storage.InputStreamContainer;

public class ImageOptions implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private ImageSize imageSize;

    private List<ImageSize> additionalImageSizes = new ArrayList<ImageSize>();

    private boolean modifyPath;

    private boolean generateUuid;

    private String pathPrefix;

    private InputStreamContainer imageInputStreamContainer;

    private Long albumId;

    private Class<? extends Album> albumEntityClass;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Construct.
     * 
     * @param imageInputStreamContainer
     * @param imageSize
     */
    public ImageOptions(InputStreamContainer imageInputStreamContainer, ImageSize imageSize) {
        this.imageInputStreamContainer = imageInputStreamContainer;
        this.imageSize = imageSize;
    }

    /**
     * Gets albumId.
     * 
     * @return albumId
     */
    public Long getAlbumId() {
        return albumId;
    }

    /**
     * Sets albumId.
     * 
     * @param albumId
     *            albumId
     */
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    /**
     * Gets albumEntityClass.
     * 
     * @return albumEntityClass
     */
    public Class<? extends Album> getAlbumEntityClass() {
        return albumEntityClass;
    }

    /**
     * Sets albumEntityClass.
     * 
     * @param albumEntityClass
     *            albumEntityClass
     */
    public void setAlbumEntityClass(Class<? extends Album> albumEntityClass) {
        this.albumEntityClass = albumEntityClass;
    }

    /**
     * Gets imageInputStreamContainer.
     * 
     * @return imageInputStreamContainer
     */
    public InputStreamContainer getImageInputStreamContainer() {
        return imageInputStreamContainer;
    }

    /**
     * Sets imageInputStreamContainer.
     * 
     * @param imageInputStreamContainer
     *            imageInputStreamContainer
     */
    public void setImageInputStreamContainer(InputStreamContainer imageInputStreamContainer) {
        this.imageInputStreamContainer = imageInputStreamContainer;
    }

    /**
     * Gets additionalImageSizes.
     * 
     * @return additionalImageSizes
     */
    public List<ImageSize> getAdditionalImageSizes() {
        return additionalImageSizes;
    }

    /**
     * Gets imageSize.
     * 
     * @return imageSize
     */
    public ImageSize getImageSize() {
        return imageSize;
    }

    /**
     * Sets imageSize.
     * 
     * @param imageSize
     *            imageSize
     */
    public void setImageSize(ImageSize imageSize) {
        this.imageSize = imageSize;
    }

    /**
     * Gets modifyPath.
     * 
     * @return modifyPath
     */
    public boolean isModifyPath() {
        return modifyPath;
    }

    /**
     * Sets modifyPath.
     * 
     * @param modifyPath
     *            modifyPath
     */
    public void setModifyPath(boolean modifyPath) {
        this.modifyPath = modifyPath;
    }

    /**
     * Gets generateUuid.
     * 
     * @return generateUuid
     */
    public boolean isGenerateUuid() {
        return generateUuid;
    }

    /**
     * Sets generateUuid.
     * 
     * @param generateUuid
     *            generateUuid
     */
    public void setGenerateUuid(boolean generateUuid) {
        this.generateUuid = generateUuid;
    }

    /**
     * Gets pathPrefix.
     * 
     * @return pathPrefix
     */
    public String getPathPrefix() {
        return pathPrefix;
    }

    /**
     * Sets pathPrefix.
     * 
     * @param pathPrefix
     *            pathPrefix
     */
    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

}
