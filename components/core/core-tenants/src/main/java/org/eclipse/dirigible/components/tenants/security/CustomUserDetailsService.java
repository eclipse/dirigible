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

import static org.eclipse.dirigible.components.base.http.roles.Roles.ADMINISTRATOR;

import java.util.ArrayList;

import org.eclipse.dirigible.components.tenants.repository.UserRepository;
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

    /**
     * Instantiates a new custom user details service.
     *
     * @param userRepository the user repository
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username.
     *
     * @param email the email
     * @return the user details
     * @throws UsernameNotFoundException the username not found exception
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var tenant = TenantContext.getCurrentTenant();

        if (tenant != null) {
            return loadUser(email, tenant);
        } else {
            return loadGeneralAdmin(email);
        }
    }

    /**
     * Load user.
     *
     * @param email the email
     * @param tenant the tenant
     * @return the user details
     */
    private UserDetails loadUser(String email, String tenant) {
        var user = userRepository.findUser(email, tenant)
                                 .orElseThrow(() -> new UsernameNotFoundException("'" + email + "' / '" + tenant + "' was not found."));

        var auths = new ArrayList<GrantedAuthority>();
        auths.add(new SimpleGrantedAuthority(user.getRole()
                                                 .getRoleName()));
        return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getId(), user.getTenant()
                                                                                            .getId(),
                auths);
    }

    /**
     * Load general admin.
     *
     * @param email the email
     * @return the user details
     */
    private UserDetails loadGeneralAdmin(String email) {
        var admin = userRepository.findGeneralAdmin(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("'" + email + "' was not found as a general admin."));
        var auths = new ArrayList<GrantedAuthority>();
        auths.add(new SimpleGrantedAuthority(ADMINISTRATOR.getRoleName()));
        return new CustomUserDetails(admin.getEmail(), admin.getPassword(), admin.getId(), null, auths);
    }
}
