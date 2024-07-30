/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.source.snowpark;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourceInitializerContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * The Class DataSourceInitializerContributorSnowpark.
 */
@org.springframework.context.annotation.Configuration
public class DataSourceInitializerContributorSnowpark implements DataSourceInitializerContributor {
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInitializerContributorSnowpark.class);
    public static final String TOKEN_FILE_PATH = "/snowflake/session/token";

    /**
     * Contribute.
     *
     * @param dataSource the data source
     * @param properties the properties
     */
    @Override
    public void contribute(DataSource dataSource, Properties properties) {
        if (!dataSource.getName()
                       .startsWith("SNOWFLAKE")) {
            logger.info("[{}] will NOT contribute to datasource [{}]", this, dataSource.getName());
            return;
        }
        try {
            String url;
            properties.put("dataSource.CLIENT_SESSION_KEEP_ALIVE", true);
            setPropertyIfConfigAvailable("SNOWFLAKE_ACCOUNT", "dataSource.account", properties);
            setPropertyIfConfigAvailable("SNOWFLAKE_WAREHOUSE", "dataSource.warehouse", properties);
            setPropertyIfConfigAvailable("SNOWFLAKE_DATABASE", "dataSource.db", properties);
            setPropertyIfConfigAvailable("SNOWFLAKE_SCHEMA", "dataSource.schema", properties);

            if (hasTokenFile()) {
                properties.put("dataSource.authenticator", "OAUTH");
                properties.put("dataSource.token", new String(Files.readAllBytes(Paths.get(TOKEN_FILE_PATH))));
                properties.put("dataSource.insecureMode", true);
                url = "jdbc:snowflake://" + Configuration.get("SNOWFLAKE_HOST") + ":" + Configuration.get("SNOWFLAKE_PORT");
            } else {
                setPropertyIfConfigAvailable("SNOWFLAKE_ROLE", "dataSource.role", properties);
                setPropertyIfConfigAvailable("SNOWFLAKE_USERNAME", "dataSource.user", properties);
                setPropertyIfConfigAvailable("SNOWFLAKE_PASSWORD", "dataSource.password", properties);

                url = Configuration.get("SNOWFLAKE_URL", dataSource.getUrl());
            }

            properties.put("jdbcUrl", url);
            properties.put("dataSource.url", url);

        } catch (IOException ex) {
            logger.error("Invalid configuration for the datasource: [{}]", dataSource.getName(), ex);
        }
    }

    private static boolean hasTokenFile() {
        return Files.exists(Paths.get(TOKEN_FILE_PATH));
    }

    private void setPropertyIfConfigAvailable(String configName, String propertyName, Properties properties) {
        String value = Configuration.get(configName);
        if (StringUtils.isNotBlank(value)) {
            logger.debug("Setting property [{}] from config [{}]", propertyName, configName);
            properties.put(propertyName, value);
        } else {
            logger.debug("Will NOT set property [{}] since config [{}] value is [{}]", propertyName, configName, value);
        }
    }
}
