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

public enum TaskGeneralState {
	/**
	 * Indicates active task configuration
	 */
	ACTIVE,

	/**
	 * Indicates disabled task configuration
	 */
	DISABLED,

	/**
	 * Indicates a request towards the master to drop task definition.
	 */
	DELETE_REQUESTED,

	/**
	 * Indicates the delete request is being processed.
	 */
	DELETING,

	/**
	 * Indicates a delete request failed, set again to {@link #DELETE_REQUESTED} to retry.
	 */
	DELETE_FAILED,

	STOP_REQUESTED, STOPPING, STOP_FAILED, START_REQUESTED, STARTING, START_FAILED;

	public boolean isDeleteState() {
		return this == TaskGeneralState.DELETE_REQUESTED || this == TaskGeneralState.DELETING || this == TaskGeneralState.DELETE_FAILED;
	}

	public boolean isStopState() {
		return this == TaskGeneralState.STOP_REQUESTED || this == TaskGeneralState.STOPPING || this == TaskGeneralState.STOP_FAILED;
	}

	public boolean isStartState() {
		return this == TaskGeneralState.START_REQUESTED || this == TaskGeneralState.STARTING || this == TaskGeneralState.START_FAILED;
	}

}
