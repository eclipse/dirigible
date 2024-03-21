package org.eclipse.dirigible.components.engine.cms.s3.provider;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.CmsProviderInitializationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        String tenantFolder = getTenantFolder();
        try {
            return new CmsProviderS3(tenantFolder);
        } catch (IOException ex) {
            throw new CmsProviderInitializationException("Failed to create S3 CMS provider.", ex);
        }
    }

    private String getTenantFolder() {
        String tenantId = tenantContext.isNotInitialized() ? defaultTenant.getId()
                : tenantContext.getCurrentTenant()
                               .getId();
        return tenantId;
    }
}
