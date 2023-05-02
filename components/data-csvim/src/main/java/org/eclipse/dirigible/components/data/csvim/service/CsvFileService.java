/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.csvim.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.csvim.domain.CsvFile;
import org.eclipse.dirigible.components.data.csvim.repository.CsvFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CsvFileService implements ArtefactService<CsvFile> {

    @Autowired
    private CsvFileRepository csvFileRepository;

    /**
     * @return all csvFiles
     */
    @Override
    public List<CsvFile> getAll() {
        return csvFileRepository.findAll();
    }

    /**
     * @param pageable the pageable
     * @return the page
     */
    @Override
    public Page<CsvFile> getPages(Pageable pageable) {
        return csvFileRepository.findAll(pageable);
    }

    /**
     * @param id the id
     * @return the csvFile
     */
    @Override
    public CsvFile findById(Long id) {
        Optional<CsvFile> csvFile = csvFileRepository.findById(id);
        if (csvFile.isPresent()) {
            return csvFile.get();
        } else {
            throw new IllegalArgumentException("CsvFile with id does not exist: " + id);
        }
    }

    /**
     * @param name the name
     * @return the csvFile
     */
    @Override
    public CsvFile findByName(String name) {
        CsvFile filter = new CsvFile();
        filter.setName(name);
        Example<CsvFile> example = Example.of(filter);
        Optional<CsvFile> csvFile = csvFileRepository.findOne(example);
        if (csvFile.isPresent()) {
            return csvFile.get();
        } else {
            throw new IllegalArgumentException("CsvFile with name does not exist: " + name);
        }
    }

    /**
     * @param location the location
     * @return the csvFile
     */
    @Override
    public List<CsvFile> findByLocation(String location) {
        CsvFile filter = new CsvFile();
        filter.setName(location);
        Example<CsvFile> example = Example.of(filter);
        return csvFileRepository.findAll(example);
    }

    /**
     * @param key the key
     * @return the csvFile
     */
    @Override
    public CsvFile findByKey(String key) {
        CsvFile filter = new CsvFile();
        filter.setKey(key);
        Example<CsvFile> example = Example.of(filter);
        Optional<CsvFile> csvFile = csvFileRepository.findOne(example);
        return csvFile.orElse(null);
    }

    /**
     * @param csvFile the csvFile
     * @return the csvFile
     */
    @Override
    public CsvFile save(CsvFile csvFile) {
        return csvFileRepository.saveAndFlush(csvFile);
    }

    /**
     * @param csvFile the csvFile
     */
    @Override
    public void delete(CsvFile csvFile) {
        csvFileRepository.delete(csvFile);
    }
}
