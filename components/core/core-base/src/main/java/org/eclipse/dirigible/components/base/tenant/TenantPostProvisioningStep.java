package org.eclipse.dirigible.components.base.tenant;

/**
 * A step which will be executed once a tenant provisioning process is completed (all
 * {@link TenantProvisioningStep} have completed).
 */
public interface TenantPostProvisioningStep {

    /**
     * Will be called once provisioning of all tenants is completed.<br>
     * If there are no tenants in INITIAL status, post provisioning step will not be called.
     *
     * @throws TenantProvisioningException
     */
    void execute() throws TenantProvisioningException;

}
