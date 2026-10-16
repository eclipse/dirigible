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
 * The tenant a session had selected is no longer granted to its user - the membership was revoked
 * while the session was open - and the selection was dropped. Published for the cross-tenant audit
 * trail.
 *
 * @param tenantId the tenant whose selection was dropped
 * @param principal the principal name
 * @param at when
 */
public record TenantSelectionRevokedEvent(String tenantId, String principal, Instant at) {
}
