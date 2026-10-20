/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.dirigible.components.engine.issues.IssueCaller.Caller;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Every tenant's issues are the instance operators' to read, and they work in the default tenant: a
 * tenant's own administrator is refused.
 */
class IssuesOperatorEndpointTest {

    private final IssueService service = mock(IssueService.class);

    private final IssueCaller caller = mock(IssueCaller.class);

    private final IssuesOperatorEndpoint endpoint = new IssuesOperatorEndpoint(service, caller);

    @Test
    void anAdministratorOfAnotherTenantIsRefused() {
        when(caller.current()).thenReturn(new Caller("t1", false, "tenant-admin"));

        ResponseStatusException refused = assertThrows(ResponseStatusException.class, () -> endpoint.search(null, null, null, 10));

        assertEquals(HttpStatus.FORBIDDEN, refused.getStatusCode());
        verify(service, never()).search(any(), any(), any(), anyInt());
    }

    @Test
    void anOperatorInTheDefaultTenantReadsEveryTenant() {
        when(caller.current()).thenReturn(new Caller("default-tenant", true, "operator"));

        endpoint.search(null, null, null, 10);

        verify(service).search(null, null, null, 10);
    }
}
