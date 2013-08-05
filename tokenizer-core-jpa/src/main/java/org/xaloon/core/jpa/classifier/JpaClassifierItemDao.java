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

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.classifier.ClassifierItem;
import org.xaloon.core.api.classifier.dao.ClassifierDao;
import org.xaloon.core.api.classifier.dao.ClassifierItemDao;
import org.xaloon.core.api.classifier.search.ClassifierItemSearchRequest;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.jpa.classifier.model.JpaClassifier;
import org.xaloon.core.jpa.classifier.model.JpaClassifierItem;

/**
 * @author vytautas r.
 */
@Named("classifierItemDao")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaClassifierItemDao implements ClassifierItemDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	@Named("persistenceServices")
	private PersistenceServices persistenceServices;

	@Inject
	private ClassifierDao classifierDao;

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ClassifierItem> T newClassifierItem(String classifierType, String parentClassifierItemCode) {
		JpaClassifier classifier = classifierDao.findClassifierByType(classifierType);
		JpaClassifierItem parentClassifierItem = null;
		if (!StringUtils.isEmpty(parentClassifierItemCode)) {
			ClassifierItemSearchRequest classifierItemSearchRequest = new ClassifierItemSearchRequest();
			classifierItemSearchRequest.setClassifierType(classifierType);
			classifierItemSearchRequest.setClassifierItemCode(parentClassifierItemCode);
			parentClassifierItem = getClassifierItem(classifierItemSearchRequest);
		}

		JpaClassifierItem item = new JpaClassifierItem();
		item.setClassifier(classifier);
		item.setParent(parentClassifierItem);
		return (T)item;
	}

	@Override
	public <T extends ClassifierItem> T createClassifierItem(T item) {
		return persistenceServices.createOrEdit(item);
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T extends ClassifierItem> T loadClassifierItemById(Long id) {
		return (T)persistenceServices.find(JpaClassifierItem.class, id);
	}

	@Override
	public Long count(ClassifierItemSearchRequest classifierItemSearchRequest) {
		QueryBuilder queryBuilder = createQueryBuilder("select count(cli) ", classifierItemSearchRequest);

		return persistenceServices.executeQuerySingle(queryBuilder);
	}

	@Override
	public <T extends ClassifierItem> List<T> find(ClassifierItemSearchRequest classifierItemSearchRequest) {
		QueryBuilder queryBuilder = createQueryBuilder("select cli ", classifierItemSearchRequest);
		if (StringUtils.isEmpty(classifierItemSearchRequest.getOrderBy())) {
			queryBuilder.addOrderBy("cli.name asc");
		} else {
			queryBuilder.addOrderBy(classifierItemSearchRequest.getOrderBy());
		}
		return persistenceServices.executeQuery(queryBuilder);
	}

	private QueryBuilder createQueryBuilder(String selectString, ClassifierItemSearchRequest classifierItemSearchRequest) {
		QueryBuilder queryBuilder = new QueryBuilder(selectString + " from " + JpaClassifierItem.class.getSimpleName() + " cli ");
		if (!StringUtils.isEmpty(classifierItemSearchRequest.getClassifierType())) {
			queryBuilder.addJoin(QueryBuilder.INNER_JOIN, "cli.classifier cl ");
			queryBuilder.addParameter("cl.type", "CL_TYPE", classifierItemSearchRequest.getClassifierType());
		}

		if (!StringUtils.isEmpty(classifierItemSearchRequest.getParentClassifierItemCode())) {
			queryBuilder.addJoin(QueryBuilder.INNER_JOIN, "cli.parent p");
			queryBuilder.addParameter("p.code", "PARENT_CODE", classifierItemSearchRequest.getParentClassifierItemCode());
		}

		if (!StringUtils.isEmpty(classifierItemSearchRequest.getClassifierItemCode())) {
			queryBuilder.addParameter("cli.code", "ITEM_CODE", classifierItemSearchRequest.getClassifierItemCode().toUpperCase());
		}

		if (!StringUtils.isEmpty(classifierItemSearchRequest.getClassifierItemName())) {
			queryBuilder.addParameter("cli.name", "ITEM_NAME", classifierItemSearchRequest.getClassifierItemName().toUpperCase(), false, true);
		}

		if (classifierItemSearchRequest.isParentSelection() && !classifierItemSearchRequest.isIgnoreParentCode()) {
			queryBuilder.addExpression("cli.parent is null");
		}
		queryBuilder.setFirstRow(classifierItemSearchRequest.getFirstRow());
		queryBuilder.setCount(classifierItemSearchRequest.getMaxRowCount());
		return queryBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ClassifierItem> T getClassifierItem(ClassifierItemSearchRequest classifierItemSearchRequest) {
		QueryBuilder queryBuilder = createQueryBuilder("select cli ", classifierItemSearchRequest);

		return (T)persistenceServices.executeQuerySingle(queryBuilder);
	}

	@Override
	public <T extends ClassifierItem> List<T> findLowerLevelItems(String classifierType) {
		QueryBuilder queryBuilder = new QueryBuilder("select cli from " + JpaClassifierItem.class.getSimpleName() +
			" cli inner join cli.classifier cl ");
		queryBuilder.addParameter("cl.type", "_TYPE", classifierType);
		queryBuilder.addExpression(" cli.parent != null ");
		return persistenceServices.executeQuery(queryBuilder);
	}
}
