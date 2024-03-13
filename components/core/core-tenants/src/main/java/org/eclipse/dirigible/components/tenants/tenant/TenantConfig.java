package org.eclipse.dirigible.components.tenants.tenant;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TenantConfig {

    @Bean
    @DefaultTenant
    Tenant getDefaultTenant() {
        return TenantImpl.getDefaultTenant();
    }
}
