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

import org.apache.zookeeper.KeeperException;
import org.tokenizer.util.zookeeper.ZkLockException;

public interface WritableExecutorModel extends ExecutorModel {

    void addTask(TaskInfoBean task) throws TaskExistsException,
            TaskModelException, TaskValidityException;

    /**
     * Loads a task and returns it in a mutable way.
     * 
     * <p>
     * This differs from {@link #getTask(String)} in that the returned
     * TaskInfoBean is mutable (updateable) and it is also freshly loaded from
     * storage.
     */
    TaskInfoBean getMutableTask(String name) throws InterruptedException,
            KeeperException, TaskNotFoundException;

    /**
     * Updates a Task.
     * 
     * <p>
     * The update will only succeed if it was not modified since it was read.
     * This situation can be avoided by taking a lock on the Task before reading
     * it. In fact, you are obliged to do so, and to pass your lock, of which it
     * will be validated that it really is the owner of the Task lock.
     */
    void updateTask(final TaskInfoBean task, String lock)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            ZkLockException, TaskUpdateException, TaskValidityException;

    /**
     * Internal Task update method, <b>this method is only intended for internal
     * Executor components</b>. It is similar to the update method but bypasses
     * some checks.
     */
    void updateTaskInternal(final TaskInfoBean task)
            throws InterruptedException, KeeperException,
            TaskNotFoundException, TaskConcurrentModificationException,
            TaskValidityException;

    void deleteTask(final String taskName) throws TaskModelException;

    /**
     * Takes a lock on this Task.
     * 
     * <p>
     * Taking a lock can avoid concurrent modification exceptions when updating
     * the Task.
     */
    String lockTask(String taskName) throws ZkLockException,
            TaskNotFoundException, InterruptedException, KeeperException,
            TaskModelException;

    void unlockTask(String lock) throws ZkLockException;

    void unlockTask(String lock, boolean ignoreMissing) throws ZkLockException;

    /**
     * Internal lock method; <b>this method is only intended for internal
     * Executor components</b>.
     */
    String lockTaskInternal(String taskName, boolean checkDeleted)
            throws ZkLockException, TaskNotFoundException,
            InterruptedException, KeeperException, TaskModelException;
}
