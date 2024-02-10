package org.eclipse.dirigible.components.tenants.tenant;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TenantFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = Logger.getLogger(TenantFilter.class.getName());

    private final TenantRepository tenantRepository;

    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

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
        chain.doFilter(request, response);
    }

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
