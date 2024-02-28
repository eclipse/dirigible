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

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The Class TenantContextInitFilter.
 */
public class TenantContextInitFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextInitFilter.class);

    private static final Cache<String, Optional<Tenant>> tenantCache = Caffeine.newBuilder()
                                                                               .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                               .maximumSize(100)
                                                                               .build();

    /** The tenant repository. */
    private final TenantRepository tenantRepository;

    /**
     * Instantiates a new tenant filter.
     *
     * @param tenantRepository the tenant repository
     */
    public TenantContextInitFilter(TenantRepository tenantRepository) {
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
        Optional<Tenant> currentTenant = determineCurrentTenant(request);
        if (currentTenant.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "There is no registered tenant for the current host " + request.getServerName());
            LOGGER.warn("Tried to reach unregistered tenant with host [{}]", request.getServerName());
            return;
        }
        TenantContext.setCurrentTenant(currentTenant.get());
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private Optional<Tenant> determineCurrentTenant(HttpServletRequest request) {
        String tenantSubdomain = getTenantSubdomain(request);
        if (null == tenantSubdomain) {
            return Optional.of(TenantImpl.getDefaultTenant());
        }

        return tenantCache.get(tenantSubdomain, (k) -> tenantRepository.findBySubdomain(tenantSubdomain)
                                                                       .map(TenantImpl::createFromEntity));
    }

    private String getTenantSubdomain(HttpServletRequest request) {
        var domain = request.getServerName();
        var dotIndex = domain.indexOf(".");

        String tenant = null;
        if (dotIndex != -1) {
            return domain.substring(0, dotIndex);
        }

        return tenant;
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
