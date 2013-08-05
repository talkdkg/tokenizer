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
package org.xaloon.core.api.security.external;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.storage.FileDescriptor;
import org.xaloon.core.api.storage.FileRepositoryFacade;
import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
@Named("externalParameterResolver")
public class ExternalParameterResolver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private FileRepositoryFacade fileRepository;


	/**
	 * @param initialToken
	 * @param user
	 * 
	 */
	public void resolve(AuthenticationToken initialToken, User user) {
		if (initialToken != null) {
			for (AuthenticationAttribute attribute : initialToken.getAttributes()) {
				if (AuthenticationConsumer.PARAM_FIRST_NAME.equals(attribute.getName())) {
					user.setFirstName(attribute.getValue());
				} else if (AuthenticationConsumer.PARAM_LAST_NAME.equals(attribute.getName())) {
					user.setLastName(attribute.getValue());
				} else if (AuthenticationConsumer.PARAM_EMAIL.equals(attribute.getName())) {
					user.setEmail(attribute.getValue());
				} else if (AuthenticationConsumer.PARAM_PICTURE_SMALL.equals(attribute.getName())) {
					FileDescriptor photoThumbnail = fileRepository.newFileDescriptor();
					photoThumbnail.setName(attribute.getValue());
					photoThumbnail.setPath(attribute.getValue());
					user.setPhotoThumbnail(photoThumbnail);
				}
			}
		}
	}
}
