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

/**
 * Removes one kind of state a tenant left in this instance, when the tenant is deprovisioned.
 *
 * <p>
 * Deprovisioning (the tenant provisioning API's {@code DELETE tenants/{id}}) runs every participant
 * bean in {@link #order() order}, after the tenant has been moved to the persisted
 * {@code DEPROVISIONING} status and before its platform users and its row are deleted. Each module
 * that keeps something per tenant contributes its own participant, so the module that owns the
 * deprovisioning depends on none of them: what runs for the tenant first (jobs, listeners,
 * processes), then its data source with the connection pool, then the rows the system database
 * holds for it.
 *
 * <p>
 * A participant must be <b>idempotent</b>: a run interrupted half way is repeated from the start,
 * so finding nothing to remove is success. It must not touch the tenant's data schema - the schema,
 * and the database user that owns it, belong to whoever provisioned them, and are dropped by that
 * caller once the call has returned. A participant that throws stops the run; the tenant stays
 * {@code DEPROVISIONING} and a repeated call resumes.
 */
public interface TenantTeardownParticipant {

    /**
     * Where this participant runs: lower first. What runs for the tenant (100) goes before its data
     * source (200), and the data source before the rows that describe the tenant (300 and up).
     *
     * @return the order
     */
    int order();

    /**
     * Removes this participant's state of the tenant; idempotent.
     *
     * @param tenantId the id of the tenant being deprovisioned
     */
    void teardown(String tenantId);
}
