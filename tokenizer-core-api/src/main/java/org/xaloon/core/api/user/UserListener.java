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
package org.xaloon.core.api.user;

import java.io.Serializable;

import org.xaloon.core.api.user.model.User;

/**
 * Default actions should be execute related to user acticity
 * 
 * @author vytautas r.
 */
public interface UserListener extends Serializable {
    /**
     * This method is invoked before user is removed from the system. One should register it's own listenere if there
     * are some actions to be taken
     * before final user removal, for example, clean up user comments, blogs, etc.
     * 
     * @param userToBeDeleted
     */
    void onBeforeDelete(User userToBeDeleted);
}
