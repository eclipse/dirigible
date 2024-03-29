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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
@Component
public class TenantContextInitFilter extends OncePerRequestFilter {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantContextInitFilter.class);

    /** The Constant TENANT_SUBDOMAIN_PATTERN. */
    private static final Pattern TENANT_SUBDOMAIN_PATTERN = Pattern.compile(DirigibleConfig.TENANT_SUBDOMAIN_REGEX.getStringValue());

    /** The Constant tenantCache. */
    private static final Cache<String, Optional<Tenant>> tenantCache = Caffeine.newBuilder()
                                                                               .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                               .maximumSize(100)
                                                                               .build();

    /** The tenant service. */
    private final TenantService tenantService;

    /** The tenant context. */
    private final TenantContext tenantContext;

    /**
     * Instantiates a new tenant context init filter.
     *
     * @param tenantService the tenant service
     * @param tenantContext the tenant context
     */
    public TenantContextInitFilter(TenantService tenantService, TenantContext tenantContext) {
        this.tenantService = tenantService;
        this.tenantContext = tenantContext;
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

        try {
            tenantContext.executeWithPossibleException(currentTenant.get(), () -> {
                chain.doFilter(request, response);
                return null;
            });

        } catch (ServletException | IOException | RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    /**
     * Determine current tenant.
     *
     * @param request the request
     * @return the optional
     */
    private Optional<Tenant> determineCurrentTenant(HttpServletRequest request) {
        if (DirigibleConfig.SINGLE_TENANT_MODE_ENABLED.getBooleanValue()) {
            LOGGER.debug("The app is in single tenant mode. Will return the default tenant.");
            return Optional.of(TenantImpl.getDefaultTenant());
        }
        String host = request.getServerName();
        Matcher matcher = TENANT_SUBDOMAIN_PATTERN.matcher(host);
        if (matcher.find()) {
            String tenantSubdomain = matcher.group(1);
            LOGGER.debug("Host [{}] MATCHES tenant subdomain pattern [{}]. Tenant subdomain [{}].", host,
                    TENANT_SUBDOMAIN_PATTERN.pattern(), tenantSubdomain);

            return tenantCache.get(tenantSubdomain, (k) -> tenantService.findBySubdomain(tenantSubdomain)
                                                                        .map(TenantImpl::createFromEntity));
        }
        LOGGER.debug("Host [{}] does NOT match tenant subdomain pattern [{}]. Will be treated as default tenant host.", host,
                TENANT_SUBDOMAIN_PATTERN.pattern());
        return Optional.of(TenantImpl.getDefaultTenant());
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
