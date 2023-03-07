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
import org.eclipse.dirigible.components.odata.domain.ODataHandler;
import org.eclipse.dirigible.components.odata.domain.ODataMapping;
import org.eclipse.dirigible.components.odata.factory.DirigibleODataServiceFactory;
import org.eclipse.dirigible.components.odata.repository.ODataMappingRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ODataMappingService.
 */
@Service
@Transactional
public class ODataMappingService implements ArtefactService<ODataMapping>, InitializingBean {
	
	/** The instance. */
	private static ODataMappingService INSTANCE;
	
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
	 * @return the o data mapping service
	 */
	public static ODataMappingService get() {
        return INSTANCE;
    }
	
	/** The ODataMapping repository. */
    @Autowired
    private ODataMappingRepository odataMappingRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    public List<ODataMapping> getAll() {
        return odataMappingRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    public Page<ODataMapping> getPages(Pageable pageable) {
        return odataMappingRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the ODataMapping
     */
    @Override
    public ODataMapping findById(Long id) {
        Optional<ODataMapping> odataMapping = odataMappingRepository.findById(id);
        if (odataMapping.isPresent()) {
            return odataMapping.get();
        } else {
            throw new IllegalArgumentException("OData Mapping with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the ODataMapping
     */
    @Override
    public ODataMapping findByName(String name) {
    	ODataMapping filter = new ODataMapping();
        filter.setName(name);
        Example<ODataMapping> example = Example.of(filter);
        Optional<ODataMapping> odataMapping = odataMappingRepository.findOne(example);
        if (odataMapping.isPresent()) {
            return odataMapping.get();
        } else {
            throw new IllegalArgumentException("OData Mapping with name does not exist: " + name);
        }
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the ODataMapping
     */
    @Override
    @Transactional(readOnly = true)
    public ODataMapping findByKey(String key) {
    	ODataMapping filter = new ODataMapping();
        filter.setKey(key);
        Example<ODataMapping> example = Example.of(filter);
        Optional<ODataMapping> odataMapping = odataMappingRepository.findOne(example);
        if (odataMapping.isPresent()) {
            return odataMapping.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param odataMapping the ODataMapping
     * @return the ODataMapping
     */
    @Override
    public ODataMapping save(ODataMapping odataMapping) {
        return odataMappingRepository.saveAndFlush(odataMapping);
    }

    /**
     * Delete.
     *
     * @param odataMapping the ODataMapping
     */
    @Override
    public void delete(ODataMapping odataMapping) {
    	odataMappingRepository.delete(odataMapping);
    }
    
    /**
     * Removes the mapping.
     *
     * @param location the location
     */
    public void removeMappings(String location) {
    	ODataMapping filter = new ODataMapping();
        filter.setLocation(location);
        Example<ODataMapping> example = Example.of(filter);
        odataMappingRepository.deleteAll(odataMappingRepository.findAll(example));
    }

}
