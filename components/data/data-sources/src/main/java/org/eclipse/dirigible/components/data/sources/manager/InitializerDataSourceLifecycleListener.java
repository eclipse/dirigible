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

import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.DataSourceLifecycleListener;
import org.springframework.stereotype.Component;

@Component
class InitializerDataSourceLifecycleListener implements DataSourceLifecycleListener {

    private final DataSourceInitializer dataSourceInitializer;

    InitializerDataSourceLifecycleListener(DataSourceInitializer dataSourceInitializer) {
        this.dataSourceInitializer = dataSourceInitializer;
    }

    @Override
    public void onSave(DataSource dataSource) {
        // nothing to do here
    }

    @Override
    public void onDelete(DataSource dataSource) {
        String dataSourceName = dataSource.getName();
        dataSourceInitializer.removeInitializedDataSource(dataSourceName);
    }


}
