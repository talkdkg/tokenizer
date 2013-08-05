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
package org.tokenizer.util.zookeeper;

import org.apache.zookeeper.KeeperException;

// Disclaimer: this interface was copied from ZooKeeper's lock recipe and slightly altered.
/**
 * A callback object which can be used for implementing retry-able operations,
 * used by {@link ZooKeeperItf#retryOperation}.
 * 
 */
public interface ZooKeeperOperation<T> {

    /**
     * Performs the operation - which may be involved multiple times if the
     * connection to ZooKeeper closes during this operation
     * 
     */
    public T execute() throws KeeperException, InterruptedException;
}
