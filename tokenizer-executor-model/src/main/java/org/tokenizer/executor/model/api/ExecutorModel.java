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

import java.util.Collection;
import java.util.UUID;

public interface ExecutorModel {

    Collection<TaskInfoBean> getTasks();

    /**
     * Gets the list of task definitions, and registers a listener. It
     * guarantees that the listener will receive events for all updates that
     * happened after the returned snapshot of the tasks.
     * <p>
     * Note that the listener does not work like the watcher in ZooKeeper: listeners are not one-time only.
     */
    Collection<TaskInfoBean> getTasks(ExecutorModelListener listener);

    TaskInfoBean getTask(UUID uuid) throws TaskNotFoundException;

    boolean hasTask(UUID uuid);

    void registerListener(ExecutorModelListener listener);

    void unregisterListener(ExecutorModelListener listener);
}
