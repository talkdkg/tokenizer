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

import java.util.Collection;
import java.util.UUID;

public interface ExecutorModel {

    Collection<TaskInfoBean> getTasks();

    /**
     * Gets the list of task definitions, and registers a listener. It
     * guarantees that the listener will receive events for all updates that
     * happened after the returned snapshot of the tasks.
     * <p>
     * Note that the listener does not work like the watcher in ZooKeeper:
     * listeners are not one-time only.
     */
    Collection<TaskInfoBean> getTasks(ExecutorModelListener listener);

    TaskInfoBean getTask(UUID uuid) throws TaskNotFoundException;

    boolean hasTask(UUID uuid);

    void registerListener(ExecutorModelListener listener);

    void unregisterListener(ExecutorModelListener listener);
}
