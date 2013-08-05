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
package org.tokenizer.guice.zk;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;

import com.google.inject.AbstractModule;

public class ZkModule extends AbstractModule {

    private final Ini ini;

    public ZkModule(Ini ini) {
        super();
        this.ini = ini;
    }

    @Override
    protected void configure() {
        Section zk = ini.getSection("zookeeper");
        bindConstant().annotatedWith(ConnectString.class).to(zk.get("connectString"));
        bindConstant().annotatedWith(SessionTimeout.class).to(zk.get("sessionTimeout"));
    }

}
