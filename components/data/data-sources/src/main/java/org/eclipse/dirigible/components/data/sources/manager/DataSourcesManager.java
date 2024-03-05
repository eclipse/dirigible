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

import org.eclipse.dirigible.components.data.sources.config.DefaultDataSourceName;
import org.eclipse.dirigible.components.data.sources.config.SystemDataSourceName;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.CustomDataSourcesService;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class DataSourcesManager.
 */
@Component
public class DataSourcesManager implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DataSourcesManager.class);

    private static DataSourcesManager INSTANCE;

    private final DataSourceService datasourceService;
    private final CustomDataSourcesService customDataSourcesService;
    private final DataSourceInitializer dataSourceInitializer;
    private final TenantDataSourceNameManager tenantDataSourceNameManager;
    private final String defaultDataSourceName;
    private final String systemDataSourceName;

    /**
     * Instantiates a new data sources manager.
     *
     * @param datasourceService the datasource service
     * @param customDataSourcesService the custom data sources service
     * @param dataSourceInitializer the data source initializer
     */
    @Autowired
    public DataSourcesManager(DataSourceService datasourceService, CustomDataSourcesService customDataSourcesService,
            DataSourceInitializer dataSourceInitializer, TenantDataSourceNameManager tenantDataSourceNameManager,
            @DefaultDataSourceName String defaultDataSourceName, @SystemDataSourceName String systemDataSourceName) {
        this.datasourceService = datasourceService;
        this.customDataSourcesService = customDataSourcesService;
        this.dataSourceInitializer = dataSourceInitializer;
        this.tenantDataSourceNameManager = tenantDataSourceNameManager;
        this.defaultDataSourceName = defaultDataSourceName;
        this.systemDataSourceName = systemDataSourceName;
        this.customDataSourcesService.initialize();
    }

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        INSTANCE = this;
    }

    /**
     * Gets the.
     *
     * @return the data sources manager
     */
    public static DataSourcesManager get() {
        return INSTANCE;
    }

    /**
     * Gets the data source.
     *
     * @param name the name
     * @return the data source
     */
    public javax.sql.DataSource getDataSource(String name) {
        return dataSourceInitializer.isInitialized(name) ? dataSourceInitializer.getInitializedDataSource(name)
                : dataSourceInitializer.initialize(getDataSourceDefinition(name));
    }

    /**
     * Gets the default data source.
     *
     * @return the default data source
     */
    public javax.sql.DataSource getDefaultDataSource() {
        return getDataSource(defaultDataSourceName);
    }

    /**
     * Gets the system data source.
     *
     * @return the system data source
     */
    public javax.sql.DataSource getSystemDataSource() {
        return getDataSource(systemDataSourceName);
    }

    /**
     * Gets the data source definition.
     *
     * @param name the name
     * @return the data source definition
     */
    public DataSource getDataSourceDefinition(String name) {
        String dataSourceName = tenantDataSourceNameManager.getTenantDataSourceName(name);
        try {
            return datasourceService.findByName(dataSourceName);
        } catch (Exception e) {
            if (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(dataSourceName)) {
                if (logger.isErrorEnabled()) {
                    logger.error("DataSource cannot be initialized, hence fail over database is started as a backup - " + dataSourceName);
                }
                return new DataSource(dataSourceName, dataSourceName, dataSourceName, "org.h2.Driver", "jdbc:h2:~/DefaultDBFailOver", "sa",
                        "");
            }
            throw e;
        }
    }

}
