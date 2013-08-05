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

import org.xaloon.core.api.image.model.Album;
import org.xaloon.core.api.image.model.Image;
import org.xaloon.core.api.image.model.ImageComposition;

/**
 * File repository to manage image upload
 * 
 * @author vytautas r.
 */
public interface ImageRepository extends Serializable {
	void uploadImage(ImageComposition composition, ImageOptions options);

	ImageRepository getAlternativeImageRepository();

	<T extends Album> void uploadThumbnail(T album, Image image, ImageOptions options);

	<T extends Image> void uploadThumbnail(T Image, Image image, ImageOptions options);
}
