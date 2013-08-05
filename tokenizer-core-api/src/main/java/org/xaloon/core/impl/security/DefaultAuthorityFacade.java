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

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xaloon.core.api.plugin.Plugin;
import org.xaloon.core.api.security.AuthorityFacade;
import org.xaloon.core.api.security.AuthorityService;
import org.xaloon.core.api.security.RoleService;
import org.xaloon.core.api.security.model.Authority;
import org.xaloon.core.api.security.model.SecurityRole;

/**
 * Default implementation for authority facade
 * 
 * @author vytautas r.
 * @version 1.1, 02/06/12
 * @since 1.5
 */
@Named("authorityFacade")
public class DefaultAuthorityFacade implements AuthorityFacade {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthorityFacade.class);

	@Inject
	private RoleService roleService;

	@Inject
	private AuthorityService authorityService;

	@Override
	public void registerRoles(Plugin plugin) {
		for (SecurityRole role : plugin.getSupportedRoles()) {
			SecurityRole securityRole = roleService.findOrCreateAuthority(role.getName());
			registerAuthoritiesForRole(securityRole, role.getAuthorities());
		}
	}

	private void registerAuthoritiesForRole(SecurityRole securityRole, List<Authority> authorities) {
		List<Authority> authoritiesToAssign = new ArrayList<Authority>();
		for (Authority authority : authorities) {
			Authority persistedAuthority = authorityService.findOrCreateAuthority(authority.getName());
			if (!securityRole.getAuthorities().contains(persistedAuthority)) {
				authoritiesToAssign.add(persistedAuthority);
			}
		}
		if (!authoritiesToAssign.isEmpty()) {
			LOGGER.debug(String.format("Registering new security role '%s' and it's authorities: %s", securityRole.getName(),
				authoritiesToAssign.toString()));
			roleService.assignChildren(securityRole, authoritiesToAssign);
		}
	}
}
