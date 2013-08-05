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
package org.xaloon.core.impl.asynchronous;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.asynchronous.JobParameters;
import org.xaloon.core.api.asynchronous.ScheduledJobService;
import org.xaloon.core.api.asynchronous.SchedulerServices;

/**
 * @author vytautas r.
 */
@Singleton
@Named("schedulerServices")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DefaultSchedulerServices implements SchedulerServices {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSchedulerServices.class);

	private ExecutorService executor;

	private int fixedThreadCount = 20;

	@SuppressWarnings("unchecked")
	public <V, T extends JobParameters> java.util.concurrent.Future<V> runAsynchronous(final ScheduledJobService<T> scheduledJobService,
		final T jobParameters) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("%s.runAsynchronous() start", DefaultSchedulerServices.class.getName()));
		}
		Future<V> f = getExecutor().submit(new Callable<V>() {
			@Override
			public V call() throws Exception {
				return (V)scheduledJobService.execute(jobParameters, true);
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("%s.runAsynchronous() end", DefaultSchedulerServices.class.getName()));
		}
		return f;
	};

	ExecutorService getExecutor() {
		if (executor == null) {
			executor = Executors.newFixedThreadPool(fixedThreadCount);
		}
		return executor;
	}

	public DefaultSchedulerServices setFixedThreadCount(int fixedThreadCount) {
		this.fixedThreadCount = fixedThreadCount;
		return this;
	}


}
