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
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.repository.UserRepository;
import org.eclipse.dirigible.components.tenants.repository.UserRoleAssignmentRepository;
import org.eclipse.dirigible.components.tenants.tenant.Tenant;
import org.eclipse.dirigible.components.tenants.tenant.TenantContext;
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

    /** The user repository. */
    private final UserRepository userRepository;
    private final UserRoleAssignmentRepository userRoleAssignmentRepository;

    public CustomUserDetailsService(UserRepository userRepository, UserRoleAssignmentRepository userRoleAssignmentRepository) {
        this.userRepository = userRepository;
        this.userRoleAssignmentRepository = userRoleAssignmentRepository;
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
        Tenant tenant = TenantContext.getCurrentTenant();

        User user = userRepository.findUserByTenantId(username, tenant.getId())
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                          "User with username [" + username + "] in tenant [" + tenant + "] was not found."));

        Set<String> userRoles = getUserRoles(user);
        Set<GrantedAuthority> auths = userRoles.stream()
                                               .map(r -> new SimpleGrantedAuthority(r))
                                               .collect(Collectors.toSet());

        return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getId(), user.getTenant()
                                                                                            .getId(),
                auths);
    }

    private Set<String> getUserRoles(User user) {
        return userRoleAssignmentRepository.findByUser(user)
                                           .stream()
                                           .map(a -> a.getRole()
                                                      .getName())
                                           .collect(Collectors.toSet());
    }
}
