/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
package org.tokenizer.crawler.db.module;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.Ini.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.nlp.NlpTools;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CrawlerRepositoryModule extends AbstractModule {

    protected static Logger LOG = LoggerFactory.getLogger(CrawlerRepositoryModule.class);

    private final Ini ini;
    private String nlpToolsImplementationClass;

    public CrawlerRepositoryModule(Ini ini) {
        super();
        this.ini = ini;
    }

    @Override
    protected void configure() {
        Section zk = ini.getSection("nlp");
        this.nlpToolsImplementationClass = zk.get("implementation");
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
