/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tenants.tenant;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class TenantFilter.
 */
public class TenantFilter extends OncePerRequestFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(TenantFilter.class.getName());

    /** The tenant repository. */
    private final TenantRepository tenantRepository;

    /**
     * Instantiates a new tenant filter.
     *
     * @param tenantRepository the tenant repository
     */
    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * Do filter internal.
     *
     * @param request the request
     * @param response the response
     * @param chain the chain
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var tenant = getTenant(request);
        var tenantId = tenantRepository.findBySlug(tenant)
                                       .map(org.eclipse.dirigible.components.tenants.domain.Tenant::getId)
                                       .orElse(null);
        if (tenant != null && tenantId == null) {
            // Attempted access to non-existing tenant
            response.setStatus(NOT_FOUND.value());
            LOGGER.info("Trying tenant: " + tenant + " (domain " + request.getServerName() + ") which does not exist");
            return;
        }
        LOGGER.info("Setting tenant: " + tenant + " (domain " + request.getServerName() + ")");
        LOGGER.info("Setting tenant ID: " + tenantId);
        TenantContext.setCurrentTenant(tenant);
        TenantContext.setCurrentTenantId(tenantId);

        List<String> tenants = tenantRepository.findAll()
                                               .stream()
                                               .map(Tenant::getSlug)
                                               .toList();

        TenantContext.setCurrentTenants(tenants);

        chain.doFilter(request, response);
    }

    /**
     * Should not filter.
     *
     * @param request the request
     * @return true, if successful
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI()
                      .startsWith("/webjars/")
                || request.getRequestURI()
                          .startsWith("/css/")
                || request.getRequestURI()
                          .startsWith("/js/")
                || request.getRequestURI()
                          .endsWith(".ico");
    }

    /**
     * Gets the tenant.
     *
     * @param request the request
     * @return the tenant
     */
    private String getTenant(HttpServletRequest request) {
        var domain = request.getServerName();
        var dotIndex = domain.indexOf(".");

        String tenant = null;
        if (dotIndex != -1) {
            tenant = domain.substring(0, dotIndex);
        }

        return tenant;
    }
}
