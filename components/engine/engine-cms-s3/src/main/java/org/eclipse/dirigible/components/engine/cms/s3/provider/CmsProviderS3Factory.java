package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.CmsProviderInitializationException;
import org.eclipse.dirigible.components.engine.cms.s3.repository.CmisS3Session;
import org.springframework.stereotype.Component;

@Component("cms-provider-s3")
class CmsProviderS3Factory implements CmsProviderFactory {

    private final TenantContext tenantContext;
    private final Tenant defaultTenant;

    CmsProviderS3Factory(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    @Override
    public CmsProvider create() throws CmsProviderInitializationException {
        CmisS3Session session = new CmisS3Session();
        return new CmsProviderS3(session);
    }

    private String getTenantFolder() {
        String tenantId = tenantContext.isNotInitialized() ? defaultTenant.getId()
                : tenantContext.getCurrentTenant()
                               .getId();
        return tenantId;
    }
}
