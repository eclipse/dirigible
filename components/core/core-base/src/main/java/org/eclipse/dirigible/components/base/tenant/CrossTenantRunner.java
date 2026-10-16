/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.tenant;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Runs work across the tenants the signed-in user holds a role in - the bulk operations of a user
 * who works for many tenants at once, such as an accounting practice closing the period of each of
 * its clients.
 *
 * <p>
 * Each tenant is visited with that tenant's data <em>and</em> that tenant's roles: the user's
 * global roles plus the roles their membership grants in the tenant being visited, never the roles
 * of the tenant the session has selected. The membership is read again when the run starts, so a
 * revoked tenant is not visited. A failure in one tenant is reported for that tenant and the run
 * carries on with the next one. Every visit is recorded in the cross-tenant audit trail.
 *
 * <p>
 * Implemented where the memberships are resolved - with the token-groups tenant resolution, by the
 * identity provider's groups. Under any other resolution there is no bean of this type.
 */
public interface CrossTenantRunner {

    /**
     * The tenants the current user holds a role in and can enter now.
     *
     * @return the tenants, never {@code null}
     * @throws IllegalStateException if no user is signed in
     */
    List<GrantedTenant> grantedTenants();

    /**
     * Runs the work once per tenant the current user can enter, in that tenant and with that tenant's
     * roles, one after the other on the calling thread.
     *
     * @param operation what the run does, for the audit trail - e.g. {@code period-close}
     * @param work the work, given the id of the tenant it runs in
     * @param <R> the result type
     * @return one result per tenant, in the order the memberships name them
     * @throws IllegalStateException if no user is signed in, or if called from within a run
     */
    <R> List<CrossTenantResult<R>> forEachGrantedTenant(String operation, Function<String, R> work);

    /**
     * A tenant the user holds a role in.
     *
     * @param id the tenant id
     * @param name the tenant name
     * @param roles the roles the membership grants in the tenant
     */
    record GrantedTenant(String id, String name, Set<String> roles) {
    }

    /**
     * What one tenant's visit of a run came to.
     *
     * @param tenantId the tenant
     * @param succeeded whether the work returned normally
     * @param result what the work returned; {@code null} on a failure
     * @param error why the work failed; {@code null} on a success
     * @param <R> the result type
     */
    record CrossTenantResult<R>(String tenantId, boolean succeeded, R result, String error) {
    }
}
