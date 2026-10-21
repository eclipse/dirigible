/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.security;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner;
import org.eclipse.dirigible.components.base.tenant.CrossTenantRunner.CrossTenantResult;

/**
 * The tenants the current user holds a role in, and runs across them - the scripting side of
 * {@link CrossTenantRunner}. Built for a user who works for many tenants at once, such as an
 * accounting practice running the period close of each of its clients: the work runs once per
 * tenant, with that tenant's data and that tenant's roles, and every visit is audited.
 *
 * <p>
 * Available with the token-groups tenant resolution only, where a user's memberships come from
 * their identity provider groups; elsewhere {@link #isAvailable()} is false and the other methods
 * refuse.
 */
public final class TenantsFacade {

    private TenantsFacade() {}

    /**
     * Whether runs across tenants are available on this instance.
     *
     * @return true with the token-groups tenant resolution
     */
    public static boolean isAvailable() {
        return !BeanProvider.getBeans(CrossTenantRunner.class)
                            .isEmpty();
    }

    /**
     * The tenants the current user holds a role in and can enter now.
     *
     * @return a JSON array of {@code {id, name, roles}}
     */
    public static String getGrantedTenants() {
        return GsonHelper.toJson(runner().grantedTenants());
    }

    /**
     * Runs the work once per tenant the current user can enter, in that tenant and with that tenant's
     * roles.
     *
     * @param operation what the run does, for the audit trail
     * @param work the work, given the id of the tenant it runs in
     * @return one result per tenant
     */
    public static List<CrossTenantResult<Object>> forEachGrantedTenant(String operation, Function<String, Object> work) {
        return runner().forEachGrantedTenant(operation, work);
    }

    /**
     * The runner behind this facade, for typed Java callers such as the Java SDK's {@code Tenants}.
     *
     * @return the cross-tenant runner
     * @throws IllegalStateException if runs across tenants are not available on this instance
     */
    public static CrossTenantRunner runner() {
        Collection<CrossTenantRunner> runners = BeanProvider.getBeans(CrossTenantRunner.class);
        if (runners.isEmpty()) {
            throw new IllegalStateException("Runs across tenants need the token-groups tenant resolution, where a user holds roles in"
                    + " several tenants - this instance resolves the tenant otherwise");
        }
        return runners.iterator()
                      .next();
    }
}
