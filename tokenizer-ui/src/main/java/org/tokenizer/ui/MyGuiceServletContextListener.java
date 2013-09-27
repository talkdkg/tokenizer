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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.jpa.PersistenceServicesModule;
import org.tokenizer.crawler.db.module.CrawlerRepositoryModule;
import org.tokenizer.executor.master.ExecutorMasterModule;
import org.tokenizer.executor.model.ExecutorModelModule;
import org.tokenizer.executor.worker.ExecutorWorkerModule;
import org.tokenizer.guice.zk.ZkModule;
import org.tokenizer.ui.v7.modules.DemoModule;
import org.tokenizer.ui.v7.modules.DemoUIModule;
import org.xaloon.core.jpa.user.JpaUserDao;

import uk.co.q3c.v7.base.config.IniModule;
import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.guice.BaseModule;
import uk.co.q3c.v7.base.guice.threadscope.ThreadScopeModule;
import uk.co.q3c.v7.base.guice.uiscope.UIScopeModule;
import uk.co.q3c.v7.base.navigate.Sitemap;
import uk.co.q3c.v7.base.navigate.SitemapProvider;
import uk.co.q3c.v7.base.shiro.DefaultShiroModule;
import uk.co.q3c.v7.base.shiro.ShiroVaadinModule;
import uk.co.q3c.v7.base.shiro.V7SecurityManager;
import uk.co.q3c.v7.base.shiro.VaadinSessionManager;
import uk.co.q3c.v7.base.useropt.DefaultUserOptionModule;
import uk.co.q3c.v7.base.view.ApplicationViewModule;
import uk.co.q3c.v7.base.view.StandardViewModule;
import uk.co.q3c.v7.i18n.I18NModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;

public class MyGuiceServletContextListener extends GuiceServletContextListener {
    private static Logger log = LoggerFactory.getLogger(MyGuiceServletContextListener.class);

    private ThreadLocal<ServletContext> ctx;

    protected ThreadLocal<ServletContext> createThreadLocalServletContext() {
        return new ThreadLocal<ServletContext>();
    }

    Injector injector;
    
    static PersistService persistService;
    

    /**
     * Module instances for the base should be added in {@link #getModules()}. Module instance for the app using V7
     * should be added to {@link AppModules#appModules()}
     * 
     * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
     */
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

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ctx = createThreadLocalServletContext();
        final ServletContext servletContext = servletContextEvent.getServletContext();
        ctx.set(servletContext);
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);
        ctx.remove();
    }
    

}