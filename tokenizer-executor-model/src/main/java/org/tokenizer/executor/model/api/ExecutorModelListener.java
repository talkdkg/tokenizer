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

/**
 * Reports changes to the {@link ExecutorModel}.
 * 
 * <p>
 * The methods are called from within a ZooKeeper Watcher event callback, so be careful what you do in the
 * implementation (should be short-running + not wait for ZK events itself).
 */
public interface ExecutorModelListener {
	void process(ExecutorModelEvent event);
}
