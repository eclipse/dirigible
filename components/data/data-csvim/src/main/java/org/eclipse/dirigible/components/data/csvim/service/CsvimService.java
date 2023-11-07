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
import org.eclipse.dirigible.components.data.csvim.domain.Csvim;
import org.eclipse.dirigible.components.data.csvim.repository.CsvimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Class ExtensionsCoreService.
 */
@Service
@Transactional
public class CsvimService implements ArtefactService<Csvim> {

  /**
   * The Constant logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(CsvimService.class);

  /**
   * The csvim repository.
   */
  @Autowired
  private CsvimRepository csvimRepository;

  /**
   * Gets the all.
   *
   * @return the all
   */
  @Override
  @Transactional(readOnly = true)
  public List<Csvim> getAll() {
    return csvimRepository.findAll();
  }

  /**
   * Find all.
   *
   * @param pageable the pageable
   * @return the page
   */
  @Override
  @Transactional(readOnly = true)
  public Page<Csvim> getPages(Pageable pageable) {
    return csvimRepository.findAll(pageable);
  }

  /**
   * Find by id.
   *
   * @param id the id
   * @return the csvim
   */
  @Override
  @Transactional(readOnly = true)
  public Csvim findById(Long id) {
    Optional<Csvim> csvimDefinition = csvimRepository.findById(id);
    if (csvimDefinition.isPresent()) {
      return csvimDefinition.get();
    } else {
      throw new IllegalArgumentException("csvimDefinition with id does not exist: " + id);
    }
  }

  /**
   * Find by name.
   *
   * @param name the name
   * @return the csvim
   */
  @Override
  @Transactional(readOnly = true)
  public Csvim findByName(String name) {
    Csvim filter = new Csvim();
    filter.setName(name);
    Example<Csvim> example = Example.of(filter);
    Optional<Csvim> CsvimDefinition = csvimRepository.findOne(example);
    if (CsvimDefinition.isPresent()) {
      return CsvimDefinition.get();
    } else {
      throw new IllegalArgumentException("csvimDefinition with name does not exist: " + name);
    }
  }

  /**
   * Find by location.
   *
   * @param location the location
   * @return the list of csvims
   */
  @Override
  @Transactional(readOnly = true)
  public List<Csvim> findByLocation(String location) {
    Csvim filter = new Csvim();
    filter.setLocation(location);
    Example<Csvim> example = Example.of(filter);
    return csvimRepository.findAll(example);
  }

  /**
   * Find by key.
   *
   * @param key the key
   * @return the csvim
   */
  @Override
  @Transactional(readOnly = true)
  public Csvim findByKey(String key) {
    Csvim filter = new Csvim();
    filter.setKey(key);
    Example<Csvim> example = Example.of(filter);
    Optional<Csvim> csvimDefinition = csvimRepository.findOne(example);
    return csvimDefinition.orElse(null);
  }

  /**
   * Save.
   *
   * @param csvim the csvim
   * @return the csvim
   */
  @Override
  public Csvim save(Csvim csvim) {
    return csvimRepository.saveAndFlush(csvim);
  }

  /**
   * Delete.
   *
   * @param csvim the csvim
   */
  @Override
  public void delete(Csvim csvim) {
    csvimRepository.delete(csvim);
  }

}
