package org.tokenizer.crawler.db.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.nlp.NlpTools;

import uk.co.q3c.v7.base.config.V7Ini;
import uk.co.q3c.v7.base.config.V7Ini.NlpParam;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CrawlerRepositoryModule extends AbstractModule {

    protected static Logger LOG = LoggerFactory.getLogger(CrawlerRepositoryModule.class);

    private final V7Ini ini;
    private String nlpToolsImplementationClass;

    public CrawlerRepositoryModule(V7Ini ini) {
        super();
        this.ini = ini;
    }

    @Override
    protected void configure() {
        this.nlpToolsImplementationClass = ini.nlpParam(NlpParam.implementation);
    }

    @Provides
    NlpTools provideNlpTools() {
        try {
            Class<?> clazz = Class.forName(nlpToolsImplementationClass);
            if (NlpTools.class.isAssignableFrom(clazz)) {
                return (NlpTools) clazz.newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.error("", e);
        }

        LOG.error("{} does not implement interface {}", nlpToolsImplementationClass, NlpTools.class.getName());
        return null;

    }

}
