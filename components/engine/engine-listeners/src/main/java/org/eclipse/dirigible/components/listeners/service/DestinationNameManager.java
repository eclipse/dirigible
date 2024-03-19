package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class DestinationNameManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationNameManager.class);
    private static final String TENANT_JOB_NAME_REGEX = "(.+)###.+";
    private static final Pattern TENANT_DESTINATION_NAME_PATTERN = Pattern.compile(TENANT_JOB_NAME_REGEX);

    private final TenantContext tenantContext;
    private final Tenant defaultTenant;

    DestinationNameManager(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    String toTenantName(String destinationName) {
        if (tenantContext.isNotInitialized()) {
            LOGGER.debug("Tenant context is NOT initialized. Will return destination name as it is. Destination name [{}]",
                    destinationName);
            return destinationName;
        }
        Tenant currentTenant = tenantContext.getCurrentTenant();
        return currentTenant.isDefault() ? destinationName : currentTenant.getId() + "###" + destinationName;
    }

    String extractTenantId(String tenantDestinationName) {
        Matcher matcher = TENANT_DESTINATION_NAME_PATTERN.matcher(tenantDestinationName);
        return matcher.matches() ? matcher.group(1) : defaultTenant.getId();
    }
}
