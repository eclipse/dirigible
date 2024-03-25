package org.eclipse.dirigible.components.api.s3;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TenantPathResolved {
    private static final Logger logger = LoggerFactory.getLogger(TenantPathResolved.class);
    private static final String PATH_SEPARATOR = "/";
    private static final String ROOT_PATH = "/";
    private final TenantContext tenantContext;
    private final Tenant defaultTenant;

    TenantPathResolved(TenantContext tenantContext, @DefaultTenant Tenant defaultTenant) {
        this.tenantContext = tenantContext;
        this.defaultTenant = defaultTenant;
    }

    public String resolve(String path) {
        String tenantId = tenantContext.isInitialized() ? tenantContext.getCurrentTenant()
                                                                       .getId()
                : defaultTenant.getId();
        String prefix = tenantId + PATH_SEPARATOR;
        if (ROOT_PATH.equals(path)) {
            return prefix;
        }

        if (path.startsWith(prefix)) {
            return path;
        }

        String tenantPath = prefix + (path.startsWith(PATH_SEPARATOR) ? path.substring(1) : path);
        logger.debug("Path [{}] is resolved to [{}]", path, tenantPath);
        return tenantPath;
    }
}
