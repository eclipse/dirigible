/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.openapi.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.openapi.domain.OpenAPI;
import org.eclipse.dirigible.components.openapi.repository.OpenAPIRepository;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The Class OpenAPIsCoreService.
 */


@Service
@Transactional
public class OpenAPIService implements ArtefactService<OpenAPI> {

    @Autowired
    private OpenAPIRepository openAPIRepository;

    /**
     * The repository.
     */
    private IRepository repository;

    @Autowired
    public OpenAPIService(IRepository repository) {
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
    
    @Override
    @Transactional(readOnly = true)
    public List<OpenAPI> getAll() {
        return openAPIRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpenAPI> findAll(Pageable pageable) {
        return openAPIRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public OpenAPI findById(Long id) {
        Optional<OpenAPI> openAPI = openAPIRepository.findById(id);
        if (openAPI.isPresent()) {
            return openAPI.get();
        } else {
            throw new IllegalArgumentException("OpenAPI with id does not exist: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OpenAPI findByName(String name) {
        OpenAPI filter = new OpenAPI();
        filter.setName(name);
        Example<OpenAPI> example = Example.of(filter);
        Optional<OpenAPI> openAPI = openAPIRepository.findOne(example);
        if (openAPI.isPresent()) {
            return openAPI.get();
        } else {
            throw new IllegalArgumentException("OpenAPI with name does not exist: " + name);
        }
    }

    @Override
    public OpenAPI save(OpenAPI openAPI) {
        return openAPIRepository.saveAndFlush(openAPI);
    }

    @Override
    public void delete(OpenAPI openAPI) {
        openAPIRepository.delete(openAPI);
    }
}
