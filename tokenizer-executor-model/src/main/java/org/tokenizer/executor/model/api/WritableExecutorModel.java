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
import org.lilyproject.util.zookeeper.ZkLockException;

public interface WritableExecutorModel extends ExecutorModel {
  
  /**
   * Instantiates an TaskDefinition object, but does not register it yet, you
   * should do this using {@link #addTaskDefinition}.
   */
  TaskDefinition newTaskDefinition(String name);
  
  void addTaskDefinition(TaskDefinition taskDefinition)
      throws TaskExistsException, TaskModelException, TaskValidityException;
  
  /**
   * Loads a task definition and returns it in a mutable way.
   * 
   * <p>
   * This differs from {@link #getTaskDefinition(String)} in that the returned
   * TaskDefinition is mutable (updateable) and it is also freshly loaded from
   * storage.
   */
  TaskDefinition getMutableTaskDefinition(String name)
      throws InterruptedException, KeeperException, TaskNotFoundException;
  
  /**
   * Updates a TaskDefinition.
   * 
   * <p>
   * The update will only succeed if it was not modified since it was read. This
   * situation can be avoided by taking a lock on the TaskDefinition before
   * reading it. In fact, you are obliged to do so, and to pass your lock, of
   * which it will be validated that it really is the owner of the
   * TaskDefinition lock.
   */
  void updateTaskDefinition(final TaskDefinition taskDefinition, String lock)
      throws InterruptedException, KeeperException, TaskNotFoundException,
      TaskConcurrentModificationException, ZkLockException,
      TaskUpdateException, TaskValidityException;
  
  /**
   * Internal TaskDefinition update method, <b>this method is only intended for
   * internal Executor components</b>. It is similar to the update method but
   * bypasses some checks.
   */
  void updateTaskDefinitionInternal(final TaskDefinition taskDefinition)
      throws InterruptedException, KeeperException, TaskNotFoundException,
      TaskConcurrentModificationException, TaskValidityException;
  
  void deleteTaskDefinition(final String taskDefinitionName)
      throws TaskModelException;
  
  /**
   * Takes a lock on this TaskDefinition.
   * 
   * <p>
   * Taking a lock can avoid concurrent modification exceptions when updating
   * the TaskDefinition.
   */
  String lockTaskDefinition(String taskDefinitionName) throws ZkLockException,
      TaskNotFoundException, InterruptedException, KeeperException,
      TaskModelException;
  
  void unlockTaskDefinition(String lock) throws ZkLockException;
  
  void unlockTaskDefinition(String lock, boolean ignoreMissing)
      throws ZkLockException;
  
  /**
   * Internal TaskDefinition lock method, <b>this method is only intended for
   * internal Executor components</b>.
   */
  String lockTaskDefinitionInternal(String taskDefinitionName,
      boolean checkDeleted) throws ZkLockException, TaskNotFoundException,
      InterruptedException, KeeperException, TaskModelException;
}
