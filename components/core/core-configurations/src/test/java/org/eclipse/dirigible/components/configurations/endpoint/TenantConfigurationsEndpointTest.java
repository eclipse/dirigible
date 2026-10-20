/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.configurations.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.dirigible.components.base.http.roles.ApplicationRoles;
import org.eclipse.dirigible.components.base.http.roles.Roles.RoleNames;
import org.eclipse.dirigible.components.configurations.domain.TenantConfiguration;
import org.eclipse.dirigible.components.configurations.service.SensitiveConfigurations;
import org.eclipse.dirigible.components.configurations.tenant.TenantConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

/**
 * The tenant configuration endpoints return sensitive values masked and never store the mask echoed
 * back by an unchanged edit; a tenant's owner manages its tenant settings only, never an
 * instance-wide key.
 */
public class TenantConfigurationsEndpointTest {

    private static final String TENANT_SETTING = "DIRIGIBLE_BRANDING_NAME";

    private static final String INSTANCE_WIDE = "DIRIGIBLE_TENANT_SMTP_PASSWORD";

    private static final List<TenantConfiguration> STORED =
            List.of(new TenantConfiguration(INSTANCE_WIDE, "p4ss"), new TenantConfiguration("DIRIGIBLE_TENANT_API_TOKEN", "tok-123"),
                    new TenantConfiguration("DIRIGIBLE_TENANT_UNSET_SECRET", null), new TenantConfiguration(TENANT_SETTING, "Acme"));

    private TenantConfigurationService service;

    private TenantConfigurationsEndpoint endpoint;

    @BeforeEach
    public void setUp() throws Exception {
        service = mock(TenantConfigurationService.class);
        when(service.listForCurrentTenant()).thenReturn(STORED);
        when(service.listPredefinedForCurrentTenant()).thenReturn(STORED);
        when(service.listPredefinedForCurrentTenant(any())).thenReturn(STORED);
        when(service.isTenantOverridable(TENANT_SETTING)).thenReturn(true);
        endpoint = new TenantConfigurationsEndpoint(service);
    }

    @Test
    public void findAllMasksSensitiveValues() {
        assertMasked(endpoint.findAll(caller(RoleNames.ADMINISTRATOR))
                             .getBody());
    }

    @Test
    public void findPredefinedMasksSensitiveValues() {
        assertMasked(endpoint.findPredefined(null)
                             .getBody());
    }

    @Test
    public void findPredefinedOfAGroupMasksSensitiveValues() {
        assertMasked(endpoint.findPredefined(List.of("branding"))
                             .getBody());
    }

    @Test
    public void setIgnoresTheEchoedMaskOfASensitiveKey() throws Exception {
        endpoint.set(new TenantConfiguration(INSTANCE_WIDE, SensitiveConfigurations.MASK), caller(RoleNames.OPERATOR));
        verify(service, never()).set(any(), any());
    }

    @Test
    public void setStoresANewSensitiveValue() throws Exception {
        endpoint.set(new TenantConfiguration(INSTANCE_WIDE, "n3w"), caller(RoleNames.ADMINISTRATOR));
        verify(service).set(INSTANCE_WIDE, "n3w");
    }

    @Test
    public void anOwnerSeesOnlyTheTenantSettingsAmongTheStoredEntries() {
        assertEquals(List.of(new TenantConfiguration(TENANT_SETTING, "Acme")), endpoint.findAll(caller(ApplicationRoles.OWNER))
                                                                                       .getBody());
    }

    @Test
    public void anOwnerSetsATenantSetting() throws Exception {
        endpoint.set(new TenantConfiguration(TENANT_SETTING, "Beta"), caller(ApplicationRoles.OWNER));
        verify(service).set(TENANT_SETTING, "Beta");
    }

    @Test
    public void anOwnerIsRefusedAnInstanceWideKey() throws Exception {
        ResponseStatusException set = assertThrows(ResponseStatusException.class,
                () -> endpoint.set(new TenantConfiguration(INSTANCE_WIDE, "n3w"), caller(ApplicationRoles.OWNER)));
        assertEquals(HttpStatus.FORBIDDEN, set.getStatusCode());
        ResponseStatusException delete =
                assertThrows(ResponseStatusException.class, () -> endpoint.delete(INSTANCE_WIDE, caller(ApplicationRoles.OWNER)));
        assertEquals(HttpStatus.FORBIDDEN, delete.getStatusCode());
        verify(service, never()).set(any(), any());
        verify(service, never()).delete(any());
    }

    @Test
    public void anOwnerDeletesATenantSetting() throws Exception {
        endpoint.delete(TENANT_SETTING, caller(ApplicationRoles.OWNER));
        verify(service).delete(TENANT_SETTING);
    }

    private static MockHttpServletRequest caller(String role) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addUserRole(role);
        return request;
    }

    private static void assertMasked(List<TenantConfiguration> body) {
        assertEquals(
                List.of(new TenantConfiguration(INSTANCE_WIDE, SensitiveConfigurations.MASK),
                        new TenantConfiguration("DIRIGIBLE_TENANT_API_TOKEN", SensitiveConfigurations.MASK),
                        new TenantConfiguration("DIRIGIBLE_TENANT_UNSET_SECRET", null), new TenantConfiguration(TENANT_SETTING, "Acme")),
                body);
        assertFalse(body.toString()
                        .contains("p4ss"));
        assertFalse(body.toString()
                        .contains("tok-123"));
    }

}
