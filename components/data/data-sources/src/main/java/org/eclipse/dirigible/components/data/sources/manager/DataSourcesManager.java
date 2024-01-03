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
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.CustomDataSourcesService;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO: Auto-generated Javadoc
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

    /**
     * Instantiates a new data sources manager.
     *
     * @param datasourceService the datasource service
     * @param customDataSourcesService the custom data sources service
     * @param dataSourceInitializer the data source initializer
     */
    @Autowired
    public DataSourcesManager(DataSourceService datasourceService, CustomDataSourcesService customDataSourcesService,
            DataSourceInitializer dataSourceInitializer) {
        this.datasourceService = datasourceService;
        this.customDataSourcesService = customDataSourcesService;
        this.dataSourceInitializer = dataSourceInitializer;
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
        return getDataSource(getDefaultDataSourceName());
    }

    /**
     * Gets the system data source.
     *
     * @return the system data source
     */
    public javax.sql.DataSource getSystemDataSource() {
        return getDataSource(getSystemDataSourceName());
    }



    /**
     * Gets the data source definition.
     *
     * @param name the name
     * @return the data source definition
     */
    public DataSource getDataSourceDefinition(String name) {
        try {
            return datasourceService.findByName(name);
        } catch (Exception e) {
            if (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name)) {
                if (logger.isErrorEnabled()) {
                    logger.error("DataSource cannot be initialized, hence fail over database is started as a backup - " + name);
                }
                return new DataSource(name, name, name, "org.h2.Driver", "jdbc:h2:~/DefaultDBFailOver", "sa", "");
            }
            throw e;
        }
    }

    /**
     * Gets the default data source name.
     *
     * @return the default data source name
     */
    public String getDefaultDataSourceName() {
        return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT);
    }

    /**
     * Gets the system data source name.
     *
     * @return the system data source name
     */
    public String getSystemDataSourceName() {
        return Configuration.get(DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_NAME_SYSTEM,
                DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM);
    }

}
