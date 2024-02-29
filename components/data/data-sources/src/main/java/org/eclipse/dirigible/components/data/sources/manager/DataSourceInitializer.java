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

import static java.text.MessageFormat.format;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.domain.DataSourceProperty;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class DataSourceInitializer.
 */
@Component
public class DataSourceInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializer.class);

    private static final Map<String, javax.sql.DataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());

    private final ApplicationContext applicationContext;

    private final List<DataSourceInitializerContributor> contributors;

    private final TenantDataSourceNameManager tenantDataSourceNameManager;

    /**
     * Instantiates a new data source initializer.
     *
     * @param applicationContext the application context
     */
    DataSourceInitializer(ApplicationContext applicationContext, List<DataSourceInitializerContributor> contributors,
            TenantDataSourceNameManager tenantDataSourceNameManager) {
        this.applicationContext = applicationContext;
        this.contributors = contributors;
        this.tenantDataSourceNameManager = tenantDataSourceNameManager;
    }

    /**
     * Initialize.
     *
     * @param dataSource the data source
     * @return the javax.sql. data source
     */
    public javax.sql.DataSource initialize(DataSource dataSource) {
        if (isInitialized(dataSource.getName())) {
            return getInitializedDataSource(dataSource.getName());
        }

        return initDataSource(dataSource.getName(), dataSource.getDriver(), dataSource.getUrl(), dataSource.getUsername(),
                dataSource.getPassword(), dataSource.getSchema(), dataSource.getProperties());
    }

    @SuppressWarnings("resource")
    private ManagedDataSource initDataSource(String name, String driver, String url, String username, String password, String schema,
            List<DataSourceProperty> additionalProperties) {
        logger.info("Initializing a datasource with name: [{}]", name);
        if ("org.h2.Driver".equals(driver)) {
            try {
                prepareRootFolder(name);
            } catch (IOException ex) {
                logger.error("Invalid configuration for the datasource: [{}]", name, ex);
            }
        }
        Properties properties = new Properties();
        Properties contributed = new Properties();

        properties.put("driverClassName", driver);
        properties.put("jdbcUrl", url);
        properties.put("dataSource.url", url);
        properties.put("dataSource.user", username);
        properties.put("dataSource.password", password);
        properties.put("dataSource.logWriter", new PrintWriter(System.out));

        contributors.forEach(contributor -> contributor.contribute(name, contributed));

        Map<String, String> hikariProperties = getHikariProperties(name);
        hikariProperties.forEach(properties::setProperty);

        HikariConfig config;

        if (name.startsWith("SNOWFLAKE")) {
            config = new HikariConfig(contributed);
            config.setDriverClassName(driver);
            config.setJdbcUrl(contributed.get("jdbcUrl")
                                         .toString());
        } else {
            properties.putAll(contributed);
            config = new HikariConfig(properties);
            additionalProperties.forEach(dsp -> config.addDataSourceProperty(dsp.getName(), dsp.getValue()));
        }

        config.setSchema(schema);
        config.setPoolName(name);
        config.setAutoCommit(true);
        HikariDataSource hds = new HikariDataSource(config);

        ManagedDataSource managedDataSource = new ManagedDataSource(hds);
        registerDataSourceBean(name, managedDataSource);

        DATASOURCES.put(name, managedDataSource);

        return managedDataSource;
    }

    /**
     * Checks if it is initialized.
     *
     * @param dataSourceName the data source name
     * @return true, if is initialized
     */
    public boolean isInitialized(String dataSourceName) {
        String name = tenantDataSourceNameManager.getTenantDataSourceName(dataSourceName);
        return DATASOURCES.containsKey(name);

    }

    /**
     * Gets the initialized data source.
     *
     * @param dataSourceName the data source name
     * @return the initialized data source
     */
    public javax.sql.DataSource getInitializedDataSource(String dataSourceName) {
        String name = tenantDataSourceNameManager.getTenantDataSourceName(dataSourceName);
        return DATASOURCES.get(name);
    }

    /**
     * Removes the initialized data source.
     *
     * @param dataSourceName the data source name
     */
    public void removeInitializedDataSource(String dataSourceName) {
        String name = tenantDataSourceNameManager.getTenantDataSourceName(dataSourceName);
        DATASOURCES.remove(name);
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

    private String prepareRootFolder(String name) throws IOException {
        String rootFolder = (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_DEFAULT.equals(name))
                ? DatabaseParameters.DIRIGIBLE_DATABASE_H2_ROOT_FOLDER_DEFAULT
                : DatabaseParameters.DIRIGIBLE_DATABASE_H2_ROOT_FOLDER + name;
        String h2Root = Configuration.get(rootFolder, name);
        File rootFile = new File(h2Root);
        File parentFile = rootFile.getCanonicalFile()
                                  .getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException(format("Creation of the root folder [{0}] of the embedded H2 database failed.", h2Root));
            }
        }
        return h2Root;
    }

    private void registerDataSourceBean(String name, ManagedDataSource dataSource) {
        if (DatabaseParameters.DIRIGIBLE_DATABASE_DATASOURCE_SYSTEM.equals(name)) {
            return; // bean already set by org.eclipse.dirigible.components.database.DataSourceSystemConfig
        }
        GenericApplicationContext genericAppContext = (GenericApplicationContext) applicationContext;
        ConfigurableListableBeanFactory beanFactory = genericAppContext.getBeanFactory();
        beanFactory.registerSingleton(name, dataSource);
    }

    private Map<String, String> getHikariProperties(String databaseName) {
        Map<String, String> properties = new HashMap<>();
        String hikariDelimiter = "_HIKARI_";
        String databaseKeyPrefix = databaseName + hikariDelimiter;
        int hikariDelimiterLength = hikariDelimiter.length();
        Arrays.stream(Configuration.getKeys())
              .filter(key -> key.startsWith(databaseKeyPrefix))//
              .map(key -> key.substring(key.lastIndexOf(hikariDelimiter) + hikariDelimiterLength))
              .forEach(key -> properties.put(key, Configuration.get(databaseKeyPrefix + key)));

        return properties;
    }

}
