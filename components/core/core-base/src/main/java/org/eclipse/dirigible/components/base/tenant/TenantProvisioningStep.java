package org.eclipse.dirigible.components.base.tenant;

public interface TenantProvisioningStep {

    /**
     * This step will be executed when there is a tenant in INITIAL status.<br>
     * Implement what is required to provision the passed tenant.
     *
     * @param tenant
     * @throws TenantProvisioningException in case of error.
     */
    void execute(Tenant tenant) throws TenantProvisioningException;

}
