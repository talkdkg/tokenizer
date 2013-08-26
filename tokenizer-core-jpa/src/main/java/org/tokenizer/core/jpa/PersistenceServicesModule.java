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
package org.tokenizer.core.jpa;

import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.jpa.DefaultPersistenceServices;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PersistenceServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistenceServices.class).annotatedWith(Names.named("persistenceServices")).to(
                DefaultPersistenceServices.class);
    }
}
