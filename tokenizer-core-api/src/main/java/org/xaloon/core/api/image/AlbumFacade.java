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

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;

import org.xaloon.core.api.image.model.Album;
import org.xaloon.core.api.image.model.Image;
import org.xaloon.core.api.image.model.ImageComposition;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface AlbumFacade extends Serializable {

    /**
     * @return new instance of album implementation class
     */
    Album newAlbum();

    /**
     * @return new instance of image implementation class
     */
    Image newImage();

    /**
     * Creates new album entity and populates with provided properties. However, this method does not persist it.
     * 
     * @param owner
     *            the owner of the new album
     * @param title
     *            the title of the new album
     * @param description
     *            the description of the new album. Optional
     * @param parent
     *            the parent of the new album. Optional
     * @return new album created
     */
    Album createNewAlbum(User owner, String title, String description, Album parent);

    /**
     * Adds new images to existing album. Ignores images, which are already added to the album
     * 
     * @param album
     *            the album where new images will be added
     * @param imagesToAdd
     *            list of new images to be added to the existing album
     * @param imageLocation
     * @param thumbnailLocation
     */
    void addNewImagesToAlbum(Album album, List<ImageComposition> imagesToAdd, String imageLocation,
            String thumbnailLocation);

    /**
     * Delete image physical files from storage
     * 
     * @param album
     *            album which should be updated also
     * @param imagesToDelete
     *            list of images to be deleted
     */
    void deleteImages(Album album, List<ImageComposition> imagesToDelete);

    /**
     * Deletes selected album and it's photos from file repository
     * 
     * @param imageAlbum
     *            image album to delete
     */
    void deleteAlbum(Album imageAlbum);

    /**
     * 
     * @param thumbnailToAdd
     * @return file descriptor of newly created file
     * @throws MalformedURLException
     * @throws IOException
     * 
     * @see ImageDao#createPhysicalFile(Image, FileDescriptor)
     */
    FileDescriptor createPhysicalFile(Image thumbnailToAdd) throws MalformedURLException, IOException;

    /**
     * Parses image and stores it.
     * 
     * @param temporaryImage
     * @param existingToUpdate
     * @param imageLocation
     * 
     * @return file descriptor
     * @throws MalformedURLException
     * @throws IOException
     */
    FileDescriptor createPhysicalFile(Image temporaryImage, FileDescriptor existingToUpdate)
            throws MalformedURLException, IOException;

    /**
     * Update existing image
     * 
     * @param image
     *            image to update
     */
    void save(org.xaloon.core.api.image.model.Image image);

    /**
     * Delete all images by provided username
     * 
     * @param userToBeDeleted
     */
    void deleteImagesByUsername(User userToBeDeleted);

    /**
     * Delete all albums by provided username
     * 
     * @param userToBeDeleted
     */
    void deleteAlbumsByUsername(User userToBeDeleted);

    List<ImageComposition> getImagesByAlbum(Album album);

    <T extends Album> void uploadThumbnail(T album, Image thumbnailToAdd, String resolveThumbnailLocation);

    <T extends Image> void uploadThumbnail(T image, Image thumbnailToAdd, String resolveThumbnailLocation);

    Image getImageByPath(String path);
}
