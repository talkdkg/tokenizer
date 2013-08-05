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
package org.xaloon.core.api.asynchronous;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * Defines interface which will execute {@link ScheduledJobService} job with specified parameters asynchronously or
 * schedule a job
 * 
 * @author vytautas r.
 */
public interface SchedulerServices extends Serializable {

    /**
     * @param <V>
     * @param <T>
     * @param scheduledJobService
     * @param jobParameters
     * @return asynchronous result if required
     */
    <V, T extends JobParameters> Future<V> runAsynchronous(ScheduledJobService<T> scheduledJobService, T jobParameters);

}
