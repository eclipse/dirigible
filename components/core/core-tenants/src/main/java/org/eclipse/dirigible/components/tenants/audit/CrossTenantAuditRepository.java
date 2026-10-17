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

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The cross-tenant audit trail. Every read is scoped by a tenant or by a principal.
 */
@Repository("crossTenantAuditRepository")
interface CrossTenantAuditRepository extends JpaRepository<CrossTenantAuditRecord, Long> {

    /**
     * What happened in a tenant - including a switch away from it - latest first.
     *
     * @param tenantId the tenant
     * @param page how many
     * @return the records
     */
    @Query("SELECT r FROM CrossTenantAuditRecord r WHERE r.tenantId = :tenantId OR r.fromTenantId = :tenantId ORDER BY r.at DESC, r.id DESC")
    List<CrossTenantAuditRecord> findLatestOfTenant(@Param("tenantId") String tenantId, Pageable page);

    /**
     * What happened to a user, in any tenant, latest first.
     *
     * @param principal the principal name
     * @param page how many
     * @return the records
     */
    @Query("SELECT r FROM CrossTenantAuditRecord r WHERE r.principal = :principal ORDER BY r.at DESC, r.id DESC")
    List<CrossTenantAuditRecord> findLatestOfPrincipal(@Param("principal") String principal, Pageable page);
}
