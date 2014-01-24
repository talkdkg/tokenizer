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
package org.tokenizer.ui;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.module.CrawlerRepositoryModule;
import org.tokenizer.executor.master.ExecutorMasterModule;
import org.tokenizer.executor.model.ExecutorModelModule;
import org.tokenizer.executor.worker.ExecutorWorkerModule;
import org.tokenizer.guice.zk.ZkModule;
import org.tokenizer.ui.v7.modules.AdminPages;
import org.tokenizer.ui.v7.modules.DemoModule;
import org.tokenizer.ui.v7.modules.DemoUIModule;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.SystemAccountManagementPages;
import uk.co.q3c.v7.demo.view.DemoViewModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector
{

    private static Logger log = LoggerFactory.getLogger(DemoGuiceServletInjector.class);

    Injector injector;

    static PersistService persistService;

    private final CompositeConfiguration configuration = new CompositeConfiguration();
    
    /**
     * @see uk.co.q3c.v7.base.guice.BaseGuiceServletInjector#addAppModules(java.util.List)
     */
    @Override
    protected void addAppModules(List<Module> baseModules)
    {
        
        
        File file = new File("/usr/java/tokenizer-home", "tokenizer.ini");

        
        if (file.exists()) {
            HierarchicalINIConfiguration config;
            try
            {
                config = new HierarchicalINIConfiguration(file);
            }
            catch (ConfigurationException e)
            {
                throw new RuntimeException(e);
            }
            configuration.addConfiguration(config);
        } else {
            throw new RuntimeException("Configuration file " + file.getAbsolutePath() + " does not exist");
        }

        
        // baseModules.add(new TestFileSitemapModule());
        // baseModules.add(new JpaPersistModule("default-persistence-unit"));
        // baseModules.add(new PersistenceServicesModule());
        baseModules.add(new ZkModule(configuration));
        baseModules.add(new CrawlerRepositoryModule(configuration));
        // baseModules.add(new DemoDAOModule());
        baseModules.add(new ExecutorModelModule());
        baseModules.add(new ExecutorMasterModule());
        baseModules.add(new ExecutorWorkerModule());

        // baseModules.add(new StandardViewModule());
        // baseModules.add(new SystemAccountManagementPages());

        baseModules.add(new DemoModule());
        baseModules.add(new DemoUIModule());
    }

    @Override
    protected Module standardViewsModule()
    {
        return new DemoViewModule();
    }

    @Override
    protected void addSitemapModules(List<Module> baseModules)
    {
        super.addSitemapModules(baseModules);
        baseModules.add(new SystemAccountManagementPages());
        baseModules.add(new AdminPages());
    }

}