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
package org.eclipse.dirigible.components.tenants.init;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Optional;
import org.eclipse.dirigible.components.base.ApplicationListenersOrder.ApplicationReadyEventListeners;
import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.domain.UserRoleAssignment;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "basic.enabled", havingValue = "true")
@Order(ApplicationReadyEventListeners.ADMIN_USER_INITIALIZER)
@Component
class AdminUserInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final Decoder base64Decoder;
    private final UserService userService;
    private final Tenant defaultTenant;
    private final RoleService roleService;

    AdminUserInitializer(UserService userService, @DefaultTenant Tenant defaultTenant, RoleService roleService) {
        this.userService = userService;
        this.defaultTenant = defaultTenant;
        this.roleService = roleService;
        this.base64Decoder = Base64.getDecoder();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Executing...");
        initAdminUser();
        LOGGER.info("Completed.");

    }

    private void initAdminUser() {
        String base64Username = org.eclipse.dirigible.commons.config.Configuration.get(
                org.eclipse.dirigible.commons.config.Configuration.BASIC_USERNAME, "YWRtaW4="); // admin
        String base64Password = org.eclipse.dirigible.commons.config.Configuration.get(
                org.eclipse.dirigible.commons.config.Configuration.BASIC_PASSWORD, "YWRtaW4="); // admin

        String username = decode(base64Username);
        String password = decode(base64Password);

        Optional<User> existingUser = userService.findUserByUsernameAndTenantId(username, defaultTenant.getId());
        if (existingUser.isPresent()) {
            LOGGER.info("A user with username [{}] for tenant [{}] already exists. Skipping its creation.", username,
                    defaultTenant.getId());
            return;
        }
        User adminUser = userService.createNewUser(username, password, defaultTenant.getId());
        LOGGER.info("Created admin user with username [{}] for tenant with id [{}]", username, defaultTenant.getId());
        for (Roles predefinedRole : Roles.values()) {
            assignRole(adminUser, predefinedRole);
        }
    }

    private String decode(String base64String) {
        byte[] decodedValue = base64Decoder.decode(base64String);
        return new String(decodedValue, StandardCharsets.UTF_8);
    }

    private void assignRole(User user, Roles predefinedRole) {
        UserRoleAssignment assignment = new UserRoleAssignment();
        assignment.setUser(user);

        String roleName = predefinedRole.getRoleName();
        Role role = roleService.findByName(roleName);
        userService.assignUserRoles(user, role);

        LOGGER.info("Assigned role [{}] to user [{}] in tenant [{}]", roleName, user.getUsername(), user.getTenant()
                                                                                                        .getId());
    }

}
