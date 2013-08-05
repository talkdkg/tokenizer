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
package org.xaloon.core.api.interceptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.util.ClassUtil;

/**
 * @author vytautas r.
 * @param <T>
 */
public abstract class AbstractInterceptor<T extends Annotation> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInterceptor.class);

	/**
	 * @param ic
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		Method method = ic.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			method = ClassUtil.getMethodIfAvailable(ic.getTarget().getClass(), method.getName(), method.getParameterTypes());
		}
		if (method == null) {
			throw new RuntimeException("Method not found!");
		}
		Signature signature = new Signature().setMethodName(method.getName())
			.setDeclaringType(method.getDeclaringClass())
			.setDeclaringTypeName(method.getDeclaringClass().getName())
			.setParameters(ic.getParameters());
		try {
			beforeProceed(signature, method.getAnnotation(getAnnotationClass()));
		} catch (Exception e) {
			LOGGER.error("Could not perform action.", e);
		}
		return ic.proceed();
	}

	/**
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	public Object aroundInvokeAspectJ(ProceedingJoinPoint pjp) throws Throwable {
		org.aspectj.lang.Signature s = pjp.getSignature();
		final MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
		Method method = methodSignature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			method = ClassUtil.getMethodIfAvailable(pjp.getTarget().getClass(), method.getName(), method.getParameterTypes());
		}
		if (method == null) {
			throw new RuntimeException("Method not found!");
		}
		Signature signature = new Signature().setMethodName(s.getName())
			.setDeclaringType(s.getDeclaringType())
			.setDeclaringTypeName(s.getDeclaringTypeName())
			.setParameters(pjp.getArgs());
		beforeProceed(signature, method.getAnnotation(getAnnotationClass()));
		return pjp.proceed();
	}

	protected abstract Class<T> getAnnotationClass();

	protected abstract void beforeProceed(Signature signature, T annotation);
}
