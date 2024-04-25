/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.endpoint;

import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class TenantEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "tenants")
public class TenantEndpoint {


    /** The tenant service. */
    private final TenantService tenantService;

    /**
     * Instantiates a new tenants endpoint.
     *
     * @param tenantService the tenant service
     */
    @Autowired
    public TenantEndpoint(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    @GetMapping
    public ResponseEntity<List<Tenant>> getAll() {
        return ResponseEntity.ok(tenantService.getAll());
    }

}
