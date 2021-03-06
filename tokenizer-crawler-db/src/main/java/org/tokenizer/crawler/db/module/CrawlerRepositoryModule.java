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
package org.tokenizer.crawler.db.module;

import org.apache.commons.configuration.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.nlp.NlpTools;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CrawlerRepositoryModule extends AbstractModule
{

    protected static Logger LOG = LoggerFactory.getLogger(CrawlerRepositoryModule.class);

    //private Ini ini;
    private String nlpToolsImplementationClass;

    private final CompositeConfiguration configuration;

    public CrawlerRepositoryModule(final CompositeConfiguration configuration)
    {

        super();
        this.configuration = configuration;
        this.nlpToolsImplementationClass = configuration.getString("nlp.implementation",
                "org222.tokenizer.nlp.impl.NlpToolsImpl");
        
        LOG.error("nlpToolsImplementationClass: {}", nlpToolsImplementationClass);
        
    }

    /**
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure()
    {
        // TODO Auto-generated method stub

    }

    //@Override
    //protected void configure()
    //{
    //Section zk = ini.getSection("nlp");
    //this.nlpToolsImplementationClass = configuration.getString("nlp.implementation");
    //}

    @Provides
    NlpTools provideNlpTools()
    {

        if (nlpToolsImplementationClass == null)
        {
            LOG.warn("null!!!");
            nlpToolsImplementationClass = configuration.getString("nlp.implementation");
        }
        else
        {
            LOG.warn("nlpToolsImplementationClass: {}", nlpToolsImplementationClass);
        }

        try
        {
            final Class<?> clazz = Class.forName(nlpToolsImplementationClass);
            if (NlpTools.class.isAssignableFrom(clazz))
            {
                return (NlpTools) clazz.newInstance();
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            LOG.error("", e);
        }

        LOG.error("{} does not implement interface {}", nlpToolsImplementationClass, NlpTools.class.getName());
        return null;

    }

}
