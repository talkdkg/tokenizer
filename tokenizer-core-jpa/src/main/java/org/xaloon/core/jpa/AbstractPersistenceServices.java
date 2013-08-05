/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xaloon.core.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.xaloon.core.api.audit.annotation.Audited;
import org.xaloon.core.api.audit.model.AuditState;
import org.xaloon.core.api.persistence.Persistable;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;

/**
 * @author vytautas r.
 */
public abstract class AbstractPersistenceServices implements PersistenceServices {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return instance used to interact with the persistence context.
	 */
	public abstract EntityManager getEm();

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#remove(java.lang.Object)
	 */
	@Override
    @Audited(state = AuditState.DELETE)
	public <T> void remove(final T o) {
		getEm().remove(getEm().merge(o));
		getEm().flush();
	}

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#remove(java.lang.Class, java.lang.Long)
	 */
	@Override
    @Audited(state = AuditState.DELETE)
	public <T> void remove(final Class<T> clazz, final Long id) {
		getEm().remove(getEm().getReference(clazz, id));
		getEm().flush();
	}

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#find(Class, Object)
	 */
	@Override
    public <T> T find(final Class<T> clazz, final Object id) {
		return getEm().find(clazz, id);
	}

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#createOrEdit(org.xaloon.core.jpa.model.AbstractEntity)
	 */
	@Override
    @Audited(state = AuditState.CREATE_OR_UPDATE)
	public <T extends Persistable> T createOrEdit(final T entity) {
		if (entity.isNew()) {
			return create(entity);
		} else {
			return edit(entity);
		}
	}

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#executeQuery(org.xaloon.core.api.persistence.QueryBuilder)
	 */
	@Override
    @SuppressWarnings("unchecked")
	public <T> List<T> executeQuery(final QueryBuilder query) {
		Query queryToExecute = prepareJpaQuery(query);

		List<T> result = queryToExecute.getResultList();
		result.size();
		return result;
	}

	private Query prepareJpaQuery(final QueryBuilder query) {
		Query queryToExecute = getEm().createQuery(query.getQuery());
		if (query.getFirstRow() > 0) {
			queryToExecute.setFirstResult((int)query.getFirstRow());
		}
		if (query.getCount() > 0) {
			queryToExecute.setMaxResults((int)query.getCount());
		}
		if (query.getParameters() != null) {
			for (Map.Entry<String, Object> param : query.getParameters().entrySet()) {
				queryToExecute.setParameter(param.getKey(), param.getValue());
			}
		}
		queryToExecute.setHint("org.hibernate.cacheable", query.isCacheable());
		return queryToExecute;
	}

	/**
	 * 
	 * @see org.xaloon.core.api.persistence.PersistenceServices#executeQuerySingle(org.xaloon.core.api.persistence.QueryBuilder)
	 */
	@Override
    @SuppressWarnings("unchecked")
	public <T> T executeQuerySingle(final QueryBuilder query) {
		Query queryToExecute = getEm().createQuery(query.getQuery());
		if (query.getParameters() != null) {
			for (Map.Entry<String, Object> param : query.getParameters().entrySet()) {
				queryToExecute.setParameter(param.getKey(), param.getValue());
			}
		}
		try {
			queryToExecute.setHint("org.hibernate.cacheable", query.isCacheable());
			return (T)queryToExecute.getSingleResult();
		} catch (NoResultException e) {
		}
		return null;
	}

	@Override
	public <T> Long getCount(final Class<T> classForCount) {
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(classForCount)));
		return getEm().createQuery(cq).getSingleResult();
	}

	@Override
	@Audited(state = AuditState.UPDATE)
	public <T> T edit(final T entity) {
		return getEm().merge(entity);
	}

	@Override
	@Audited(state = AuditState.CREATE)
	public <T> T create(final T entity) {
		getEm().persist(entity);
		return entity;
	}

	@Override
	public int executeUpdate(final QueryBuilder queryBuilder) {
		Query queryToExecute = prepareJpaQuery(queryBuilder);
		return queryToExecute.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> executeNativeQuery(final String query) {
		return getEm().createNativeQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T executeNativeQuerySingle(final String query) {
		return (T)getEm().createNativeQuery(query).getSingleResult();
	}

	@Override
	public int executeNativeUpdate(final String query) {
		return getEm().createNativeQuery(query).executeUpdate();
	}

	@Override
	public void flush() {
		getEm().flush();
	}
}
