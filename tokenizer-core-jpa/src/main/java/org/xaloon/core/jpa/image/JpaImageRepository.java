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
package org.xaloon.core.jpa.image;

import java.io.IOException;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.image.AbstractImageRepository;
import org.xaloon.core.api.image.ImageOptions;
import org.xaloon.core.api.image.ImageRepository;
import org.xaloon.core.api.image.model.Image;
import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.storage.FileDescriptorDao;
import org.xaloon.core.api.storage.FileStorageService;
import org.xaloon.core.api.storage.InputStreamContainer;

@Named("jpaImageRepository")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaImageRepository extends AbstractImageRepository {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	@Named(FileStorageService.FILE_STORAGE_SERVICE_JPA)
	private FileStorageService fileStorageService;

	@Inject
	private FileDescriptorDao fileDescriptorDao;

	@Override
	public ImageRepository getAlternativeImageRepository() {
		// No alternative image repository
		return null;
	}

	@Override
	protected FileStorageService getFileStorageService() {
		return fileStorageService;
	}

	@Override
	protected KeyValue<String, String> storeFile(Image image, ImageOptions options) throws IOException {
		InputStreamContainer resizedInputStreamContainer = resize(options);
		return getFileStorageService().storeFile(image, resizedInputStreamContainer);
	}
}
