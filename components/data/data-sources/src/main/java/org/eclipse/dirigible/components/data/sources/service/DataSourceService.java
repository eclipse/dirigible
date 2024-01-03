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
import java.util.Optional;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.repository.DataSourceRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Data Source Service incoming requests.
 */
@Service
@Transactional
public class DataSourceService implements ArtefactService<DataSource> {



    private final DataSourceRepository datasourceRepository;
    private final List<DataSourceLifecycleListener> dataSourceListeners;

    DataSourceService(DataSourceRepository datasourceRepository, List<DataSourceLifecycleListener> dataSourceListeners) {
        this.datasourceRepository = datasourceRepository;
        this.dataSourceListeners = dataSourceListeners;
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getAll() {
        return datasourceRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DataSource> getPages(Pageable pageable) {
        return datasourceRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the data source
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource findById(Long id) {
        Optional<DataSource> table = datasourceRepository.findById(id);
        if (table.isPresent()) {
            return table.get();
        }
        throw new IllegalArgumentException("DataSource with id does not exist: " + id);
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the data source
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource findByName(String name) {
        DataSource filter = new DataSource();
        filter.setName(name);
        Example<DataSource> example = Example.of(filter);
        Optional<DataSource> table = datasourceRepository.findOne(example);
        if (table.isPresent()) {
            return table.get();
        }
        throw new IllegalArgumentException("DataSource with name does not exist: " + name);
    }

    /**
     * Find by location.
     *
     * @param location the location
     * @return the list
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataSource> findByLocation(String location) {
        DataSource filter = new DataSource();
        filter.setLocation(location);
        Example<DataSource> example = Example.of(filter);
        return datasourceRepository.findAll(example);
    }

    /**
     * Find by key.
     *
     * @param key the key
     * @return the table
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource findByKey(String key) {
        DataSource filter = new DataSource();
        filter.setKey(key);
        Example<DataSource> example = Example.of(filter);
        Optional<DataSource> table = datasourceRepository.findOne(example);
        if (table.isPresent()) {
            return table.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param datasource the datasource
     * @return the data source
     */
    @Override
    public DataSource save(DataSource datasource) {
        DataSource savedDataSource = datasourceRepository.saveAndFlush(datasource);
        dataSourceListeners.forEach(l -> l.onSaved(savedDataSource));
        return savedDataSource;
    }

    /**
     * Delete.
     *
     * @param datasource the datasource
     */
    @Override
    public void delete(DataSource datasource) {
        datasourceRepository.delete(datasource);
        dataSourceListeners.forEach(l -> l.onDeleted(datasource));
    }

}
