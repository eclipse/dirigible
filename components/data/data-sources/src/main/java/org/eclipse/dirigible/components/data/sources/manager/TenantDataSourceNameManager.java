package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.components.tenants.tenant.Tenant;

public class TenantDataSourceNameManager {

    public static String createName(Tenant tenant, String dataSourceName) {
        if (isTenantDataSourceName(tenant, dataSourceName)) {
            return dataSourceName;
        }
        return tenant.isDefault() ? dataSourceName : createPrefix(tenant) + dataSourceName;
    }

    private static boolean isTenantDataSourceName(Tenant tenant, String dataSourceName) {
        return dataSourceName.startsWith(createPrefix(tenant));
    }

    private static String createPrefix(Tenant tenant) {
        return tenant.getId() + "_";
    }

}
