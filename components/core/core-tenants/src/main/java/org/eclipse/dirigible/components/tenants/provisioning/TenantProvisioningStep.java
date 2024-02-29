package org.eclipse.dirigible.components.tenants.provisioning;

import org.eclipse.dirigible.components.tenants.tenant.Tenant;

public interface TenantProvisioningStep {

    void execute(Tenant tenant) throws TenantProvisioningException;

}
