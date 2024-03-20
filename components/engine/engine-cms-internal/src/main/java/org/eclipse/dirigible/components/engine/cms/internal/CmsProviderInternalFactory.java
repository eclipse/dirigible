package org.eclipse.dirigible.components.engine.cms.internal;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.engine.cms.CmsProvider;
import org.eclipse.dirigible.components.engine.cms.CmsProviderFactory;
import org.eclipse.dirigible.components.engine.cms.internal.provider.CmsProviderInternal;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component("cms-provider-internal")
class CmsProviderInternalFactory implements CmsProviderFactory {

    private static final String DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER = "DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER";

    private final TenantContext tenantContext;
    private final Tenant defaultTenant;

    CmsProviderInternalFactory(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    @Override
    public CmsProvider create() {
        String rootFolder = Configuration.get(DIRIGIBLE_CMS_INTERNAL_ROOT_FOLDER, "target/dirigible") + getTenantFolder();
        Path path = Paths.get(rootFolder);
        boolean absolutePath = path.isAbsolute();

        return new CmsProviderInternal(rootFolder, absolutePath);
    }

    private String getTenantFolder() {
        String tenantId = tenantContext.isNotInitialized() ? defaultTenant.getId()
                : tenantContext.getCurrentTenant()
                               .getId();
        return File.separator + tenantId;
    }
}
