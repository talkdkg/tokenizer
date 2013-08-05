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

/**
 * @author vytautas r.
 * @param <T>
 */
public interface ScheduledJobService<T extends JobParameters> {
	/**
	 * @param <V>
	 * @param jobParameters
	 * @param isScheduled
	 * @return any calculated result if necessary
	 */
	<V> V execute(T jobParameters, boolean isScheduled);
}
