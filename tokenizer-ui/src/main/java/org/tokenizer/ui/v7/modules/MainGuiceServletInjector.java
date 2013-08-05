/*
 * Copyright 2013 Tokenizer Inc.
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
