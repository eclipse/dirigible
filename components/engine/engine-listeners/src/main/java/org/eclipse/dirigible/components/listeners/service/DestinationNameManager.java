package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DestinationNameManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationNameManager.class);

    private final TenantContext tenantContext;

    DestinationNameManager(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
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
}
