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
 * One tenant's visit of a cross-tenant run - see
 * {@link org.eclipse.dirigible.components.base.tenant.CrossTenantRunner}. Published for the
 * cross-tenant audit trail.
 *
 * @param tenantId the tenant visited
 * @param principal the principal name of the user the run is for
 * @param operation what the run does
 * @param succeeded whether the work in this tenant returned normally
 * @param error why it failed; null on a success
 * @param at when the visit ended
 */
public record CrossTenantRunEvent(String tenantId, String principal, String operation, boolean succeeded, String error, Instant at) {
}
