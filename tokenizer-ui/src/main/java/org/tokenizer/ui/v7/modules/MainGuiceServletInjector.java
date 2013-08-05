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
package org.tokenizer.ui.v7.modules;

import java.util.List;

import org.tokenizer.crawler.db.module.CrawlerRepositoryModule;
import org.tokenizer.executor.master.ExecutorMasterModule;
import org.tokenizer.executor.model.ExecutorModelModule;
import org.tokenizer.executor.worker.ExecutorWorkerModule;
import org.tokenizer.guice.zk.ZkModule;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;

import com.google.inject.Module;
//import org.tokenizer.ui.demo.dao.DemoDAOModule;

public class MainGuiceServletInjector extends BaseGuiceServletInjector {

    @Override
    protected void addAppModules(List<Module> modules, V7Ini ini) {

 
        modules.add(new DemoModule());
        modules.add(new ZkModule(ini));
        modules.add(new CrawlerRepositoryModule(ini));

        modules.add(new DemoUIModule());
        // baseModules.add(new DemoDAOModule());
        modules.add(new ExecutorModelModule());
        modules.add(new ExecutorMasterModule());
        modules.add(new ExecutorWorkerModule());
    }

}
