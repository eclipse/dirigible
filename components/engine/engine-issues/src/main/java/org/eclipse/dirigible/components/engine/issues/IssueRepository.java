/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** The issues store. */
@Repository("issueRepository")
public interface IssueRepository extends JpaRepository<Issue, Long> {

    /**
     * A reporter's issues in a tenant, latest change first.
     *
     * @param tenantId the tenant
     * @param reporter the reporter
     * @return the issues
     */
    List<Issue> findByTenantIdAndReporterOrderByUpdatedAtDescIdDesc(String tenantId, String reporter);

    /**
     * One issue, when it belongs to the reporter in the tenant.
     *
     * @param id the id
     * @param tenantId the tenant
     * @param reporter the reporter
     * @return the issue
     */
    Optional<Issue> findByIdAndTenantIdAndReporter(Long id, String tenantId, String reporter);

    /**
     * The issue a lifecycle process instance belongs to.
     *
     * @param processInstanceId the process instance
     * @return the issue
     */
    Optional<Issue> findByProcessInstanceId(String processInstanceId);

    /**
     * Every tenant's issues, filtered by whichever criteria are given, latest change first.
     *
     * @param tenantId the tenant, {@code null} for all
     * @param status the status, {@code null} for all
     * @param assignee the assignee, {@code null} for all
     * @param page how many
     * @return the issues
     */
    @Query("""
            SELECT i FROM Issue i
             WHERE (:tenantId IS NULL OR i.tenantId = :tenantId)
               AND (:status IS NULL OR i.status = :status)
               AND (:assignee IS NULL OR i.assignee = :assignee)
             ORDER BY i.updatedAt DESC, i.id DESC
            """)
    List<Issue> search(@Param("tenantId") String tenantId, @Param("status") IssueStatus status, @Param("assignee") String assignee,
            Pageable page);
}
