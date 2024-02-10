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
package org.eclipse.dirigible.components.tenants.service;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.exceptions.TenantNotFoundException;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.eclipse.dirigible.components.tenants.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The Class UserService.
 */
@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class UserService {

    /** The tenant repository. */
    private final TenantRepository tenantRepository;

    /** The user repository. */
    private final UserRepository userRepository;

    /** The password encoder. */
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Instantiates a new user service.
     *
     * @param tenantRepository the tenant repository
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     */
    public UserService(TenantRepository tenantRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates the new user.
     *
     * @param email the email
     * @param password the password
     * @param tenantId the tenant id
     * @param role the role
     */
    public void createNewUser(String email, String password, long tenantId, Roles role) {
        var tenant = tenantRepository.findById(tenantId)
                                     .orElseThrow(() -> new TenantNotFoundException("Tenant " + tenantId + " not found."));
        userRepository.save(new User(tenant, email, passwordEncoder.encode(password), role));
    }
}
