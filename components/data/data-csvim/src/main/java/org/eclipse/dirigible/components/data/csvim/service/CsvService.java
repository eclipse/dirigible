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
package org.eclipse.dirigible.components.data.csvim.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.csvim.domain.Csv;
import org.eclipse.dirigible.components.data.csvim.repository.CsvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Class CsvService.
 */
@Service
@Transactional
public class CsvService implements ArtefactService<Csv> {

  /**
   * The csv repository.
   */
  @Autowired
  private CsvRepository csvRepository;


  /**
   * Gets the all.
   *
   * @return all csvs
   */
  @Override
  public List<Csv> getAll() {
    return csvRepository.findAll();
  }

  /**
   * Gets the pages.
   *
   * @param pageable the pageable
   * @return the page
   */
  @Override
  public Page<Csv> getPages(Pageable pageable) {
    return csvRepository.findAll(pageable);
  }

  /**
   * Find by id.
   *
   * @param id the id
   * @return the csv
   */
  @Override
  public Csv findById(Long id) {
    Optional<Csv> csv = csvRepository.findById(id);
    if (csv.isPresent()) {
      return csv.get();
    } else {
      throw new IllegalArgumentException("Csv with id does not exist: " + id);
    }
  }

  /**
   * Find by name.
   *
   * @param name the name
   * @return the csv
   */
  @Override
  public Csv findByName(String name) {
    Csv filter = new Csv();
    filter.setName(name);
    Example<Csv> example = Example.of(filter);
    Optional<Csv> csv = csvRepository.findOne(example);
    if (csv.isPresent()) {
      return csv.get();
    } else {
      throw new IllegalArgumentException("Csv with name does not exist: " + name);
    }
  }

  /**
   * Find by location.
   *
   * @param location the location
   * @return the list of csv's
   */
  @Override
  public List<Csv> findByLocation(String location) {
    Csv filter = new Csv();
    filter.setLocation(location);
    Example<Csv> example = Example.of(filter);
    return csvRepository.findAll(example);
  }

  /**
   * Find by key.
   *
   * @param key the key
   * @return the csv
   */
  @Override
  public Csv findByKey(String key) {
    Csv filter = new Csv();
    filter.setKey(key);
    Example<Csv> example = Example.of(filter);
    Optional<Csv> csv = csvRepository.findOne(example);
    return csv.orElse(null);
  }

  /**
   * Save.
   *
   * @param csv the csv
   * @return the csv
   */
  @Override
  public Csv save(Csv csv) {
    return csvRepository.saveAndFlush(csv);
  }

  /**
   * Delete.
   *
   * @param csv the csv
   */
  @Override
  public void delete(Csv csv) {
    csvRepository.delete(csv);
  }
}
