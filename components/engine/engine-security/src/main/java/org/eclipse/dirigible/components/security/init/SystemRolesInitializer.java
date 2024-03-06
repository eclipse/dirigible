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
package org.eclipse.dirigible.components.security.init;

import org.eclipse.dirigible.components.base.ApplicationListenersOrder.ApplicationReadyEventListeners;
import org.eclipse.dirigible.components.base.artefact.ArtefactPhase;
import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
@Order(ApplicationReadyEventListeners.SYSTEM_ROLES_INITIALIZER)
@Component
class SystemRolesInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemRolesInitializer.class);

    private final RoleService roleService;

    SystemRolesInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Executing...");

        for (Roles predefinedRole : Roles.values()) {
            createRole(predefinedRole);
        }

        LOGGER.info("Completed...");
    }

    private void createRole(Roles predefinedRole) {
        String roleName = predefinedRole.getRoleName();
        if (roleService.roleExistsByName(roleName)) {
            LOGGER.info("Role with name [{}] already exists. Skipping its creation.", roleName);
            return;
        }
        Role role = new Role();
        role.setType(Role.ARTEFACT_TYPE);
        role.setName(roleName);
        role.setDescription("System role");
        role.setKey("n/a");
        role.setPhase(ArtefactPhase.CREATE);
        role.setLocation("SYSTEM_n/a");

        role.updateKey();

        Role savedRole = roleService.save(role);

        LOGGER.info("Created system role [{}].", savedRole);
    }

}
