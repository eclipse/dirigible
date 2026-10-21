/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.security;

import java.util.List;
import java.util.function.Function;

import org.eclipse.dirigible.components.api.security.TenantsFacade;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.CrossTenantResult;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.GrantedTenant;

/**
 * The tenants the current user holds a role in, and runs across them. Built for a user who works
 * for many tenants at once - an accounting practice keeping the books of its clients, each one a
 * tenant - and who has to run the same operation (a period close, a statutory file) in each of
 * them.
 * <p>
 * Each tenant is visited with that tenant's data and that tenant's roles, never the roles of the
 * tenant the session has selected; a failure in one tenant is reported for it and the run carries
 * on with the next, and every visit is recorded in the cross-tenant audit trail.
 * <p>
 * Available with the token-groups tenant resolution only
 * ({@code DIRIGIBLE_TENANT_RESOLUTION_STRATEGY=TOKEN_GROUPS}); check {@link #isAvailable()} first,
 * the other methods refuse elsewhere.
 *
 * <pre>{@code
 * List<CrossTenantResult<String>> results = Tenants.forEachGrantedTenant("period-close", tenantId -> closePeriod("2026-09"));
 * }</pre>
 */
public final class Tenants {

    private Tenants() {}

    /**
     * Whether runs across tenants are available on this instance.
     *
     * @return true with the token-groups tenant resolution
     */
    public static boolean isAvailable() {
        return TenantsFacade.isAvailable();
    }

    /**
     * The tenants the current user holds a role in and can enter now.
     *
     * @return the tenants, with the roles held in each
     * @throws IllegalStateException if runs across tenants are not available, or no user is signed in
     */
    public static List<GrantedTenant> getGrantedTenants() {
        return TenantsFacade.runner()
                            .grantedTenants();
    }

    /**
     * Runs the work once per tenant the current user can enter, in that tenant's data and with that
     * tenant's roles, one after the other on the calling thread.
     *
     * @param operation what the run does, recorded in the audit trail - e.g. {@code period-close}
     * @param work the work, given the id of the tenant it runs in
     * @param <R> the result type
     * @return one result per tenant
     * @throws IllegalStateException if runs across tenants are not available, no user is signed in, or
     *         the call is made from within a run
     */
    public static <R> List<CrossTenantResult<R>> forEachGrantedTenant(String operation, Function<String, R> work) {
        return TenantsFacade.runner()
                            .forEachGrantedTenant(operation, work);
    }
}
