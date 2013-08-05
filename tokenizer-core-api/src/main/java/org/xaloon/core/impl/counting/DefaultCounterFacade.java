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

import org.xaloon.core.api.asynchronous.ScheduledJobService;
import org.xaloon.core.api.asynchronous.SchedulerServices;
import org.xaloon.core.api.counting.CounterDao;
import org.xaloon.core.api.counting.CounterFacade;
import org.xaloon.core.api.inject.ServiceLocator;

/**
 * @author vytautas r.
 */
@Named("counterFacade")
public class DefaultCounterFacade implements CounterFacade {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Inject
    @Named("counterDao")
    private CounterDao counterDao;

    @Inject
    @Named("counterJobService")
    private ScheduledJobService<CounterJobParameters> scheduledJobService;

    /**
     * Manually injected service. This allows to check if exists {@link SchedulerServices} implementation.
     */
    private SchedulerServices schedulerServices;

    private SchedulerServices getSchedulerServices() {
        if (schedulerServices == null) {
            schedulerServices = ServiceLocator.get().getInstance(SchedulerServices.class);
        }
        return schedulerServices;
    }

    @Override
    public boolean increment(String counterGroup, Long categoryId, Long entityId) {
        // Schedule and start asynchronous job to increment value
        CounterJobParameters params = new CounterJobParameters();
        params.setCounterGroup(counterGroup);
        params.setCategoryId(categoryId);
        params.setEntityId(entityId);

        getSchedulerServices().runAsynchronous(scheduledJobService, params);
        return true;
    }

    @Override
    public Long count(String counterGroup, Long categoryId, Long entityId) {
        return counterDao.count(counterGroup, categoryId, entityId);
    }

    @Override
    public boolean decrement(String counterGroup, Long categoryId, Long entityId) {
        CounterJobParameters params = new CounterJobParameters();
        params.setCounterGroup(counterGroup);
        params.setCategoryId(categoryId);
        params.setEntityId(entityId);
        params.setIncrement(false);
        getSchedulerServices().runAsynchronous(scheduledJobService, params);
        return true;
    }
}
