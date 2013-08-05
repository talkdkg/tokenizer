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
package org.xaloon.core.jpa.plugin.comment;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.api.plugin.comment.Comment;
import org.xaloon.core.api.plugin.comment.CommentDao;
import org.xaloon.core.api.plugin.comment.Commentable;
import org.xaloon.core.api.user.model.User;
import org.xaloon.core.jpa.plugin.comment.model.JpaComment;
import org.xaloon.core.jpa.user.model.JpaAnonymousUser;

/**
 * @author vytautas r.
 */
@Named
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaCommentDao implements CommentDao {
	private static final long serialVersionUID = 1L;

	@Inject
	@Named("persistenceServices")
	private PersistenceServices persistenceServices;

	@Override
	public void save(Comment comment) {
		boolean merge = comment.getFromUser() != null && comment.getFromUser().getId() != null;
		if (merge) {
			persistenceServices.edit(comment);
		} else {
			if (comment.getFromUser() instanceof JpaAnonymousUser) {
				persistenceServices.create(comment.getFromUser());
			}
			persistenceServices.create(comment);
		}
	}

	@Override
	public List<Comment> getComments(Commentable commentable, long first, long count) {
		QueryBuilder queryBuilder = new QueryBuilder("select c from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.categoryId", "COMPONENT_ID", commentable.getTrackingCategoryId());
		queryBuilder.addParameter("c.entityId", "ID", commentable.getId());
		queryBuilder.addParameter("c.enabled", "_ENABLED", true);
		queryBuilder.addParameter("c.inappropriate", "_inappropriate", false);
		queryBuilder.setFirstRow(first);
		queryBuilder.setCount(count);
		queryBuilder.addOrderBy("c.createDate desc");
		return persistenceServices.executeQuery(queryBuilder);
	}

	@Override
	public void deleteAll() {
		QueryBuilder queryBuilder = new QueryBuilder("delete from c " + JpaComment.class.getSimpleName() + " c");
		persistenceServices.executeUpdate(queryBuilder);
	}

	@Override
	public Long count(Commentable commentable) {
		QueryBuilder queryBuilder = new QueryBuilder("select count(c) from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.categoryId", "COMPONENT_ID", commentable.getTrackingCategoryId());
		queryBuilder.addParameter("c.entityId", "ID", commentable.getId());
		queryBuilder.addParameter("c.enabled", "_ENABLED", true);
		queryBuilder.addParameter("c.inappropriate", "_inappropriate", false);
		return persistenceServices.executeQuerySingle(queryBuilder);
	}

	@Override
	public Comment load(Long id) {
		return persistenceServices.find(JpaComment.class, id);
	}

	@Override
	public List<Comment> getWaitingCommentsForApproval() {
		QueryBuilder queryBuilder = new QueryBuilder("select c from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.enabled", "_ENABLED", false);
		return persistenceServices.executeQuery(queryBuilder);
	}

	@Override
	public List<Comment> getInappropriateCommentsForApproval() {
		QueryBuilder queryBuilder = new QueryBuilder("select c from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.inappropriate", "_inappropriate", true);
		return persistenceServices.executeQuery(queryBuilder);
	}

	@Override
	public void delete(Comment comment) {
		persistenceServices.remove(JpaComment.class, comment.getId());
	}

	@Override
	public void enable(Comment comment) {
		comment.setEnabled(true);
		persistenceServices.edit(comment);
	}

	@Override
	public void deleteWaitingCommentsForApproval() {
		QueryBuilder queryBuilder = new QueryBuilder("delete from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.enabled", "_ENABLED", false);
		persistenceServices.executeUpdate(queryBuilder);
	}

	@Override
	public Comment newComment() {
		return new JpaComment();
	}

	@Override
	public void markAsInappropriate(Comment comment, boolean flag) {
		comment.setInappropriate(flag);
		persistenceServices.edit(comment);
	}

	@Override
	public void deleteInappropriateCommentsForApproval() {
		QueryBuilder queryBuilder = new QueryBuilder("delete from " + JpaComment.class.getSimpleName() + " c");
		queryBuilder.addParameter("c.inappropriate", "_inappropriate", true);
		persistenceServices.executeUpdate(queryBuilder);
	}

	@Override
	public void deleteCommentsByUsername(User userToBeDeleted) {
		QueryBuilder update = new QueryBuilder("delete from " + JpaComment.class.getSimpleName() + " c");
		update.addParameter("c.fromUser", "_USER", userToBeDeleted);
		persistenceServices.executeUpdate(update);
	}
}
