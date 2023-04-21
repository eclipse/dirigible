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
package org.eclipse.dirigible.components.engine.wiki.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.engine.wiki.domain.Confluence;
import org.eclipse.dirigible.components.engine.wiki.repository.ConfluenceRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class ConfluenceService.
 */

@Service
@Transactional
public class ConfluenceService implements ArtefactService<Confluence> {

    /** The confluence repository. */
    @Autowired
    private ConfluenceRepository confluenceRepository;

    /**
     * The repository.
     */
    private IRepository repository;

    /**
     * Instantiates a new confluence service.
     * 
     * @param repository the repository
     */
    @Autowired
    public ConfluenceService(IRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets the repository.
     *
     * @return the repository
     */
    protected IRepository getRepository() {
        return repository;
    }

    /**
     * Gets the resource.
     *
     * @param path the path
     * @return the resource
     */
    public IResource getResource(String path) {
        return getRepository().getResource(path);
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<Confluence> getAll() {
        return confluenceRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Confluence> getPages(Pageable pageable) {
        return confluenceRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the confluence
     */
    @Override
    @Transactional(readOnly = true)
    public Confluence findById(Long id) {
        Optional<Confluence> confluence = confluenceRepository.findById(id);
        if (confluence.isPresent()) {
            return confluence.get();
        } else {
            throw new IllegalArgumentException("Confluence with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the confluence
     */
    @Override
    @Transactional(readOnly = true)
    public Confluence findByName(String name) {
    	Confluence filter = new Confluence();
        filter.setName(name);
        Example<Confluence> example = Example.of(filter);
        Optional<Confluence> confluence = confluenceRepository.findOne(example);
        if (confluence.isPresent()) {
            return confluence.get();
        } else {
            throw new IllegalArgumentException("Confluence with name does not exist: " + name);
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
    public List<Confluence> findByLocation(String location) {
    	Confluence filter = new Confluence();
        filter.setName(location);
        Example<Confluence> example = Example.of(filter);
        List<Confluence> list = confluenceRepository.findAll(example);
        return list;
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the confluence
     */
    @Override
    @Transactional(readOnly = true)
    public Confluence findByKey(String key) {
    	Confluence filter = new Confluence();
        filter.setKey(key);
        Example<Confluence> example = Example.of(filter);
        Optional<Confluence> confluence = confluenceRepository.findOne(example);
        if (confluence.isPresent()) {
            return confluence.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param confluence the confluence
     * @return the confluence
     */
    @Override
    public Confluence save(Confluence confluence) {
        return confluenceRepository.saveAndFlush(confluence);
    }

    /**
     * Delete.
     *
     * @param confluence the confluence
     */
    @Override
    public void delete(Confluence confluence) {
        confluenceRepository.delete(confluence);
    }
}
