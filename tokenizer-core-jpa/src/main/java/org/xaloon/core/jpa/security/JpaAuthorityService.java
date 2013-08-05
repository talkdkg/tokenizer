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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.xaloon.core.api.persistence.PersistenceServices;
import org.xaloon.core.api.persistence.QueryBuilder;
import org.xaloon.core.api.plugin.Plugin;
import org.xaloon.core.api.security.AuthorityService;
import org.xaloon.core.api.security.model.Authority;
import org.xaloon.core.api.security.model.UserDetails;
import org.xaloon.core.api.util.UrlUtil;
import org.xaloon.core.jpa.security.model.JpaAuthority;

/**
 * @author vytautas r.
 */
@Named
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class JpaAuthorityService implements AuthorityService {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Inject
    private PersistenceServices persistenceServices;

    @Override
    public synchronized void registerPermissions(Plugin plugin) {
        List<Authority> authorities = plugin.getSupportedAuthorities();
        for (Authority authority : authorities) {
            findOrCreateAuthority(authority.getName());
        }
    }

    @Override
    public synchronized Authority findOrCreateAuthority(String permission) {
        Authority authority = getAuthorityByName(permission);
        if (authority == null) {
            authority = newAuthority();
            authority.setName(permission);
            authority = save(authority);
        }
        return authority;
    }

    @Override
    public Authority newAuthority() {
        return new JpaAuthority();
    }

    @Override
    public Authority save(Authority entity) {
        if (StringUtils.isEmpty(entity.getPath())) {
            entity.setPath(UrlUtil.encode(entity.getName()));
        }
        return persistenceServices.createOrEdit(entity);
    }

    @Override
    public List<Authority> getAuthorities(long first, long count) {
        QueryBuilder queryBuilder = new QueryBuilder("select a from " + JpaAuthority.class.getSimpleName() + " a");
        queryBuilder.setCount(count);
        queryBuilder.setFirstRow(first);
        return persistenceServices.executeQuery(queryBuilder);
    }

    @Override
    public Long getCount() {
        QueryBuilder queryBuilder = new QueryBuilder("select count(a) from " + JpaAuthority.class.getSimpleName()
                + " a");
        return (Long) persistenceServices.executeQuerySingle(queryBuilder);
    }

    @Override
    public void delete(Authority authority) {
        throw new NotImplementedException("This method is not supported! This is an application attribute!");
    }

    @Override
    public List<Authority> getAuthoritiesByUsername(String username) {
        QueryBuilder queryBuilder = new QueryBuilder("select a from " + JpaAuthority.class.getSimpleName()
                + " a join a.users u");
        queryBuilder.addParameter("u.username", "USERNAME", username);
        return persistenceServices.executeQuery(queryBuilder);
    }

    @Override
    public UserDetails revoke(UserDetails userDetails, Authority authority) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        tmp.getAuthorities().remove(authority);
        return persistenceServices.edit(tmp);
    }

    @Override
    public Authority getAuthorityByName(String name) {
        QueryBuilder queryBuilder = new QueryBuilder("select a from " + JpaAuthority.class.getSimpleName() + " a");
        queryBuilder.addParameter("a.name", "_AUTHORITY_NAME", name);
        return persistenceServices.executeQuerySingle(queryBuilder);
    }

    @Override
    public Authority getAuthorityByPath(String path) {
        QueryBuilder queryBuilder = new QueryBuilder("select a from " + JpaAuthority.class.getSimpleName() + " a");
        queryBuilder.addParameter("a.path", "PATH", path);
        return persistenceServices.executeQuerySingle(queryBuilder);
    }

    @Override
    public UserDetails assignAuthoritiesByName(UserDetails userDetails, List<String> selections) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        for (String authorityName : selections) {
            Authority authority = findOrCreateAuthority(authorityName);
            tmp.getAuthorities().add(authority);
        }
        return persistenceServices.edit(tmp);
    }

    @Override
    public UserDetails assignAuthorities(UserDetails userDetails, List<Authority> selections) {
        UserDetails tmp = persistenceServices.find(userDetails.getClass(), userDetails.getId());
        tmp.getAuthorities().addAll(selections);
        return persistenceServices.edit(tmp);
    }

    @Override
    public Authority assignChildren(Authority parent, List<Authority> selections) {
        throw new NotImplementedException("This method is not supported!");
    }

    @Override
    public Authority revokeChild(Authority parent, Authority authority) {
        throw new NotImplementedException("This method is not supported!");
    }
}
