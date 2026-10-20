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

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Who is calling and in which tenant - always taken from the request, never from a parameter, so
 * the tenant filter of the issues endpoints cannot be talked around.
 */
@Component
class IssueCaller {

    private final TenantContext tenantContext;

    private final Tenant defaultTenant;

    IssueCaller(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    /**
     * The caller of the current request.
     *
     * @return the caller
     */
    Caller current() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Tenant tenant = tenantContext.isInitialized() ? tenantContext.getCurrentTenant() : defaultTenant;
        return new Caller(tenant.getId(), tenant.isDefault(), authentication.getName());
    }

    /**
     * The caller of a request.
     *
     * @param tenantId the tenant the request runs in
     * @param inDefaultTenant whether that is the instance's default tenant, where its operators work
     * @param principal who is calling
     */
    record Caller(String tenantId, boolean inDefaultTenant, String principal) {
    }
}
