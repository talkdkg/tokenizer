package uk.co.q3c.v7.demo;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.tokenizer.crawler.db.module.CrawlerRepositoryModule;
import org.tokenizer.executor.master.ExecutorMasterModule;
import org.tokenizer.executor.model.ExecutorModelModule;
import org.tokenizer.executor.worker.ExecutorWorkerModule;
import org.tokenizer.guice.zk.ZkModule;
import org.tokenizer.ui.v7.modules.AdminPages;

import uk.co.q3c.v7.base.guice.BaseGuiceServletInjector;
import uk.co.q3c.v7.base.navigate.sitemap.SystemAccountManagementPages;

import com.google.inject.Module;

public class DemoGuiceServletInjector extends BaseGuiceServletInjector {
    
    private final CompositeConfiguration configuration = new CompositeConfiguration();

    @Override
    protected void addAppModules(List<Module> modules) {
        modules.add(new DemoModule());
        modules.add(new DemoUIModule());
        
        File file = new File("/java/git/tokenizer/tokenizer-ui", "V7.ini");
        
        if (file.exists()) {
            HierarchicalINIConfiguration config;
            try
            {
                config = new HierarchicalINIConfiguration(file);
            } catch (ConfigurationException e)
            {
                throw new RuntimeException(e);
            }
            configuration.addConfiguration(config);
        } else {
            throw new RuntimeException("Configuration file " + file.getAbsolutePath() + " does not exist");
        }
        
        modules.add(new ZkModule(configuration));
        modules.add(new CrawlerRepositoryModule(configuration));
        modules.add(new ExecutorModelModule());
        modules.add(new ExecutorMasterModule());
        modules.add(new ExecutorWorkerModule());
        
        // baseModules.add(new DemoDAOModule());
        // baseModules.add(new StandardViewModule());
        // baseModules.add(new SystemAccountManagementPages());
        // baseModules.add(new JpaPersistModule("default-persistence-unit"));
        // baseModules.add(new PersistenceServicesModule());
        
    }
    
    // @Override
    // protected Module standardViewsModule() {
    // return new DemoViewModule();
    // }
    
    @Override
    protected void addSitemapModules(List<Module> baseModules) {
        super.addSitemapModules(baseModules);
        baseModules.add(new SystemAccountManagementPages());
        baseModules.add(new AdminPages());
        //baseModules.add(new StandardViewModule());
    }
    
}
