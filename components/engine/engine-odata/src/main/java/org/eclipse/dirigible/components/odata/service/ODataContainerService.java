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
import org.eclipse.dirigible.components.odata.domain.ODataContainer;
import org.eclipse.dirigible.components.odata.repository.ODataContainerRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ODataContainerService.
 */
@Service
@Transactional
public class ODataContainerService implements ArtefactService<ODataContainer>, InitializingBean {
	
	/** The instance. */
	private static ODataContainerService INSTANCE;
	
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
	 * @return the o data container service
	 */
	public static ODataContainerService get() {
        return INSTANCE;
    }
	
	/** The ODataContainer repository. */
    @Autowired
    private ODataContainerRepository odataContainerRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    public List<ODataContainer> getAll() {
        return odataContainerRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    public Page<ODataContainer> getPages(Pageable pageable) {
        return odataContainerRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the ODataContainer
     */
    @Override
    public ODataContainer findById(Long id) {
        Optional<ODataContainer> odataContainer = odataContainerRepository.findById(id);
        if (odataContainer.isPresent()) {
            return odataContainer.get();
        } else {
            throw new IllegalArgumentException("OData Container with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the ODataContainer
     */
    @Override
    public ODataContainer findByName(String name) {
    	ODataContainer filter = new ODataContainer();
        filter.setName(name);
        Example<ODataContainer> example = Example.of(filter);
        Optional<ODataContainer> odataContainer = odataContainerRepository.findOne(example);
        if (odataContainer.isPresent()) {
            return odataContainer.get();
        } else {
            throw new IllegalArgumentException("OData Container with name does not exist: " + name);
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
    public List<ODataContainer> findByLocation(String location) {
    	ODataContainer filter = new ODataContainer();
        filter.setLocation(location);
        Example<ODataContainer> example = Example.of(filter);
        List<ODataContainer> list = odataContainerRepository.findAll(example);
        return list;
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the ODataContainer
     */
    @Override
    @Transactional(readOnly = true)
    public ODataContainer findByKey(String key) {
    	ODataContainer filter = new ODataContainer();
        filter.setKey(key);
        Example<ODataContainer> example = Example.of(filter);
        Optional<ODataContainer> odataContainer = odataContainerRepository.findOne(example);
        if (odataContainer.isPresent()) {
            return odataContainer.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param odataContainer the ODataContainer
     * @return the ODataContainer
     */
    @Override
    public ODataContainer save(ODataContainer odataContainer) {
        return odataContainerRepository.saveAndFlush(odataContainer);
    }

    /**
     * Delete.
     *
     * @param odataContainer the ODataContainer
     */
    @Override
    public void delete(ODataContainer odataContainer) {
    	odataContainerRepository.delete(odataContainer);
    }
    
    /**
     * Removes the container.
     *
     * @param location the location
     */
    public void removeContainer(String location) {
    	ODataContainer filter = new ODataContainer();
        filter.setLocation(location);
        Example<ODataContainer> example = Example.of(filter);
        odataContainerRepository.deleteAll(odataContainerRepository.findAll(example));
    }

}
