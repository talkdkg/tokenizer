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
package org.tokenizer.executor.model;

import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.impl.ExecutorModelImpl;
import org.tokenizer.util.zookeeper.ZooKeeperImpl;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.google.inject.AbstractModule;

public class ExecutorModelModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WritableExecutorModel.class).to(ExecutorModelImpl.class);
        bind(ZooKeeperItf.class).to(ZooKeeperImpl.class);
    }

}
