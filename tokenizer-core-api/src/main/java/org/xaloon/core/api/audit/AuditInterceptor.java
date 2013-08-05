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
package org.xaloon.core.api.audit;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.audit.annotation.Audited;
import org.xaloon.core.api.interceptor.AbstractInterceptor;
import org.xaloon.core.api.interceptor.Signature;

/**
 * @author vytautas r.
 */
@Audited
@Interceptor
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AuditInterceptor extends AbstractInterceptor<Audited> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditInterceptor.class);

	@Inject
	private AuditFacade auditFacade;

	@Override
	protected void beforeProceed(final Signature signature, final Audited annotation) {
		Object[] params = signature.getParameters();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Audit interceptor got class[%s], method [%s] with state [%s], parameter count [%d], first parameter [%s]",
				signature.getDeclaringTypeName(), signature.getMethodName(), annotation.state(), params.length, (params.length > 0) ? params[0] : ""));
		}
		if (params != null && params.length > 0) {
			auditFacade.audit(annotation.state(), params[0]);
		}
	}

	@Override
	protected Class<Audited> getAnnotationClass() {
		return Audited.class;
	}
}
