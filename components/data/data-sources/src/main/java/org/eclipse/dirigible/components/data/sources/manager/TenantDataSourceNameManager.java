/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.sources.manager;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.springframework.stereotype.Component;
import com.google.common.base.Objects;

@Component
public class TenantDataSourceNameManager {

    private final TenantContext tenantContext;

    TenantDataSourceNameManager(TenantContext tenantContext) {
        this.tenantContext = tenantContext;

    }

    public String getTenantDataSourceName(String dataSourceName) {
        if (isSystemDataSource(dataSourceName) || tenantContext.isNotInitialized()) {
            return dataSourceName;
        }
        Tenant tenant = tenantContext.getCurrentTenant();
        return createName(tenant, dataSourceName);
    }

    private boolean isSystemDataSource(String dataSourceName) {
        return Objects.equal(dataSourceName, getSystemDataSourceName());
    }

    private String getSystemDataSourceName() {
        return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
    }

    public String createName(Tenant tenant, String dataSourceName) {
        if (isTenantDataSourceName(tenant, dataSourceName)) {
            return dataSourceName;
        }
        return tenant.isDefault() ? dataSourceName : createPrefix(tenant) + dataSourceName;
    }

    private boolean isTenantDataSourceName(Tenant tenant, String dataSourceName) {
        return dataSourceName.startsWith(createPrefix(tenant));
    }

    private String createPrefix(Tenant tenant) {
        return tenant.getId() + "_";
    }

}
