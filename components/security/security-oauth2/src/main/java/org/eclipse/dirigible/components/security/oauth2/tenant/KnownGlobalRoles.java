/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.security.oauth2.tenant;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Tells the global roles this instance knows from the groups that only happen to carry no tenant.
 *
 * <p>
 * Every group of a token that does not name a tenant of this application becomes a global role -
 * including the groups an identity provider manages itself (Cognito puts a federated user in
 * {@code <userPoolId>_<providerName>}) and those of other systems sharing the user pool. Such a
 * group grants nothing an access rule checks, so it must not be what lets a user without a tenant
 * into the default tenant. A role is known when it is a platform role or one registered in this
 * instance by a {@code .roles} artefact; the match is exact, like the authorities.
 */
@Component
class KnownGlobalRoles {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnownGlobalRoles.class);

    private static final Set<String> PLATFORM_ROLES = Arrays.stream(Roles.values())
                                                            .map(Roles::getRoleName)
                                                            .collect(Collectors.toUnmodifiableSet());

    private final RoleService roleService;

    KnownGlobalRoles(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Whether any of the global roles is known to this instance. The platform roles are checked first,
     * without touching the database.
     *
     * @param globalRoles the global roles of a user
     * @return true if at least one of them is a platform role or a registered role
     */
    boolean anyKnown(Set<String> globalRoles) {
        if (globalRoles.stream()
                       .anyMatch(PLATFORM_ROLES::contains)
                || globalRoles.stream()
                              .anyMatch(roleService::roleExistsByName)) {
            return true;
        }
        if (!globalRoles.isEmpty()) {
            LOGGER.debug("None of the global roles {} is known to this instance.", globalRoles);
        }
        return false;
    }
}
