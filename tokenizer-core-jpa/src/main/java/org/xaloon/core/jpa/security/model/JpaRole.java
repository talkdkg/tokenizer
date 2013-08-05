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
package org.xaloon.core.jpa.security.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xaloon.core.api.security.model.SecurityRole;
import org.xaloon.core.api.util.UrlUtil;
import org.xaloon.core.jpa.model.BookmarkableEntity;

/**
 * @author vytautas r.
 */
@Entity
@Table(name = "XAL_SECURITY_ROLE", uniqueConstraints = @UniqueConstraint(columnNames = { "ROLE_NAME" }))
public class JpaRole extends BookmarkableEntity implements SecurityRole {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Column(name = "ROLE_NAME", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "XAL_SECURITY_USER_ROLES", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USER_DETAILS_ID", referencedColumnName = "ID"))
    private List<JpaUserDetails> users = new ArrayList<JpaUserDetails>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "XAL_SECURITY_ROLE_AUTHORITIES", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID"))
    private List<JpaAuthority> authorities = new ArrayList<JpaAuthority>();

    /**
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets users.
     * 
     * @return users
     */
    public List<JpaUserDetails> getUsers() {
        return users;
    }

    /**
     * Sets users.
     * 
     * @param users
     *            users
     */
    public void setUsers(List<JpaUserDetails> users) {
        this.users = users;
    }

    /**
     * Gets authorities.
     * 
     * @return authorities
     */
    public List<JpaAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Sets authorities.
     * 
     * @param authorities
     *            authorities
     */
    public void setAuthorities(List<JpaAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JpaRole)) {
            return false;
        }
        JpaRole entity = (JpaRole) obj;

        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getId(), entity.getId());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getId());
        return hashCodeBuilder.hashCode();
    }

    @Override
    @PrePersist
    protected void beforeCreate() {
        setPath(UrlUtil.encode(getName()));
    }
}
