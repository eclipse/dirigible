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
package org.eclipse.dirigible.components.engine.camel.service;

import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourceInitializer;
import org.eclipse.dirigible.components.data.sources.service.DataSourceLifecycleListener;
import org.springframework.stereotype.Component;

@Component
class CamelDataSourceLifecycleListener implements DataSourceLifecycleListener {

    private final SpringBootCamelContext camelContext;
    private final DataSourceInitializer dataSourceInitializer;

    CamelDataSourceLifecycleListener(SpringBootCamelContext camelContext, DataSourceInitializer dataSourceInitializer) {
        this.camelContext = camelContext;
        this.dataSourceInitializer = dataSourceInitializer;
    }

    @Override
    public void onSave(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        javax.sql.DataSource sqlDataSource = dataSourceInitializer.initialize(dataSource);

        camelContext.getRegistry()
                    .bind(dataSourceName, sqlDataSource);

    }

    @Override
    public void onDelete(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        camelContext.getRegistry()
                    .unbind(dataSourceName);
    }

}
