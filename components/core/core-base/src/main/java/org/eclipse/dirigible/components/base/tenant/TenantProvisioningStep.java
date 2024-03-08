package org.eclipse.dirigible.components.base.tenant;

public interface TenantProvisioningStep {

    void execute(Tenant tenant) throws TenantProvisioningException;

}
