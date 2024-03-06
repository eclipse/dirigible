package org.eclipse.dirigible.components.tenants.provisioning;

import java.util.Set;
import org.eclipse.dirigible.components.base.tenant.TenantPostProvisioningStep;
import org.eclipse.dirigible.components.base.tenant.TenantProvisioningStep;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.eclipse.dirigible.components.tenants.tenant.TenantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class TenantsProvisioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantsProvisioner.class);

    private final TenantRepository tenantRepository;
    private final Set<TenantProvisioningStep> provisioningSteps;
    private final Set<TenantPostProvisioningStep> postProvisioningSteps;
    private final TenantFactory tenantFactory;

    TenantsProvisioner(TenantRepository tenantRepository, Set<TenantProvisioningStep> provisioningSteps,
            Set<TenantPostProvisioningStep> postProvisioningSteps, TenantFactory tenantFactory) {
        this.tenantRepository = tenantRepository;
        this.provisioningSteps = provisioningSteps;
        this.postProvisioningSteps = postProvisioningSteps;
        this.tenantFactory = tenantFactory;
    }

    public void provision() {
        LOGGER.info("Starting tenants provisioning...");
        Set<Tenant> tenants = tenantRepository.findByStatus(TenantStatus.INITIAL);
        LOGGER.info("Tenants applicable for provisioning [{}]", tenants);

        tenants.forEach(this::provisionTenant);

        if (!tenants.isEmpty()) {
            LOGGER.info("Starting post provisioning process...");
            postProvisioningSteps.forEach(this::callPostProvisioningStep);
            LOGGER.info("Post provisioning process has completed.");
        }
        LOGGER.info("Tenants [{}] have been provisioned successfully.", tenants);
    }

    private void provisionTenant(Tenant tenant) {
        LOGGER.info("Starting provisioning process for tenant [{}]...", tenant);

        try {
            org.eclipse.dirigible.components.base.tenant.Tenant t = tenantFactory.createFromEntity(tenant);
            provisioningSteps.forEach(step -> step.execute(t));

            tenant.setStatus(TenantStatus.PROVISIONED);
            tenantRepository.save(tenant);

            LOGGER.info("Tenant [{}] has been provisioned successfully.", tenant);
        } catch (RuntimeException ex) {
            LOGGER.error("Failed to provision tenant [{}]", tenant, ex);
        }
    }

    private void callPostProvisioningStep(TenantPostProvisioningStep step) {
        try {
            step.execute();
        } catch (RuntimeException ex) {
            LOGGER.error("PostProvisioning step [{}] has failed.", step, ex);
        }
    }

}
