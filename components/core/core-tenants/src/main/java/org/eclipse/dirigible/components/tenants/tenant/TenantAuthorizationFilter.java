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

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.dirigible.components.tenants.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class TenantAuthorizationFilter.
 */
public class TenantAuthorizationFilter extends OncePerRequestFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(TenantAuthorizationFilter.class.getName());

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
        var tenantId = TenantContext.getCurrentTenantId();
        var authentication = SecurityContextHolder.getContext()
                                                  .getAuthentication();
        var user = authentication == null ? null : (CustomUserDetails) authentication.getPrincipal();
        var userTenantId = user == null ? null : user.getTenantId();

        if (user == null || Objects.equals(tenantId, userTenantId)) {
            chain.doFilter(request, response);
        } else {
            LOGGER.warning("Attempted cross-tenant access. User ID '" + user.getUserId() + "', User's Tenant ID '" + user.getTenantId()
                    + "', Target Tenant ID '" + tenantId + "'.");
            response.setStatus(FORBIDDEN.value());
        }
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
}
