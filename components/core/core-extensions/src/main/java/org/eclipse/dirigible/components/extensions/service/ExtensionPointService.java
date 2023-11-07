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
package org.eclipse.dirigible.components.extensions.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.eclipse.dirigible.components.extensions.repository.ExtensionPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Extension Points Service incoming requests.
 */
@Service
@Transactional
public class ExtensionPointService implements ArtefactService<ExtensionPoint> {

  /** The extension point repository. */
  @Autowired
  private ExtensionPointRepository extensionPointRepository;

  /**
   * Gets the all.
   *
   * @return the all
   */
  @Override
  @Transactional(readOnly = true)
  public List<ExtensionPoint> getAll() {
    return extensionPointRepository.findAll();
  }

  /**
   * Find all.
   *
   * @param pageable the pageable
   * @return the page
   */
  @Override
  @Transactional(readOnly = true)
  public Page<ExtensionPoint> getPages(Pageable pageable) {
    return extensionPointRepository.findAll(pageable);
  }

  /**
   * Find by id.
   *
   * @param id the id
   * @return the extension point
   */
  @Override
  @Transactional(readOnly = true)
  public ExtensionPoint findById(Long id) {
    Optional<ExtensionPoint> extensionPoint = extensionPointRepository.findById(id);
    if (extensionPoint.isPresent()) {
      return extensionPoint.get();
    } else {
      throw new IllegalArgumentException("ExtensionPoint with id does not exist: " + id);
    }
  }

  /**
   * Find by name.
   *
   * @param name the name
   * @return the extension point
   */
  @Override
  @Transactional(readOnly = true)
  public ExtensionPoint findByName(String name) {
    ExtensionPoint filter = new ExtensionPoint();
    filter.setName(name);
    Example<ExtensionPoint> example = Example.of(filter);
    Optional<ExtensionPoint> extensionPoint = extensionPointRepository.findOne(example);
    if (extensionPoint.isPresent()) {
      return extensionPoint.get();
    } else {
      throw new IllegalArgumentException("ExtensionPoint with name does not exist: " + name);
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
  public List<ExtensionPoint> findByLocation(String location) {
    ExtensionPoint filter = new ExtensionPoint();
    filter.setLocation(location);
    Example<ExtensionPoint> example = Example.of(filter);
    List<ExtensionPoint> list = extensionPointRepository.findAll(example);
    return list;
  }

  /**
   * Find by key.
   *
   * @param key the key
   * @return the extension point
   */
  @Override
  @Transactional(readOnly = true)
  public ExtensionPoint findByKey(String key) {
    ExtensionPoint filter = new ExtensionPoint();
    filter.setKey(key);
    Example<ExtensionPoint> example = Example.of(filter);
    Optional<ExtensionPoint> extensionPoint = extensionPointRepository.findOne(example);
    if (extensionPoint.isPresent()) {
      return extensionPoint.get();
    }
    return null;
  }

  /**
   * Save.
   *
   * @param extensionPoint the extension point
   * @return the extension point
   */
  @Override
  public ExtensionPoint save(ExtensionPoint extensionPoint) {
    return extensionPointRepository.saveAndFlush(extensionPoint);
  }

  /**
   * Delete.
   *
   * @param extensionPoint the extension point
   */
  @Override
  public void delete(ExtensionPoint extensionPoint) {
    extensionPointRepository.delete(extensionPoint);
  }

}
