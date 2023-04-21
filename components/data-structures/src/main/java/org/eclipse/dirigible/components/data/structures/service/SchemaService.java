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
package org.eclipse.dirigible.components.data.structures.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.structures.domain.Schema;
import org.eclipse.dirigible.components.data.structures.repository.SchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Schemas Service incoming requests.
 */
@Service
@Transactional
public class SchemaService implements ArtefactService<Schema> {
	
	/** The Schema repository. */
	@Autowired 
	private SchemaRepository schemaRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Schema> getAll() {
		return schemaRepository.findAll();
	}
	
	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Schema> getPages(Pageable pageable) {
		return schemaRepository.findAll(pageable);
	}
	
	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the schema
	 */
	@Override
	@Transactional(readOnly = true)
	public Schema findById(Long id) {
		Optional<Schema> schema = schemaRepository.findById(id);
		if (schema.isPresent()) {
			return schema.get();
		} else {
			throw new IllegalArgumentException("Schema with id does not exist: " + id);
		}
	}
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the schema
	 */
	@Override
	@Transactional(readOnly = true)
	public Schema findByName(String name) {
		Schema filter = new Schema();
		filter.setName(name);
		Example<Schema> example = Example.of(filter);
		Optional<Schema> schema = schemaRepository.findOne(example);
		if (schema.isPresent()) {
			return schema.get();
		} else {
			throw new IllegalArgumentException("Schema with name does not exist: " + name);
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
    public List<Schema> findByLocation(String location) {
    	Schema filter = new Schema();
        filter.setName(location);
        Example<Schema> example = Example.of(filter);
        List<Schema> list = schemaRepository.findAll(example);
        return list;
    }
	
	/**
     * Find by key.
     *
     * @param key the key
     * @return the schema
     */
    @Override
    @Transactional(readOnly = true)
    public Schema findByKey(String key) {
    	Schema filter = new Schema();
        filter.setKey(key);
        Example<Schema> example = Example.of(filter);
        Optional<Schema> schema = schemaRepository.findOne(example);
        if (schema.isPresent()) {
            return schema.get();
        }
        return null;
    }
	
	/**
	 * Save.
	 *
	 * @param schema the schema
	 * @return the schema
	 */
	@Override
	public Schema save(Schema schema) {
		return schemaRepository.saveAndFlush(schema);
	}
	
	/**
	 * Delete.
	 *
	 * @param schema the schema
	 */
	@Override
	public void delete(Schema schema) {
		schemaRepository.delete(schema);
	}

}
