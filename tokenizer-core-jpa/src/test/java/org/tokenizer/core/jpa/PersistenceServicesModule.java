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
