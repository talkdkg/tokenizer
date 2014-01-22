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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.ui.v7.modules.DemoModule;
import org.tokenizer.ui.v7.modules.DemoUIModule;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.SystemAccountManagementPages;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {
    
    
    private static Logger log = LoggerFactory.getLogger(DemoGuiceServletInjector.class);

    Injector injector;
    
    static PersistService persistService;

    /**
    * @see uk.co.q3c.v7.base.guice.BaseGuiceServletInjector#addAppModules(java.util.List)
    */
    @Override
    protected void addAppModules(List<Module> baseModules)
    {

        // baseModules.add(new JpaPersistModule("default-persistence-unit"));
        // baseModules.add(new PersistenceServicesModule());

        baseModules.add(new DemoModule());
        //baseModules.add(new ZkModule(ini));
        //baseModules.add(new CrawlerRepositoryModule(ini));
        baseModules.add(new DemoUIModule());
        // baseModules.add(new DemoDAOModule());
        //baseModules.add(new ExecutorModelModule());
        //baseModules.add(new ExecutorMasterModule());
        //baseModules.add(new ExecutorWorkerModule());

    }
    
    
    @Override
    protected void addSitemapModules(List<Module> baseModules) {
        super.addSitemapModules(baseModules);
        baseModules.add(new SystemAccountManagementPages());
    }

    /* copied from DEMO application:
     
        @Override
    protected Module standardViewsModule() {
        return new DemoViewModule();
    }

    @Override
    protected void addSitemapModules(List<Module> baseModules) {
        super.addSitemapModules(baseModules);
        baseModules.add(new SystemAccountManagementPages());
    }

     */
    
    
/*
    @Override
    public Injector getInjector() {
        if (this.injector == null) {
            Injector injector = Guice.createInjector(new IniModule(), new I18NModule());
            injector = injector.createChildInjector(getModules(injector));
            SecurityManager securityManager = injector.getInstance(SecurityManager.class);
            SecurityUtils.setSecurityManager(securityManager);
            ((V7SecurityManager) securityManager).setSessionManager(new VaadinSessionManager());
            persistService = injector.getInstance(PersistService.class);
            persistService.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    persistService.stop();
                }
            });
            this.injector = injector;
        }
        return this.injector;
    }

    private List<Module> getModules(Injector injector) {

        V7Ini ini = injector.getInstance(V7Ini.class);
        List<Module> baseModules = new ArrayList<>();

        if (ini.optionReadSiteMap()) {
            log.debug("ini sitemap option is true, loading sitemap");
            Provider<Sitemap> sitemapPro = injector.getInstance(SitemapProvider.class);
            Sitemap sitemap = sitemapPro.get();
            baseModules.add(new ApplicationViewModule(sitemap));
        }
        else {
            // module for Views must be in addAppModules()
            log.debug("ini sitemap option is false, not loading sitemap");
        }

        baseModules.add(new JpaPersistModule("default-persistence-unit"));

        //baseModules.add(new PersistenceServicesModule());

        baseModules.add(new ThreadScopeModule());
        baseModules.add(new UIScopeModule());

        baseModules.add(new DefaultShiroModule());
        baseModules.add(new ShiroVaadinModule());
        baseModules.add(new ShiroAopModule());
        baseModules.add(new DefaultUserOptionModule(ini));

        baseModules.add(new BaseModule());

        baseModules.add(new StandardViewModule());
        baseModules.add(new DemoModule());
        baseModules.add(new ZkModule(ini));
        baseModules.add(new CrawlerRepositoryModule(ini));
        baseModules.add(new DemoUIModule());
        // baseModules.add(new DemoDAOModule());
        baseModules.add(new ExecutorModelModule());
        baseModules.add(new ExecutorMasterModule());
        baseModules.add(new ExecutorWorkerModule());

        return baseModules;
    }

    
*/
}