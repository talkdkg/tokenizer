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
package org.tokenizer.executor.master;

import org.tokenizer.util.Version;

public class ExecutorInfoImpl implements ExecutorInfo {

    private boolean master;

    @Override
    public String getVersion() {
        return Version.readVersion("org.tokenizer", "tokenizer-executor-master");
    }

    @Override
    public boolean isMaster() {
        return master;
    }

    @Override
    public void setMaster(boolean master) {
        this.master = master;
    }

}
