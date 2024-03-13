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
package org.eclipse.dirigible.components.data.sources.service;

import java.util.List;
import org.eclipse.dirigible.components.base.artefact.BaseArtefactService;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.repository.DataSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Data Source Service incoming requests.
 */
@Service
@Transactional
public class DataSourceService extends BaseArtefactService<DataSource, Long> {

    private final List<DataSourceLifecycleListener> dataSourceListeners;

    DataSourceService(DataSourceRepository datasourceRepository, List<DataSourceLifecycleListener> dataSourceListeners) {
        super(datasourceRepository);
        this.dataSourceListeners = dataSourceListeners;
    }

    @Override
    public DataSource save(DataSource datasource) {
        DataSource savedDataSource = super.save(datasource);
        dataSourceListeners.forEach(l -> l.onSave(savedDataSource));
        return savedDataSource;
    }

    @Override
    public void delete(DataSource datasource) {
        super.delete(datasource);
        dataSourceListeners.forEach(l -> l.onDelete(datasource));
    }

}
