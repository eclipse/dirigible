/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.cms.s3.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.eclipse.dirigible.components.api.s3.S3Facade;
import org.eclipse.dirigible.components.base.callable.CallableResultAndException;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** The S3 CMS teardown deletes the tenant's root folder from inside the tenant's context. */
class CmsS3TenantTeardownTest {

    private final TenantContext tenantContext = mock(TenantContext.class);
    private final List<String> deletes = new ArrayList<>();
    private String currentTenant = "default";
    private MockedStatic<S3Facade> facade;

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
        facade = mockStatic(S3Facade.class);
        facade.when(() -> S3Facade.deleteFolder(anyString()))
              .thenAnswer(invocation -> deletes.add(currentTenant + ":" + invocation.getArgument(0)));
    }

    @AfterEach
    void release() {
        facade.close();
        Configuration.remove("DIRIGIBLE_CMS_PROVIDER");
    }

    @Test
    void theRootIsDeletedInsideTheTenantsContext() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", CmsS3TenantTeardown.PROVIDER);

        new CmsS3TenantTeardown(tenantContext).teardown("acme");

        assertEquals(List.of("acme:/"), deletes);
    }

    @Test
    void anotherProviderTouchesNothing() {
        Configuration.set("DIRIGIBLE_CMS_PROVIDER", "cms-provider-internal");

        new CmsS3TenantTeardown(tenantContext).teardown("acme");

        assertEquals(List.of(), deletes);
        verifyNoInteractions(tenantContext);
    }

    @Test
    void noProviderConfiguredMeansTheInternalOneAndTouchesNothing() {
        new CmsS3TenantTeardown(tenantContext).teardown("acme");

        assertEquals(List.of(), deletes);
        verifyNoInteractions(tenantContext);
    }

    @Test
    void itRunsWithTheOtherContentParticipants() {
        assertEquals(600, new CmsS3TenantTeardown(tenantContext).order());
    }
}
