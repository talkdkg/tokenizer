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
package org.tokenizer.ui.demo.guice;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.tokenizer.crawler.db.module.CrawlerRepositoryModule;
import org.tokenizer.executor.master.Executor;
import org.tokenizer.executor.master.ExecutorMaster;
import org.tokenizer.executor.master.ExecutorMasterModule;
import org.tokenizer.executor.model.ExecutorModelModule;
import org.tokenizer.executor.worker.ExecutorWorkerModule;
import org.tokenizer.ui.base.guice.BaseGuiceServletInjector;
import org.tokenizer.ui.demo.dao.DemoDAOModule;
import org.tokenizer.ui.demo.ui.DemoUIModule;
import org.tokenizer.ui.demo.view.DemoModule;


import com.google.inject.Module;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {

	@Override
	protected void addAppModules(List<Module> baseModules) {
		baseModules.add(new DemoModule());
		baseModules.add(new DemoDAOModule());
		baseModules.add(new DemoUIModule());
		//baseModules.add(new ZkModule());
        baseModules.add(new ExecutorModelModule());
        baseModules.add(new ExecutorMasterModule());
        baseModules.add(new ExecutorWorkerModule());
        
        
	}
	
	   @Override
	    public void contextInitialized(ServletContextEvent servletContextEvent) {
	        super.contextInitialized(servletContextEvent);
	        //getInjector().getInstance(Executor.class);

	        //getInjector();
	        
	        
	   }

	
	
}
