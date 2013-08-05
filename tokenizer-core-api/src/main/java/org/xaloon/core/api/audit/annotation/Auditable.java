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
package org.xaloon.core.api.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation should be added to class header in case it should be audited.
 * <p>
 * 
 * @Auditable public class A {
 * @Auditable private String name; }
 * @author vytautas r.
 * 
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
	/**
	 * Used by list auditing. Identifies which list entry should be audited
	 * 
	 * @return integer index, indicating which list entry will be audited
	 */
	int index() default -1;

	/**
	 * 
	 * @return
	 */
	boolean key() default false;
}
