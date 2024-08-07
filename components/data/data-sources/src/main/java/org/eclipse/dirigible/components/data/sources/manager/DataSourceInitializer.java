/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.sources.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.config.DefaultDataSourceName;
import org.eclipse.dirigible.components.data.sources.config.SystemDataSourceName;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.domain.DataSourceProperty;
import org.eclipse.dirigible.components.database.DatabaseParameters;
import org.eclipse.dirigible.components.database.DatabaseSystem;
import org.eclipse.dirigible.components.database.DirigibleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;

/**
 * The Class DataSourceInitializer.
 */
@Component
public class DataSourceInitializer {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializer.class);
    /** The Constant DATASOURCES. */
    private static final Map<String, DirigibleDataSource> DATASOURCES = Collections.synchronizedMap(new HashMap<>());
    /** The application context. */
    private final ApplicationContext applicationContext;

    /** The contributors. */
    private final List<DatabaseConfigurator> databaseConfigurators;

    /** The tenant data source name manager. */
    private final TenantDataSourceNameManager tenantDataSourceNameManager;
    private final String systemDataSourceName;
    private final String defaultDataSourceName;

    DataSourceInitializer(ApplicationContext applicationContext, List<DatabaseConfigurator> databaseConfigurators,
            TenantDataSourceNameManager tenantDataSourceNameManager, @SystemDataSourceName String systemDataSourceName,
            @DefaultDataSourceName String defaultDataSourceName) {
        this.applicationContext = applicationContext;
        this.databaseConfigurators = databaseConfigurators;
        this.tenantDataSourceNameManager = tenantDataSourceNameManager;
        this.systemDataSourceName = systemDataSourceName;
        this.defaultDataSourceName = defaultDataSourceName;
    }

    /**
     * Initialize.
     *
     * @param dataSource the data source
     * @return the javax.sql. data source
     */
    public DirigibleDataSource initialize(DataSource dataSource) {
        if (isInitialized(dataSource.getName())) {
            return getInitializedDataSource(dataSource.getName());
        }

        return initDataSource(dataSource);
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
    public DirigibleDataSource getInitializedDataSource(String dataSourceName) {
        String name = tenantDataSourceNameManager.getTenantDataSourceName(dataSourceName);
        return DATASOURCES.get(name);
    }

    /**
     * Inits the data source.
     *
     * @param dataSource the data source
     * @return the managed data source
     */
    @SuppressWarnings("resource")
    private DirigibleDataSource initDataSource(DataSource dataSource) {

        DatabaseSystem dbType = DatabaseSystemDeterminer.determine(dataSource);

        String name = dataSource.getName();
        String driver = dataSource.getDriver();
        String url = dataSource.getUrl();
        String username = dataSource.getUsername();
        String password = dataSource.getPassword();
        String schema = dataSource.getSchema();

        logger.info("Initializing a datasource with name: [{}]", name);
        if (dbType.isH2()) {
            try {
                prepareRootFolder(name);
            } catch (IOException ex) {
                logger.error("Invalid configuration for the datasource: [{}]", name, ex);
            }
        }
        Properties hikariProperties = getHikariProperties(name);
        HikariConfig config = new HikariConfig(hikariProperties);

        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("logWriter", new PrintWriter(System.out));

        config.setSchema(schema);
        config.setPoolName(name);
        config.setAutoCommit(true);
        config.setMaximumPoolSize(20);

        config.setMinimumIdle(10);
        config.setIdleTimeout(TimeUnit.MINUTES.toMillis(3)); // free connections when idle, potentially remove leaked connections
        config.setMaxLifetime(TimeUnit.MINUTES.toMillis(15)); // recreate connections after specified time
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(15));
        config.setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(1)); // log message for possible leaked connection

        applyDbSpecificConfigurations(dataSource, dbType, config);

        List<DataSourceProperty> additionalProperties = dataSource.getProperties();
        addAdditionalProperties(additionalProperties, config);

        HikariDataSource hikariDataSource = new HikariDataSource(config);
        DirigibleDataSource managedDataSource = new ManagedDataSource(hikariDataSource, dbType);

        registerDataSourceBean(name, managedDataSource);

        DATASOURCES.put(name, managedDataSource);

        Runtime.getRuntime()
               .addShutdownHook(new Thread(hikariDataSource::close));

        return managedDataSource;
    }

    private void addPropertyIfAvailable(String propertyKey, Object propertyValue, Properties properties) {
        if (null != propertyValue) {
            properties.put(propertyKey, propertyValue);
        }
    }

    /**
     * Gets the hikari properties.
     *
     * @param databaseName the database name
     * @return the hikari properties
     */
    private Properties getHikariProperties(String databaseName) {
        Properties properties = new Properties();
        String hikariDelimiter = "_HIKARI_";
        String databaseKeyPrefix = databaseName + hikariDelimiter;
        int hikariDelimiterLength = hikariDelimiter.length();
        Arrays.stream(Configuration.getKeys())
              .filter(key -> key.startsWith(databaseKeyPrefix))//
              .map(key -> key.substring(key.lastIndexOf(hikariDelimiter) + hikariDelimiterLength))
              .forEach(key -> properties.put(key, Configuration.get(databaseKeyPrefix + key)));

        return properties;
    }

    private void addAdditionalProperties(List<DataSourceProperty> additionalProperties, HikariConfig config) {
        additionalProperties.forEach(additionalProp -> config.addDataSourceProperty(additionalProp.getName(), additionalProp.getValue()));
    }

    private void applyDbSpecificConfigurations(DataSource dataSource, DatabaseSystem dbType, HikariConfig config) {
        databaseConfigurators.stream()
                             .filter(dc -> dc.isApplicable(dbType))
                             .forEach(dc -> dc.apply(config));
    }

    /**
     * Prepare root folder.
     *
     * @param name the name
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String prepareRootFolder(String name) throws IOException {
        String rootFolder = (Objects.equals(defaultDataSourceName, name))
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

    /**
     * Register data source bean.
     *
     * @param name the name
     * @param dataSource the data source
     */
    private void registerDataSourceBean(String name, DirigibleDataSource dataSource) {
        if (Objects.equals(systemDataSourceName, name)) {
            return; // bean already set by org.eclipse.dirigible.components.database.DataSourceSystemConfig
        }
        GenericApplicationContext genericAppContext = (GenericApplicationContext) applicationContext;
        ConfigurableListableBeanFactory beanFactory = genericAppContext.getBeanFactory();

        if (beanFactory.containsBean(name)) {
            logger.debug("Bean with name [{}] is already registered. Skipping its registration.", name);
            return;
        }
        beanFactory.registerSingleton(name, dataSource);
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

}
