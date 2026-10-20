/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.sharepoint.provider;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.sharepoint.CmisObjectNotFoundException;
import org.eclipse.dirigible.components.api.sharepoint.SharePointOperationException;
import org.eclipse.dirigible.components.api.sharepoint.SharepointFacade;
import org.eclipse.dirigible.components.base.callable.CallableResultAndException;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * The SharePoint CMS teardown deletes the tenant's folder from inside the tenant's context, and a
 * missing folder is success.
 */
class CmsSharepointTenantTeardownTest {

    private final TenantContext tenantContext = mock(TenantContext.class);
    private final List<String> deletes = new ArrayList<>();
    private String currentTenant = "default";
    private MockedStatic<SharepointFacade> facade;

    @BeforeEach
    void wire() {
        when(tenantContext.execute(eq("acme"), any(CallableResultAndException.class))).thenAnswer(invocation -> {
            currentTenant = "acme";
            try {
                return ((CallableResultAndException<?, ?>) invocation.getArgument(1)).call();
            } finally {
                currentTenant = "default";
            }
        });
        facade = mockStatic(SharepointFacade.class);
    }

    @AfterEach
    void release() {
        facade.close();
        Configuration.remove("DIRIGIBLE_CMS_PROVIDER");
    }

    @Test
    void theRootIsDeletedInsideTheTenantsContext() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", CmsSharepointTenantTeardown.PROVIDER);
        facade.when(() -> SharepointFacade.deleteFolder(anyString()))
              .thenAnswer(invocation -> deletes.add(currentTenant + ":" + invocation.getArgument(0)));

        new CmsSharepointTenantTeardown(tenantContext).teardown("acme");

        assertEquals(List.of("acme:/"), deletes);
    }

    @Test
    void aMissingFolderIsSuccess() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", CmsSharepointTenantTeardown.PROVIDER);
        facade.when(() -> SharepointFacade.deleteFolder(anyString()))
              .thenThrow(new CmisObjectNotFoundException("Object not found: acme/"));

        CmsSharepointTenantTeardown teardown = new CmsSharepointTenantTeardown(tenantContext);

        assertDoesNotThrow(() -> teardown.teardown("acme"));
    }

    @Test
    void anyOtherFailurePropagates() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", CmsSharepointTenantTeardown.PROVIDER);
        facade.when(() -> SharepointFacade.deleteFolder(anyString()))
              .thenThrow(new SharePointOperationException("Failed to delete: acme/"));

        CmsSharepointTenantTeardown teardown = new CmsSharepointTenantTeardown(tenantContext);

        assertThrows(SharePointOperationException.class, () -> teardown.teardown("acme"));
    }

    @Test
    void anotherProviderTouchesNothing() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", "cms-provider-s3");

        new CmsSharepointTenantTeardown(tenantContext).teardown("acme");

        assertEquals(List.of(), deletes);
        verifyNoInteractions(tenantContext);
    }

    @Test
    void itRunsWithTheOtherContentParticipants() {
        assertEquals(600, new CmsSharepointTenantTeardown(tenantContext).order());
    }
}
