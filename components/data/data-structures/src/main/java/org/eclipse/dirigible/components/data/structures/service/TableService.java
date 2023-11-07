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
package org.eclipse.dirigible.components.data.structures.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.eclipse.dirigible.components.data.structures.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Tables Service incoming requests.
 */
@Service
@Transactional
public class TableService implements ArtefactService<Table> {

    /** The table repository. */
    @Autowired
    private TableRepository tableRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<Table> getAll() {
        return tableRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Table> getPages(Pageable pageable) {
        return tableRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the table
     */
    @Override
    @Transactional(readOnly = true)
    public Table findById(Long id) {
        Optional<Table> table = tableRepository.findById(id);
        if (table.isPresent()) {
            return table.get();
        } else {
            throw new IllegalArgumentException("Table with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the table
     */
    @Override
    @Transactional(readOnly = true)
    public Table findByName(String name) {
        Table filter = new Table();
        filter.setName(name);
        Example<Table> example = Example.of(filter);
        Optional<Table> table = tableRepository.findOne(example);
        if (table.isPresent()) {
            return table.get();
        } else {
            throw new IllegalArgumentException("Table with name does not exist: " + name);
        }
    }

    /**
     * Find by location.
     *
     * @param location the location
     * @return the list
     */
    @Override
    @Transactional(readOnly = true)
    public List<Table> findByLocation(String location) {
        Table filter = new Table();
        filter.setLocation(location);
        Example<Table> example = Example.of(filter);
        List<Table> list = tableRepository.findAll(example);
        return list;
    }

    /**
     * Find by key.
     *
     * @param key the key
     * @return the table
     */
    @Override
    @Transactional(readOnly = true)
    public Table findByKey(String key) {
        Table filter = new Table();
        filter.setKey(key);
        Example<Table> example = Example.of(filter);
        Optional<Table> table = tableRepository.findOne(example);
        if (table.isPresent()) {
            return table.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param table the table
     * @return the table
     */
    @Override
    public Table save(Table table) {
        return tableRepository.saveAndFlush(table);
    }

    /**
     * Delete.
     *
     * @param table the table
     */
    @Override
    public void delete(Table table) {
        tableRepository.delete(table);
    }

}
