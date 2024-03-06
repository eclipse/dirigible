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

import java.util.Optional;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.exceptions.TenantNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The Class UserService.
 */
@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class UserService {

    private final TenantService tenantService;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(TenantService tenantService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.tenantService = tenantService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createNewUser(String username, String password, String tenantId) {
        Tenant tenant = tenantService.findById(tenantId)
                                     .orElseThrow(() -> new TenantNotFoundException("Tenant " + tenantId + " not found."));
        return userRepository.save(new User(tenant, username, passwordEncoder.encode(password)));
    }

    public Optional<User> findUserByUsernameAndTenantId(String username, String tenantId) {
        return userRepository.findUserByUsernameAndTenantId(username, tenantId);
    }
}
