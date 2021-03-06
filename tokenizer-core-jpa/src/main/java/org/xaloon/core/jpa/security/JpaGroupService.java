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
package org.xaloon.core.jpa.security;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.api.security.GroupService;
import org.xaloon.core.api.security.model.SecurityGroup;
import org.xaloon.core.api.security.model.SecurityRole;
import org.xaloon.core.api.security.model.UserDetails;
import org.xaloon.core.api.util.UrlUtil;
import org.xaloon.core.jpa.security.model.JpaGroup;
import org.xaloon.core.jpa.security.model.JpaUserDetails;

/**
 * @author vytautas r.
 */
@Named
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaGroupService implements GroupService {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Inject
    private PersistenceServices persistenceServices;

    @Override
    public List<SecurityGroup> getAuthorities(long first, long count) {
        QueryBuilder queryBuilder = new QueryBuilder("select g from " + JpaGroup.class.getSimpleName() + " g");
        queryBuilder.setCount(count);
        queryBuilder.setFirstRow(first);
        return persistenceServices.executeQuery(queryBuilder);
    }

    @Override
    public Long getCount() {
        QueryBuilder queryBuilder = new QueryBuilder("select count(g) from " + JpaGroup.class.getSimpleName() + " g");
        return (Long) persistenceServices.executeQuerySingle(queryBuilder);
    }

    @Override
    public SecurityGroup newAuthority() {
        return new JpaGroup();
    }

    @Override
    public void delete(SecurityGroup authority) {
        SecurityGroup tmp = persistenceServices.find(authority.getClass(), authority.getId());

        // !!! Eclipselink related issue. it does not return the list of users.
        // Remove the role from users
        List<UserDetails> users = getUsersByAuthority(authority);
        for (UserDetails details : users) {
            details.getGroups().remove(tmp);
        }

        persistenceServices.remove(tmp);
    }

    private List<UserDetails> getUsersByAuthority(SecurityGroup authority) {
        QueryBuilder queryBuilder = new QueryBuilder("select u from " + JpaUserDetails.class.getSimpleName()
                + " u join u.groups g");
        queryBuilder.addParameter("g.id", "_ID", authority.getId());
        return persistenceServices.executeQuery(queryBuilder);
    }

    @Override
    public SecurityGroup save(SecurityGroup authority) {
        if (authority == null) {
            return null;
        }
        if (StringUtils.isEmpty(authority.getPath())) {
            authority.setPath(UrlUtil.encode(authority.getName()));
        }
        return persistenceServices.createOrEdit(authority);
    }

    @Override
    public SecurityGroup findOrCreateAuthority(String authorityName) {
        return null;
    }

    @Override
    public List<SecurityGroup> getAuthoritiesByUsername(String username) {
        QueryBuilder queryBuilder = new QueryBuilder("select g from " + JpaGroup.class.getSimpleName()
                + " g join g.users u");
        queryBuilder.addParameter("u.username", "USERNAME", username);
        return persistenceServices.executeQuery(queryBuilder);
    }

    @Override
    public UserDetails revoke(UserDetails userDetails, SecurityGroup authority) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        tmp.getGroups().remove(authority);
        return persistenceServices.edit(tmp);
    }

    @Override
    public SecurityGroup getAuthorityByPath(String path) {
        QueryBuilder queryBuilder = new QueryBuilder("select g from " + JpaGroup.class.getSimpleName() + " g");
        queryBuilder.addParameter("g.path", "PATH", path);
        return persistenceServices.executeQuerySingle(queryBuilder);
    }

    @Override
    public UserDetails assignAuthoritiesByName(UserDetails userDetails, List<String> selections) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        for (String groupName : selections) {
            SecurityGroup group = findOrCreateAuthority(groupName);
            tmp.getGroups().add(group);
        }
        return persistenceServices.edit(tmp);
    }

    @Override
    public UserDetails assignAuthorities(UserDetails userDetails, List<SecurityGroup> selections) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        tmp.getGroups().addAll(selections);
        return persistenceServices.edit(tmp);
    }

    @Override
    public SecurityGroup assignChildren(SecurityGroup parent, List<SecurityRole> selections) {
        SecurityGroup group = persistenceServices.find(parent.getClass(), parent.getId());
        group.getRoles().addAll(selections);
        return persistenceServices.edit(group);
    }

    @Override
    public SecurityGroup revokeChild(SecurityGroup parent, SecurityRole authority) {
        SecurityGroup group = persistenceServices.find(parent.getClass(), parent.getId());
        group.getRoles().remove(authority);
        return persistenceServices.edit(group);
    }

    @Override
    public SecurityGroup getAuthorityByName(String name) {
        QueryBuilder queryBuilder = new QueryBuilder("select g from " + JpaGroup.class.getSimpleName() + " g");
        queryBuilder.addParameter("g.name", "_NAME", name);
        return persistenceServices.executeQuerySingle(queryBuilder);
    }

}
