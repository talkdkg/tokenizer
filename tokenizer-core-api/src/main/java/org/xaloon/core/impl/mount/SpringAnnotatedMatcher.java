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
package org.xaloon.core.impl.mount;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.inject.Named;

import org.xaloon.core.api.annotation.AnnotatedMatcher;

/**
 * @author vytautas r.
 */
@Named("springAnnotatedMatcher")
public class SpringAnnotatedMatcher implements AnnotatedMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<Class<?>> getAnnotatedMatches(String pattern, Class<? extends Annotation> annotation) {
		MatchingResources resources = new MatchingResources(pattern);
		return resources.getAnnotatedMatches(annotation);
	}

}
