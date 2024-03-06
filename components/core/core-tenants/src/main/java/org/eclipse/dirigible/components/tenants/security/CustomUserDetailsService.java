/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.security;

import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.service.UserRoleAssignmentService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The Class CustomUserDetailsService.
 */
@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserService userService;
    private final UserRoleAssignmentService userRoleAssignmentService;
    private final TenantContext tenantContext;

    public CustomUserDetailsService(UserService userService, UserRoleAssignmentService userRoleAssignmentService,
            TenantContext tenantContext) {
        this.userService = userService;
        this.userRoleAssignmentService = userRoleAssignmentService;
        this.tenantContext = tenantContext;
    }

    /**
     * Load user by username.
     *
     * @param username the email
     * @return the user details
     * @throws UsernameNotFoundException the username not found exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Tenant tenant = tenantContext.getCurrentTenant();

        User user = userService.findUserByUsernameAndTenantId(username, tenant.getId())
                               .orElseThrow(() -> new UsernameNotFoundException(
                                       "User with username [" + username + "] in tenant [" + tenant + "] was not found."));

        Set<String> userRoles = getUserRoles(user);
        LOGGER.debug("User [{}] has assigned roles [{}]", user, userRoles);
        Set<GrantedAuthority> auths = userRoles.stream()
                                               .map(r -> new SimpleGrantedAuthority(r))
                                               .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), auths);
    }

    private Set<String> getUserRoles(User user) {
        return userRoleAssignmentService.findByUser(user)
                                        .stream()
                                        .map(a -> a.getRole()
                                                   .getName())
                                        .collect(Collectors.toSet());
    }
}
