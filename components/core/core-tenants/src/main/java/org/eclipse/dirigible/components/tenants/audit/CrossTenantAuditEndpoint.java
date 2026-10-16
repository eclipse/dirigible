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

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.base.http.roles.ApplicationRoles;
import org.eclipse.dirigible.components.base.http.roles.Roles.RoleNames;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.security.RolesAllowed;

/**
 * Reads the cross-tenant audit trail.
 *
 * <p>
 * A tenant's administrators read what happened in their own tenant - who entered it, switched into
 * or away from it, was refused or lost it, and which cross-tenant runs visited it. Every user reads
 * what happened to themselves, in any tenant. Nobody reads another tenant's records: the tenant is
 * always the one the request runs in, never a parameter.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_SECURITY + "cross-tenant-audit")
class CrossTenantAuditEndpoint extends BaseEndpoint {

    /** How many records a read returns when it does not say. */
    private static final String DEFAULT_LIMIT = "100";

    /** The trail. */
    private final CrossTenantAuditService audit;

    /** The tenant context. */
    private final TenantContext tenantContext;

    /**
     * Instantiates the endpoint.
     *
     * @param audit the trail
     * @param tenantContext the tenant context
     */
    CrossTenantAuditEndpoint(CrossTenantAuditService audit, TenantContext tenantContext) {
        this.audit = audit;
        this.tenantContext = tenantContext;
    }

    /**
     * What happened in the current tenant, latest first.
     *
     * @param limit how many
     * @return the records
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({ApplicationRoles.OWNER, RoleNames.DEVELOPER, RoleNames.ADMINISTRATOR, RoleNames.OPERATOR})
    ResponseEntity<List<View>> ofCurrentTenant(@RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit) {
        Tenant tenant = tenantContext.isNotInitialized() ? null : tenantContext.getCurrentTenant();
        if (tenant == null || tenant.isDefault()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "The cross-tenant audit trail is read per tenant - select a tenant first; the default tenant has none");
        }
        return ResponseEntity.ok(views(audit.ofTenant(tenant.getId(), limit)));
    }

    /**
     * What happened to the caller, in any tenant, latest first.
     *
     * @param limit how many
     * @return the records
     */
    @GetMapping(path = "/mine", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<View>> ofCaller(@RequestParam(name = "limit", defaultValue = DEFAULT_LIMIT) int limit) {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(views(audit.ofPrincipal(authentication.getName(), limit)));
    }

    private static List<View> views(List<CrossTenantAuditRecord> records) {
        return records.stream()
                      .map(View::of)
                      .toList();
    }

    /**
     * One record, as the endpoint returns it.
     *
     * @param at when, ISO-8601
     * @param principal who
     * @param action what
     * @param tenantId in which tenant
     * @param fromTenantId the tenant a switch came from
     * @param operation the operation of a run
     * @param detail why a refusal or a run failed
     */
    record View(String at, String principal, CrossTenantAuditAction action, String tenantId, String fromTenantId, String operation,
            String detail) {

        static View of(CrossTenantAuditRecord record) {
            return new View(record.getAt()
                                  .toString(),
                    record.getPrincipal(), record.getAction(), record.getTenantId(), record.getFromTenantId(), record.getOperation(),
                    record.getDetail());
        }
    }
}
