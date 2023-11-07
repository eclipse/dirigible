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
package org.eclipse.dirigible.components.odata.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.odata.domain.OData;
import org.eclipse.dirigible.components.odata.repository.ODataRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ODataService.
 */
@Service
@Transactional
public class ODataService implements ArtefactService<OData>, InitializingBean {

  /** The instance. */
  private static ODataService INSTANCE;

  /**
   * After properties set.
   *
   * @throws Exception the exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    INSTANCE = this;
  }

  /**
   * Gets the.
   *
   * @return the o data service
   */
  public static ODataService get() {
    return INSTANCE;
  }

  /** The OData repository. */
  @Autowired
  private ODataRepository odataRepository;

  /**
   * Gets the all.
   *
   * @return the all
   */
  @Override
  public List<OData> getAll() {
    return odataRepository.findAll();
  }

  /**
   * Find all.
   *
   * @param pageable the pageable
   * @return the page
   */
  @Override
  public Page<OData> getPages(Pageable pageable) {
    return odataRepository.findAll(pageable);
  }

  /**
   * Find by id.
   *
   * @param id the id
   * @return the OData
   */
  @Override
  public OData findById(Long id) {
    Optional<OData> odata = odataRepository.findById(id);
    if (odata.isPresent()) {
      return odata.get();
    } else {
      throw new IllegalArgumentException("OData with id does not exist: " + id);
    }
  }

  /**
   * Find by name.
   *
   * @param name the name
   * @return the OData
   */
  @Override
  public OData findByName(String name) {
    OData filter = new OData();
    filter.setName(name);
    Example<OData> example = Example.of(filter);
    Optional<OData> odata = odataRepository.findOne(example);
    if (odata.isPresent()) {
      return odata.get();
    } else {
      throw new IllegalArgumentException("OData with name does not exist: " + name);
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
  public List<OData> findByLocation(String location) {
    OData filter = new OData();
    filter.setLocation(location);
    Example<OData> example = Example.of(filter);
    List<OData> list = odataRepository.findAll(example);
    return list;
  }

  /**
   * Find by key.
   *
   * @param key the key
   * @return the OData
   */
  @Override
  @Transactional(readOnly = true)
  public OData findByKey(String key) {
    OData filter = new OData();
    filter.setKey(key);
    Example<OData> example = Example.of(filter);
    Optional<OData> odata = odataRepository.findOne(example);
    if (odata.isPresent()) {
      return odata.get();
    }
    return null;
  }

  /**
   * Save.
   *
   * @param odata the OData
   * @return the OData
   */
  @Override
  public OData save(OData odata) {
    return odataRepository.saveAndFlush(odata);
  }

  /**
   * Delete.
   *
   * @param odata the OData
   */
  @Override
  public void delete(OData odata) {
    odataRepository.delete(odata);
  }

}
