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
package org.eclipse.dirigible.components.engine.web.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.engine.web.domain.Expose;
import org.eclipse.dirigible.components.engine.web.repository.ExposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Expose incoming requests.
 */
@Service
@Transactional
public class ExposeService implements ArtefactService<Expose> {
	
	/** The extension repository. */
	@Autowired 
	private ExposeRepository exposeRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Expose> getAll() {
		return exposeRepository.findAll();
	}
	
	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Expose> getPages(Pageable pageable) {
		return exposeRepository.findAll(pageable);
	}
	
	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the expose
	 */
	@Override
	@Transactional(readOnly = true)
	public Expose findById(Long id) {
		Optional<Expose> extension = exposeRepository.findById(id);
		if (extension.isPresent()) {
			return extension.get();
		} else {
			throw new IllegalArgumentException("Extension with id does not exist: " + id);
		}
	}
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the expose
	 */
	@Override
	@Transactional(readOnly = true)
	public Expose findByName(String name) {
		Expose filter = new Expose();
		filter.setName(name);
		Example<Expose> example = Example.of(filter);
		Optional<Expose> expose = exposeRepository.findOne(example);
		if (expose.isPresent()) {
			return expose.get();
		} else {
			throw new IllegalArgumentException("Extension with name does not exist: " + name);
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
    public List<Expose> findByLocation(String location) {
    	Expose filter = new Expose();
        filter.setName(location);
        Example<Expose> example = Example.of(filter);
        List<Expose> list = exposeRepository.findAll(example);
        return list;
    }
	
	/**
     * Find by key.
     *
     * @param key the key
     * @return the expose
     */
    @Override
    @Transactional(readOnly = true)
    public Expose findByKey(String key) {
    	Expose filter = new Expose();
        filter.setKey(key);
        Example<Expose> example = Example.of(filter);
        Optional<Expose> expose = exposeRepository.findOne(example);
        if (expose.isPresent()) {
            return expose.get();
        }
        return null;
    }
	
	/**
	 * Save.
	 *
	 * @param expose the expose
	 * @return the expose
	 */
	@Override
	public Expose save(Expose expose) {
		return exposeRepository.saveAndFlush(expose);
	}
	
	/**
	 * Delete.
	 *
	 * @param expose the extension
	 */
	@Override
	public void delete(Expose expose) {
		exposeRepository.delete(expose);
	}

}
