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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.dirigible.components.security.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Which global roles admit a user without a tenant into the default tenant: the platform roles and
 * the roles registered in the instance, never a group that only happens to carry no tenant.
 */
@ExtendWith(MockitoExtension.class)
class KnownGlobalRolesTest {

    private static final String FEDERATION_GROUP = "eu-central-1_AbCdEf123_Google";

    @Mock
    private RoleService roleService;

    private KnownGlobalRoles knownGlobalRoles;

    @BeforeEach
    void setUp() {
        knownGlobalRoles = new KnownGlobalRoles(roleService);
    }

    @Test
    void aPlatformRoleIsKnownWithoutAskingTheDatabase() {
        assertThat(knownGlobalRoles.anyKnown(Set.of("DEVELOPER", FEDERATION_GROUP))).isTrue();

        verifyNoInteractions(roleService);
    }

    @Test
    void aRegisteredProjectRoleIsKnown() {
        when(roleService.roleExistsByName("library-librarian")).thenReturn(true);

        assertThat(knownGlobalRoles.anyKnown(Set.of("library-librarian"))).isTrue();
    }

    @Test
    void anUnknownGroupIsNotKnown() {
        when(roleService.roleExistsByName(FEDERATION_GROUP)).thenReturn(false);

        assertThat(knownGlobalRoles.anyKnown(Set.of(FEDERATION_GROUP))).isFalse();
    }

    @Test
    void noGlobalRoleIsNotKnown() {
        assertThat(knownGlobalRoles.anyKnown(Set.of())).isFalse();

        verifyNoInteractions(roleService);
    }

    @Test
    void theFirstRegisteredRoleEndsTheSearch() {
        when(roleService.roleExistsByName(anyString())).thenReturn(true);
        Set<String> globalRoles = new LinkedHashSet<>(List.of("library-librarian", FEDERATION_GROUP, "other-group"));

        assertThat(knownGlobalRoles.anyKnown(globalRoles)).isTrue();

        verify(roleService, times(1)).roleExistsByName(anyString());
    }
}
