/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.sources.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.database.DatabaseSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;

/**
 * The pool of a data source honours its {@code <name>_HIKARI_*} configurations; the platform's own
 * sizes are only defaults (#7876).
 */
class DataSourceInitializerPoolConfigTest {

    private static final String TENANT_DS = "a1b2c3_DefaultDB";

    private final List<String> setKeys = new ArrayList<>();

    private final DataSourceInitializer initializer = new DataSourceInitializer(null, List.of(), null, "SystemDB", "DefaultDB", null);

    @AfterEach
    void clear() {
        setKeys.forEach(Configuration::remove);
    }

    private void set(String key, String value) {
        Configuration.set(key, value);
        setKeys.add(key);
    }

    private HikariConfig poolOf(String name) {
        DataSource dataSource = new DataSource("/test/" + name, name, null, "org.h2.Driver", "jdbc:h2:mem:" + name, "sa", "");
        return initializer.createPoolConfig(dataSource, DatabaseSystem.H2);
    }

    @Test
    void withoutConfigurationsThePlatformDefaultsApply() {
        HikariConfig config = poolOf("DefaultDB");

        assertThat(config.getMaximumPoolSize()).isEqualTo(DataSourceInitializer.DEFAULT_MAX_POOL_SIZE);
        assertThat(config.getMinimumIdle()).isEqualTo(DataSourceInitializer.DEFAULT_MIN_IDLE);
        assertThat(config.getIdleTimeout()).isEqualTo(TimeUnit.MINUTES.toMillis(3));
    }

    @Test
    void theHikariConfigurationsWinOverTheDefaults() {
        set("DefaultDB_HIKARI_MAXIMUMPOOLSIZE", "5");
        set("DefaultDB_HIKARI_MINIMUMIDLE", "0");
        set("DefaultDB_HIKARI_IDLETIMEOUT", "60000");

        HikariConfig config = poolOf("DefaultDB");

        assertThat(config.getMaximumPoolSize()).isEqualTo(5);
        assertThat(config.getMinimumIdle()).isZero();
        assertThat(config.getIdleTimeout()).isEqualTo(60000);
    }

    @Test
    void camelCaseHikariConfigurationsWinToo() {
        set("DefaultDB_HIKARI_maximumPoolSize", "7");

        assertThat(poolOf("DefaultDB").getMaximumPoolSize()).isEqualTo(7);
    }

    @Test
    void aTenantDataSourceTakesTheInstanceWideTenantDefaults() {
        set(DirigibleConfig.TENANT_DATASOURCE_MAX_POOL_SIZE.getKey(), "4");
        set(DirigibleConfig.TENANT_DATASOURCE_MIN_IDLE.getKey(), "0");

        HikariConfig tenant = poolOf(TENANT_DS);
        HikariConfig system = poolOf("SystemDB");

        assertThat(tenant.getMaximumPoolSize()).isEqualTo(4);
        assertThat(tenant.getMinimumIdle()).isZero();
        assertThat(system.getMaximumPoolSize()).isEqualTo(DataSourceInitializer.DEFAULT_MAX_POOL_SIZE);
        assertThat(system.getMinimumIdle()).isEqualTo(DataSourceInitializer.DEFAULT_MIN_IDLE);
    }

    @Test
    void aTenantDataSourceHoldsOneIdleConnectionByDefault() {
        assertThat(poolOf(TENANT_DS).getMinimumIdle()).isEqualTo(1);
        assertThat(poolOf(TENANT_DS).getMaximumPoolSize()).isEqualTo(20);
    }

    @Test
    void aTenantDataSourcesOwnHikariConfigurationWinsOverTheTenantDefaults() {
        set(DirigibleConfig.TENANT_DATASOURCE_MAX_POOL_SIZE.getKey(), "4");
        set(TENANT_DS + "_HIKARI_MAXIMUMPOOLSIZE", "9");

        assertThat(poolOf(TENANT_DS).getMaximumPoolSize()).isEqualTo(9);
    }

    @Test
    void propertyNamesAreMatchedIgnoringCase() {
        assertThat(DataSourceInitializer.toHikariPropertyName("MAXIMUMPOOLSIZE")).isEqualTo("maximumPoolSize");
        assertThat(DataSourceInitializer.toHikariPropertyName("minimumidle")).isEqualTo("minimumIdle");
        assertThat(DataSourceInitializer.toHikariPropertyName("unknownThing")).isEqualTo("unknownThing");
    }
}
