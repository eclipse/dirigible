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
package org.eclipse.dirigible.components.odata.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.odata.domain.ODataSchema;
import org.eclipse.dirigible.components.odata.repository.ODataSchemaRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ODataSchemaService.
 */
@Service
@Transactional
public class ODataSchemaService implements ArtefactService<ODataSchema>, InitializingBean {
	
	/** The instance. */
	private static ODataSchemaService INSTANCE;
	
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
	 * @return the o data schema service
	 */
	public static ODataSchemaService get() {
        return INSTANCE;
    }
	
	/** The ODataSchema repository. */
    @Autowired
    private ODataSchemaRepository odataSchemaRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    public List<ODataSchema> getAll() {
        return odataSchemaRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    public Page<ODataSchema> getPages(Pageable pageable) {
        return odataSchemaRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the ODataSchema
     */
    @Override
    public ODataSchema findById(Long id) {
        Optional<ODataSchema> odataSchema = odataSchemaRepository.findById(id);
        if (odataSchema.isPresent()) {
            return odataSchema.get();
        } else {
            throw new IllegalArgumentException("OData Schema with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the ODataSchema
     */
    @Override
    public ODataSchema findByName(String name) {
    	ODataSchema filter = new ODataSchema();
        filter.setName(name);
        Example<ODataSchema> example = Example.of(filter);
        Optional<ODataSchema> odataSchema = odataSchemaRepository.findOne(example);
        if (odataSchema.isPresent()) {
            return odataSchema.get();
        } else {
            throw new IllegalArgumentException("OData Schema with name does not exist: " + name);
        }
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the ODataSchema
     */
    @Override
    @Transactional(readOnly = true)
    public ODataSchema findByKey(String key) {
    	ODataSchema filter = new ODataSchema();
        filter.setKey(key);
        Example<ODataSchema> example = Example.of(filter);
        Optional<ODataSchema> odataSchema = odataSchemaRepository.findOne(example);
        if (odataSchema.isPresent()) {
            return odataSchema.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param odataSchema the ODataSchema
     * @return the ODataSchema
     */
    @Override
    public ODataSchema save(ODataSchema odataSchema) {
        return odataSchemaRepository.saveAndFlush(odataSchema);
    }

    /**
     * Delete.
     *
     * @param odataSchema the ODataSchema
     */
    @Override
    public void delete(ODataSchema odataSchema) {
    	odataSchemaRepository.delete(odataSchema);
    }
    
    /**
     * Removes the schema.
     *
     * @param location the location
     */
    public void removeSchema(String location) {
    	ODataSchema filter = new ODataSchema();
        filter.setLocation(location);
        Example<ODataSchema> example = Example.of(filter);
        odataSchemaRepository.deleteAll(odataSchemaRepository.findAll(example));
    }

}
