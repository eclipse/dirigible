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
import org.eclipse.dirigible.components.tenants.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantAuthorizationFilter.class);

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
        Tenant tenant = TenantContext.getCurrentTenant();
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        CustomUserDetails userDetails = authentication == null ? null : (CustomUserDetails) authentication.getPrincipal();
        var userTenantId = userDetails == null ? null : userDetails.getTenantId();

        if (userDetails == null || Objects.equals(tenant.getId(), userTenantId)) {
            chain.doFilter(request, response);
        } else {
            LOGGER.warn("Attempted cross-tenant access. User ID [{}], User's Tenant ID [{}], Target tenant [{}]", userDetails.getUserId(),
                    userDetails.getTenantId(), tenant);
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
