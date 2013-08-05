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
package org.xaloon.core.jpa;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author vytautas r.
 */
@Named("persistenceServices")
@Stateless(mappedName = "persistenceServices")
public class DefaultPersistenceServices extends AbstractPersistenceServices {
    private static final long serialVersionUID = 1L;

    @Inject
    private transient EntityManager em;

    /**
     * @param em
     *            instance used to interact with the persistence context.
     */
    @PersistenceContext(unitName = DEFAULT_UNIT_NAME)
    public void setEm(final EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }
}
