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

/**
 * 
 * @author Fuad
 * 
 */
public class ExecutorModelEvent {
	private ExecutorModelEventType type;
	private String taskDefinitionName;

	public ExecutorModelEvent(ExecutorModelEventType type, String taskDefinitionName) {
		this.type = type;
		this.taskDefinitionName = taskDefinitionName;
	}

	public ExecutorModelEventType getType() {
		return type;
	}

	public String getTaskDefinitionName() {
		return taskDefinitionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		result = prime * result + taskDefinitionName.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutorModelEvent other = (ExecutorModelEvent) obj;
		return other.type == type && other.taskDefinitionName.equals(taskDefinitionName);
	}

	@Override
	public String toString() {
		return "ExecutorModelEvent [type = " + type + ", taskDefinitionName = " + taskDefinitionName + "]";
	}
}
