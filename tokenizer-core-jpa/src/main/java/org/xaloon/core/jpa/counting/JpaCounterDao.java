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
package org.xaloon.core.jpa.counting;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.counting.CounterDao;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.jpa.counting.model.JpaCounterEntity;
import org.xaloon.core.jpa.counting.model.JpaCounterId;

/**
 * @author vytautas r.
 */
@Named("counterDao")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaCounterDao implements CounterDao {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaCounterDao.class);

    @Inject
    @Named("persistenceServices")
    private PersistenceServices persistenceServices;

    @Override
    public boolean increment(String counterGroup, Long categoryId, Long entityId) {
        JpaCounterId id = createPrimaryKey(counterGroup, categoryId, entityId);
        if (incrementValue(id) == false) {
            return createNewSequence(id, 1L) != null;
        }
        return true;
    }

    private JpaCounterEntity createNewSequence(JpaCounterId id, long l) {
        JpaCounterEntity newEntity = new JpaCounterEntity();
        newEntity.setCounterId(id);
        newEntity.setCount(1L);
        return persistenceServices.create(newEntity);
    }

    private boolean incrementValue(JpaCounterId id) {
        QueryBuilder queryBuilder = new QueryBuilder("UPDATE JpaCounterEntity as s set s.count = s.count + 1");
        queryBuilder.addParameter("s.counterId", "_ID", id);
        int rows = persistenceServices.executeUpdate(queryBuilder);
        return (rows == 1);
    }

    private JpaCounterEntity find(JpaCounterId primaryKey) {
        return persistenceServices.find(JpaCounterEntity.class, primaryKey);
    }

    private JpaCounterId createPrimaryKey(String counterGroup, Long categoryId, Long entityId) {
        JpaCounterId id = new JpaCounterId();
        id.setCounterGroup(counterGroup);
        id.setCategoryId(categoryId);
        id.setEntityId(entityId);
        return id;
    }

    @Override
    public Long count(String counterGroup, Long categoryId, Long entityId) {
        JpaCounterEntity counterEntity = find(createPrimaryKey(counterGroup, categoryId, entityId));
        return (counterEntity != null) ? counterEntity.getCount() : 0L;
    }

    @Override
    public boolean decrement(String counterGroup, Long categoryId, Long entityId) {
        JpaCounterId id = createPrimaryKey(counterGroup, categoryId, entityId);

        QueryBuilder queryBuilder = new QueryBuilder("UPDATE JpaCounterEntity as s set s.count = s.count - 1");
        queryBuilder.addParameter("s.counterId", "_ID", id);
        int rows = persistenceServices.executeUpdate(queryBuilder);
        return (rows == 1);
    }
}
