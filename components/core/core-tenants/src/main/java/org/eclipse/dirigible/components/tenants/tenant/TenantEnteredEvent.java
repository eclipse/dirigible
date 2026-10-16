/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.tenant;

import java.time.Instant;

/**
 * A signed-in user entered a tenant - picked it on the tenant picker, or was placed in their only
 * one. Published once per selection, never for a bearer request (which names its tenant per
 * request) and never for the per-request consistency check, so a listener can treat it as a sign-in
 * into the tenant.
 *
 * @param tenantId the tenant entered
 * @param principal the principal name
 * @param email the email claim of the identity token, or null when the token carries none
 * @param at when
 * @param previousTenantId the tenant the session had selected before, or null when this is the
 *        first selection of the session - a switch between two tenants is told apart from a sign-in
 *        by it
 */
public record TenantEnteredEvent(String tenantId, String principal, String email, Instant at, String previousTenantId) {

    /**
     * A first selection of the session.
     *
     * @param tenantId the tenant entered
     * @param principal the principal name
     * @param email the email claim of the identity token, or null
     * @param at when
     */
    public TenantEnteredEvent(String tenantId, String principal, String email, Instant at) {
        this(tenantId, principal, email, at, null);
    }

    /**
     * Whether the user moved here from another tenant without signing in again.
     *
     * @return true for a switch
     */
    public boolean isSwitch() {
        return previousTenantId != null && !previousTenantId.equals(tenantId);
    }
}
