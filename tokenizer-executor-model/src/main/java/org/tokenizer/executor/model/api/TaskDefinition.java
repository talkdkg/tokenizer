/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.executor.model.api;

import java.util.Map;

/**
 * Defines configuration for a single domain/host/task
 * 
 * @author Fuad
 * 
 */
public interface TaskDefinition {

	String getName();

	TaskGeneralState getGeneralState();

	void setGeneralState(TaskGeneralState state);

	TaskBatchBuildState getBatchBuildState();

	void setBatchBuildState(TaskBatchBuildState state);

	/**
	 * The XML configuration for the Task.
	 */
	byte[] getConfiguration();

	void setConfiguration(byte[] configuration);

	int getZkDataVersion();

	public long getSubmitTime();

	public void setSubmitTime(long submitTime);

	public Map<String, Long> getCounters();

	public void addCounter(String key, Long value);

	public long getMetricsUpdateTimestamp();

	public void setMetricsUpdateTimestamp(long metricsUpdateTimestamp);

}
