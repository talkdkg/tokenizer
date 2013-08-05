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
package org.tokenizer.executor.model.api;

import java.util.UUID;

import org.apache.zookeeper.KeeperException;
import org.tokenizer.util.zookeeper.ZkLockException;

public interface WritableExecutorModel extends ExecutorModel {

    void addTask(TaskInfoBean task) throws TaskExistsException,
            TaskModelException, TaskValidityException;

    /**
     * Loads a task and returns it in a mutable way.
     * 
     * <p>
     * This differs from {@link #getTask(UUID)} in that the returned
     * TaskInfoBean is mutable (updateable) and it is also freshly loaded from
     * storage.
     */
    TaskInfoBean getMutableTask(UUID uuid) throws InterruptedException,
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

    void deleteTask(final UUID uuid) throws TaskModelException;

    /**
     * Takes a lock on this Task.
     * 
     * <p>
     * Taking a lock can avoid concurrent modification exceptions when updating
     * the Task.
     */
    String lockTask(UUID uuid) throws ZkLockException, TaskNotFoundException,
            InterruptedException, KeeperException, TaskModelException;

    void unlockTask(String lock) throws ZkLockException;

    void unlockTask(String lock, boolean ignoreMissing) throws ZkLockException;

    /**
     * Internal lock method; <b>this method is only intended for internal
     * Executor components</b>.
     */
    String lockTaskInternal(UUID uuid, boolean checkDeleted)
            throws ZkLockException, TaskNotFoundException,
            InterruptedException, KeeperException, TaskModelException;
}
