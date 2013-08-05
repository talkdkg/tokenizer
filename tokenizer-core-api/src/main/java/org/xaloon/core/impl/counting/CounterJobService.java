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
package org.xaloon.core.impl.counting;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.asynchronous.RetryAction;
import org.xaloon.core.api.asynchronous.ScheduledJobService;
import org.xaloon.core.api.counting.CounterDao;

/**
 * @author vytautas r.
 */
@Named("counterJobService")
public class CounterJobService implements ScheduledJobService<CounterJobParameters> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CounterJobService.class);

	@Inject
	@Named("counterDao")
	private CounterDao counterDao;

	@Override
	public <V> V execute(CounterJobParameters jobParameters, boolean isScheduled) {
		try {
			Boolean result = executeJob(jobParameters);
			if (LOGGER.isDebugEnabled()) {
				if (result == null) {
					LOGGER.debug(String.format("[%s] Could not increment/decrement. Giving up.", Thread.currentThread().getName()));
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return null;
	}


	private Boolean executeJob(final CounterJobParameters jobParameters) throws InterruptedException {
		return new RetryAction<Boolean, CounterJobParameters>(false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Boolean onPerform(CounterJobParameters parameters) {
				boolean success = false;
				if (jobParameters.isIncrement()) {
					success = counterDao.increment(jobParameters.getCounterGroup(), jobParameters.getCategoryId(), jobParameters.getEntityId());
				} else {
					success = counterDao.decrement(jobParameters.getCounterGroup(), jobParameters.getCategoryId(), jobParameters.getEntityId());
				}
				// In case of failure we return null. It means we retry action
				return (success) ? success : null;
			}

		}.setMillisecondsToSleep(10000).setRetryCount(10).setRandomTimeUsed(true).perform(jobParameters);
	}
}
