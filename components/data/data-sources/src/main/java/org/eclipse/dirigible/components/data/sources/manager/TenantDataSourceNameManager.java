package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.components.tenants.tenant.Tenant;
import org.eclipse.dirigible.components.tenants.tenant.TenantContext;
import com.google.common.base.Objects;

public class TenantDataSourceNameManager {

    public static String getTenantDataSourceName(String dataSourceName) {
        if (isSystemDataSource(dataSourceName) || TenantContext.isNotInitialized()) {
            return dataSourceName;
        }
        Tenant tenant = TenantContext.getCurrentTenant();
        return createName(tenant, dataSourceName);
    }

    private static boolean isSystemDataSource(String dataSourceName) {
        return Objects.equal(dataSourceName, getSystemDataSourceName());
    }

    private static String getSystemDataSourceName() {
        return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
    }

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
