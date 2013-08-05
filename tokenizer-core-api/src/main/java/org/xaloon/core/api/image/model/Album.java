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
package org.xaloon.core.api.image.model;

import java.util.List;

import org.xaloon.core.api.persistence.Persistable;
import org.xaloon.core.api.plugin.comment.Commentable;
import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.user.model.User;


/**
 * @author vytautas r.
 */
public interface Album extends Persistable, Commentable {

	/**
	 * Gets title.
	 * 
	 * @return title
	 */
	String getTitle();

	/**
	 * Sets title.
	 * 
	 * @param title
	 *            title
	 */
	void setTitle(String title);

	/**
	 * Gets description.
	 * 
	 * @return description
	 */
	String getDescription();

	/**
	 * Sets description.
	 * 
	 * @param description
	 *            description
	 */
	void setDescription(String description);

	/**
	 * Gets parent.
	 * 
	 * @return parent
	 */
	Album getParent();

	/**
	 * Sets parent.
	 * 
	 * @param parent
	 *            parent
	 */
	void setParent(Album parent);

	/**
	 * Gets thumbnail.
	 * 
	 * @return thumbnail
	 */
	FileDescriptor getThumbnail();

	/**
	 * Sets thumbnail.
	 * 
	 * @param thumbnail
	 *            thumbnail
	 */
	void setThumbnail(FileDescriptor thumbnail);

	/**
	 * Gets images.
	 * 
	 * @return images
	 */
	<T extends ImageComposition> List<T> getImages();

	/**
	 * Sets images.
	 * 
	 * @param images
	 *            images
	 */
	void setImages(List<? extends ImageComposition> images);

	/**
	 * Gets owner.
	 * 
	 * @return owner
	 */
	User getOwner();

	/**
	 * Sets owner.
	 * 
	 * @param owner
	 *            owner
	 */
	void setOwner(User owner);

}
