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
package org.xaloon.core.jpa.classifier;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.classifier.Classifier;
import org.xaloon.core.api.classifier.dao.ClassifierDao;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.jpa.classifier.model.JpaClassifier;

/**
 * @author vytautas r.
 */
@Named("classifierDao")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaClassifierDao implements ClassifierDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	@Named("persistenceServices")
	private PersistenceServices persistenceServices;

	@Override
	public Long getCount() {
		return persistenceServices.getCount(JpaClassifier.class);
	}

	@Override
	public <T extends Classifier> List<T> findClassifiers(long first, long count) {
		QueryBuilder queryBuilder = new QueryBuilder("select cl from " + JpaClassifier.class.getSimpleName() + " cl ");
		queryBuilder.setFirstRow(first);
		queryBuilder.setCount(count);
		queryBuilder.addOrderBy("cl.name asc");
		return persistenceServices.executeQuery(queryBuilder);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Classifier> T loadClassifierById(Long id) {
		return (T)persistenceServices.find(JpaClassifier.class, id);
	}

	@Override
	public <T extends Classifier> void createClassifier(T item) {
		persistenceServices.createOrEdit(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Classifier> T newClassifier() {
		return (T)new JpaClassifier();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Classifier> T findClassifierByType(String type) {
		QueryBuilder queryBuilder = new QueryBuilder("select cl from " + JpaClassifier.class.getSimpleName() + " cl ");
		queryBuilder.addParameter("cl.type", "CL_TYPE", type.toUpperCase());
		return (T)persistenceServices.executeQuerySingle(queryBuilder);
	}
}
