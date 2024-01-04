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
import java.util.Map;
import java.util.Properties;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
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

    /**
     * Instantiates a new data source initializer.
     *
     * @param applicationContext the application context
     */
    DataSourceInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Initialize.
     *
     * @param dataSource the data source
     * @return the javax.sql. data source
     */
    public javax.sql.DataSource initialize(DataSource dataSource) {
        String name = dataSource.getName();

        if (isInitialized(name)) {
            return getInitializedDataSource(name);
        }

        logger.info("Initializing a datasource with name: [{}]", name);
        if ("org.h2.Driver".equals(dataSource.getDriver())) {
            try {
                prepareRootFolder(name);
            } catch (IOException ex) {
                logger.error("Invalid configuration for the datasource: [{}]", name, ex);
            }
        }
        Properties properties = new Properties();
        properties.put("driverClassName", dataSource.getDriver());
        properties.put("jdbcUrl", dataSource.getUrl());
        properties.put("dataSource.url", dataSource.getUrl());
        properties.put("dataSource.user", dataSource.getUsername());
        properties.put("dataSource.password", dataSource.getPassword());
        properties.put("dataSource.logWriter", new PrintWriter(System.out));

        Map<String, String> hikariProperties = getHikariProperties(name);
        hikariProperties.forEach(properties::setProperty);

        HikariConfig config = new HikariConfig(properties);
        config.setPoolName(name);
        config.setAutoCommit(true);
        dataSource.getProperties()
                  .forEach(dsp -> config.addDataSourceProperty(dsp.getName(), dsp.getValue()));
        HikariDataSource hds = new HikariDataSource(config);

        ManagedDataSource managedDataSource = new ManagedDataSource(hds);
        registerDataSourceBean(name, managedDataSource);

        DATASOURCES.put(name, managedDataSource);

        return managedDataSource;
    }

    /**
     * Checks if is initialized.
     *
     * @param dataSourceName the data source name
     * @return true, if is initialized
     */
    public boolean isInitialized(String dataSourceName) {
        return DATASOURCES.containsKey(dataSourceName);
    }

    /**
     * Gets the initialized data source.
     *
     * @param dataSourceName the data source name
     * @return the initialized data source
     */
    public javax.sql.DataSource getInitializedDataSource(String dataSourceName) {
        return DATASOURCES.get(dataSourceName);
    }

    /**
     * Removes the initialized data source.
     *
     * @param dataSourceName the data source name
     */
    public void removeInitializedDataSource(String dataSourceName) {
        DATASOURCES.remove(dataSourceName);
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
