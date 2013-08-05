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
package org.xaloon.core.api.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author vytautas r.
 */
public interface AnnotatedMatcher extends Serializable {

	/**
	 * Get all matching classes that are annotated with the given Annotation. Note that this method only returns those classes that actually contain
	 * the annotation, and does not return classes whose superclass contains the annotation.
	 * 
	 * @param pattern
	 *            the pattern to search for
	 * @param annotation
	 *            an annotation class
	 * @return List of all classes that have the given annotation. List is empty if non matches found.
	 */
	List<Class<?>> getAnnotatedMatches(String pattern, Class<? extends Annotation> annotation);

}
