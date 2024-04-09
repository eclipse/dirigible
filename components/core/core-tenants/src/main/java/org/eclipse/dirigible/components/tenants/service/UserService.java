/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.domain.UserRoleAssignment;
import org.eclipse.dirigible.components.tenants.exceptions.TenantNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The Class UserService.
 */
@Service
public class UserService {

    /** The tenant service. */
    private final TenantService tenantService;

    /** The user repository. */
    private final UserRepository userRepository;

    /** The password encoder. */
    private final BCryptPasswordEncoder passwordEncoder;

    /** The assignment repository. */
    private final UserRoleAssignmentRepository assignmentRepository;

    /**
     * Instantiates a new user service.
     *
     * @param tenantService the tenant service
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     * @param assignmentRepository the assignment repository
     */
    public UserService(TenantService tenantService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
            UserRoleAssignmentRepository assignmentRepository) {
        this.tenantService = tenantService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Creates the new user.
     *
     * @param username the username
     * @param password the password
     * @param tenantId the tenant id
     * @return the user
     */
    public User createNewUser(String username, String password, String tenantId) {
        Tenant tenant = tenantService.findById(tenantId)
                                     .orElseThrow(() -> new TenantNotFoundException("Tenant " + tenantId + " not found."));
        return userRepository.save(new User(tenant, username, passwordEncoder.encode(password)));
    }

    /**
     * Find user by username and tenant id.
     *
     * @param username the username
     * @param tenantId the tenant id
     * @return the optional
     */
    public Optional<User> findUserByUsernameAndTenantId(String username, String tenantId) {
        return userRepository.findUserByUsernameAndTenantId(username, tenantId);
    }

    /**
     * Gets the user role names.
     *
     * @param user the user
     * @return the user role names
     */
    public Set<String> getUserRoleNames(User user) {
        return getUserRoles(user).stream()
                                 .map(Role::getName)
                                 .collect(Collectors.toSet());
    }

    /**
     * Gets the user roles.
     *
     * @param user the user
     * @return the user roles
     */
    private Set<Role> getUserRoles(User user) {
        return assignmentRepository.findByUser(user)
                                   .stream()
                                   .map(a -> a.getRole())
                                   .collect(Collectors.toSet());
    }

    /**
     * Assign user roles.
     *
     * @param user the user
     * @param roles the roles
     */
    public void assignUserRoles(User user, Role... roles) {
        for (Role role : roles) {
            UserRoleAssignment assignment = new UserRoleAssignment();
            assignment.setUser(user);
            assignment.setRole(role);

            assignmentRepository.save(assignment);
        }
    }
}
