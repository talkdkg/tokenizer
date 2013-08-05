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
package org.xaloon.core.api.plugin.comment;

import java.io.Serializable;
import java.util.List;

import org.xaloon.core.api.user.model.User;

/**
 * @author vytautas r.
 */
public interface CommentDao extends Serializable {
    /**
     * Property for counter to identify that comment count should be incremented
     */
    String ENTRY_COUNT_COMMENT = "ENTRY_COUNT_COMMENT";

    /**
     * Save comment to storage
     * 
     * @param comment
     */
    void save(Comment comment);

    /**
     * Retrieve comment list for selected commentable object
     * 
     * @param commentable
     * @param first
     * @param count
     * @return list of comments
     */
    List<Comment> getComments(Commentable commentable, long first, long count);

    /**
     * Delete all comments. NOT RECCOMENDED!
     */
    void deleteAll();

    /**
     * Retrieve count of selected commentable object
     * 
     * @param commentable
     * @return how many comments there are by commentable object
     */
    Long count(Commentable commentable);

    /**
     * Load concrete comment by id
     * 
     * @param id
     * @return comment instance by id
     */
    Comment load(Long id);

    /**
     * Retrieve all comments, waiting for approval of administrator
     * 
     * @return list of waiting comments
     */
    List<Comment> getWaitingCommentsForApproval();

    /**
     * Delete concrete comment
     * 
     * @param comment
     */
    void delete(Comment comment);

    /**
     * Modify comment and save
     * 
     * @param comment
     */
    void enable(Comment comment);

    /**
     * Delete all comments, which are still waiting for approval. Should be a spam.
     */
    void deleteWaitingCommentsForApproval();

    /**
     * @return create new instance of Comment persistable object
     */
    Comment newComment();

    /**
     * @param comment
     *            comment which contains inappropriate content
     * @param flag
     *            true/false - false means rollback inappropriate comment to proper state
     */
    void markAsInappropriate(Comment comment, boolean flag);

    /**
     * Returns the list of inappropriate comments
     * 
     * @return the list of inappropriate comments
     */
    List<Comment> getInappropriateCommentsForApproval();

    /**
     * Delete all comments, which are inappropriate. Should be a spam.
     */
    void deleteInappropriateCommentsForApproval();

    /**
     * Delete all comments for specified user
     * 
     * @param userToBeDeleted
     */
    void deleteCommentsByUsername(User userToBeDeleted);
}
