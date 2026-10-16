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
 * A signed-in user asked to enter a tenant their memberships do not grant - on the tenant picker,
 * or with a bearer request naming the tenant. Published for the cross-tenant audit trail: a refusal
 * is how a revoked membership, a stale bookmark or a probe for another tenant shows up.
 *
 * @param tenantId the tenant asked for
 * @param principal the principal name
 * @param reason why it was refused
 * @param at when
 */
public record TenantAccessRefusedEvent(String tenantId, String principal, String reason, Instant at) {
}
