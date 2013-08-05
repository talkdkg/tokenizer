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
package org.xaloon.core.impl.security;

import java.util.ArrayList;
import java.util.List;

import org.xaloon.core.api.security.model.Authority;
import org.xaloon.core.api.security.model.SecurityRole;
import org.xaloon.core.api.security.model.UserDetails;

/**
 * @author vytautas r.
 */
public class DefaultSecurityRole extends DefaultSecurityEntity implements SecurityRole {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private List<Authority> authorities = new ArrayList<Authority>();
    private List<UserDetails> users = new ArrayList<UserDetails>();

    /**
     * Construct.
     * 
     * @param name
     */
    public DefaultSecurityRole(String name) {
        super(name);
    }

    /**
     * Gets users.
     * 
     * @return users
     */
    public List<UserDetails> getUsers() {
        return users;
    }

    /**
     * Sets users.
     * 
     * @param users
     *            users
     */
    public void setUsers(List<UserDetails> users) {
        this.users = users;
    }

    /**
     * Gets authorities.
     * 
     * @return authorities
     */
    public List<Authority> getAuthorities() {
        return authorities;
    }

    /**
     * Sets authorities.
     * 
     * @param authorities
     *            authorities
     */
    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
