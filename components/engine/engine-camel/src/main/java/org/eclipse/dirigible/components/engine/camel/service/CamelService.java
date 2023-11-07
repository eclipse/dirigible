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

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.eclipse.dirigible.components.engine.camel.repository.CamelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CamelService implements ArtefactService<Camel> {

  /** The camel repository. */
  @Autowired
  private CamelRepository camelRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Camel> getAll() {
    return camelRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Camel> getPages(Pageable pageable) {
    return camelRepository.findAll(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Camel findById(Long id) {
    Optional<Camel> camel = camelRepository.findById(id);
    if (camel.isPresent()) {
      return camel.get();
    } else {
      throw new IllegalArgumentException("Camel with id does not exist: " + id);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Camel findByName(String name) {
    Camel filter = new Camel();
    filter.setName(name);
    Example<Camel> example = Example.of(filter);
    Optional<Camel> camel = camelRepository.findOne(example);
    if (camel.isPresent()) {
      return camel.get();
    } else {
      throw new IllegalArgumentException("Camel with name does not exist: " + name);
    }
  }

  @Override
  public List<Camel> findByLocation(String location) {
    Camel filter = new Camel();
    filter.setLocation(location);
    Example<Camel> example = Example.of(filter);
    return camelRepository.findAll(example);
  }

  @Override
  @Transactional(readOnly = true)
  public Camel findByKey(String key) {
    Camel filter = new Camel();
    filter.setKey(key);
    Example<Camel> example = Example.of(filter);
    Optional<Camel> camel = camelRepository.findOne(example);
    if (camel.isPresent()) {
      return camel.get();
    }
    return null;
  }

  @Override
  public Camel save(Camel camel) {
    return camelRepository.saveAndFlush(camel);
  }

  @Override
  public void delete(Camel camel) {
    camelRepository.delete(camel);
  }
}
