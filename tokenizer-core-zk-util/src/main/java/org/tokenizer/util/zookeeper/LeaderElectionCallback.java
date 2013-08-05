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

/**
 * Used by {@link LeaderElection} to notify when to become leader and when to
 * step down as leader.
 * 
 * <p>
 * The callback methods are not called from within a ZooKeeper Watcher callback,
 * so you do not have to worry that they might take some time or that they
 * should not perform ZooKeeper operations by themselves.
 * 
 * <p>
 * The {@link #activateAsLeader()} and {@link #deactivateAsLeader()} will never
 * be called concurrently.
 * 
 * <p>
 * This callback is not called for every state change. If the state would switch
 * multiple times between leader and not-leader during the processing of this
 * callback, there will be only one call to this callback to bring it to the
 * current state.
 */
public interface LeaderElectionCallback {

    void activateAsLeader() throws Exception;

    void deactivateAsLeader() throws Exception;
}
