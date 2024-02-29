package org.eclipse.dirigible.components.tenants.provisioning;

import org.eclipse.dirigible.components.base.tenant.Tenant;

public interface TenantProvisioningStep {

    void execute(Tenant tenant) throws TenantProvisioningException;

}
