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
package org.eclipse.dirigible.components.data.store.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.store.domain.Entity;
import org.eclipse.dirigible.components.data.store.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EntityService implements ArtefactService<Entity> {

  /** The entity repository. */
  @Autowired
  private EntityRepository entityRepository;

  /**
   * Gets the all.
   *
   * @return the all
   */
  @Override
  @Transactional(readOnly = true)
  public List<Entity> getAll() {
    return entityRepository.findAll();
  }

  /**
   * Find all.
   *
   * @param pageable the pageable
   * @return the page
   */
  @Override
  @Transactional(readOnly = true)
  public Page<Entity> getPages(Pageable pageable) {
    return entityRepository.findAll(pageable);
  }

  /**
   * Find by id.
   *
   * @param id the id
   * @return the entity
   */
  @Override
  @Transactional(readOnly = true)
  public Entity findById(Long id) {
    Optional<Entity> entity = entityRepository.findById(id);
    if (entity.isPresent()) {
      return entity.get();
    } else {
      throw new IllegalArgumentException("Entity with id does not exist: " + id);
    }
  }

  /**
   * Find by name.
   *
   * @param name the name
   * @return the entity
   */
  @Override
  @Transactional(readOnly = true)
  public Entity findByName(String name) {
    Entity filter = new Entity();
    filter.setName(name);
    Example<Entity> example = Example.of(filter);
    Optional<Entity> entity = entityRepository.findOne(example);
    if (entity.isPresent()) {
      return entity.get();
    } else {
      throw new IllegalArgumentException("Entity with name does not exist: " + name);
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
  public List<Entity> findByLocation(String location) {
    Entity filter = new Entity();
    filter.setLocation(location);
    Example<Entity> example = Example.of(filter);
    List<Entity> list = entityRepository.findAll(example);
    return list;
  }

  /**
   * Find by key.
   *
   * @param key the key
   * @return the entity point
   */
  @Override
  @Transactional(readOnly = true)
  public Entity findByKey(String key) {
    Entity filter = new Entity();
    filter.setKey(key);
    Example<Entity> example = Example.of(filter);
    Optional<Entity> entity = entityRepository.findOne(example);
    if (entity.isPresent()) {
      return entity.get();
    }
    return null;
  }

  /**
   * Save.
   *
   * @param entity the entity
   * @return the entity
   */
  @Override
  public Entity save(Entity entity) {
    return entityRepository.saveAndFlush(entity);
  }

  /**
   * Delete.
   *
   * @param entity the entity
   */
  @Override
  public void delete(Entity entity) {
    entityRepository.delete(entity);
  }

}
