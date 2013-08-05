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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.exception.CreateClassInstanceException;
import org.xaloon.core.api.keyvalue.KeyValue;
import org.xaloon.core.api.keyvalue.KeyValueDao;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.api.util.ClassUtil;
import org.xaloon.core.api.util.UrlUtil;
import org.xaloon.core.jpa.model.JpaKeyValue;

/**
 * @author vytautas r.
 * @param <R>
 * @param <T>
 */
public abstract class AbstractKeyValueDao<R extends KeyValue<String, String>> implements KeyValueDao<String, String, R> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Inject
    @Named("persistenceServices")
    private PersistenceServices persistenceServices;

    protected abstract Class<R> getImplementationClass();

    @Override
    public R newKeyValue(String key, String parsedValue) throws CreateClassInstanceException {
        R result = ClassUtil.newInstance(getImplementationClass());
        result.setKey(key);
        result.setValue(parsedValue);
        result.setPath(UrlUtil.encode(parsedValue));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R findInStorage(String key, String parsedValue) {
        QueryBuilder query = new QueryBuilder("select kv from " + getImplementationClass().getSimpleName() + " kv");
        query.addParameter("kv.key", "KEY", key);
        query.addParameter("kv.value", "VALUE", parsedValue, false, true);
        return (R) persistenceServices.executeQuerySingle(query);
    }

    @Override
    public List<R> findRandomValues(String key, int randomLinkCountToSelect) {
        List<R> allKeyValues = findInStorage(key);
        if (!allKeyValues.isEmpty()) {
            Collections.shuffle(allKeyValues);
            if (randomLinkCountToSelect < allKeyValues.size()) {
                return allKeyValues.subList(0, randomLinkCountToSelect);
            }
        }
        return allKeyValues;
    }

    private List<R> findInStorage(String key) {
        QueryBuilder query = new QueryBuilder("select kv from " + JpaKeyValue.class.getSimpleName() + " kv");
        query.addParameter("kv.key", "KEY", key);
        return persistenceServices.executeQuery(query);
    }
}
