package org.eclipse.dirigible.components.tenants.tenant;

import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.springframework.stereotype.Component;

@Component
public class TenantFactory {

    public Tenant createFromEntity(org.eclipse.dirigible.components.tenants.domain.Tenant tenant) {
        return TenantImpl.createFromEntity(tenant);
    }
}
