/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.audit;

/**
 * What a cross-tenant audit record says happened.
 */
public enum CrossTenantAuditAction {

    /** The first tenant a session selected. */
    ENTERED,

    /** A session moved from one tenant to another without signing in again. */
    SWITCHED,

    /** Entering a tenant the user's memberships do not grant was refused. */
    REFUSED,

    /** The tenant a session had selected was revoked while it was open, and the selection dropped. */
    REVOKED,

    /** One tenant's visit of a cross-tenant run returned normally. */
    RUN_SUCCEEDED,

    /** One tenant's visit of a cross-tenant run failed. */
    RUN_FAILED
}
