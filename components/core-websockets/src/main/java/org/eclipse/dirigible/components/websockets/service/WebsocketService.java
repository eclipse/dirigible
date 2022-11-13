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
package org.eclipse.dirigible.components.websockets.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.repository.WebsocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class WebsocketService.
 */
@Service
@Transactional
public class WebsocketService implements ArtefactService<Websocket> {

    /** The websocket repository. */
    @Autowired
    private WebsocketRepository websocketRepository;

    /**
     * Gets the all.
     *
     * @return the all
     */
    @Override
    @Transactional(readOnly = true)
    public List<Websocket> getAll() {
        return websocketRepository.findAll();
    }

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Websocket> findAll(Pageable pageable) {
        return websocketRepository.findAll(pageable);
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the websocket
     */
    @Override
    @Transactional(readOnly = true)
    public Websocket findById(Long id) {
        Optional<Websocket> extension = websocketRepository.findById(id);
        if (extension.isPresent()) {
            return extension.get();
        } else {
            throw new IllegalArgumentException("Websocket with id does not exist: " + id);
        }
    }

    /**
     * Find by name.
     *
     * @param name the name
     * @return the websocket
     */
    @Override
    @Transactional(readOnly = true)
    public Websocket findByName(String name) {
        Websocket filter = new Websocket();
        filter.setName(name);
        Example<Websocket> example = Example.of(filter);
        Optional<Websocket> extension = websocketRepository.findOne(example);
        if (extension.isPresent()) {
            return extension.get();
        } else {
            throw new IllegalArgumentException("Websocket with name does not exist: " + name);
        }
    }
    
    /**
     * Find by key.
     *
     * @param key the key
     * @return the websocket
     */
    @Override
    @Transactional(readOnly = true)
    public Websocket findByKey(String key) {
    	Websocket filter = new Websocket();
        filter.setKey(key);
        Example<Websocket> example = Example.of(filter);
        Optional<Websocket> websocket = websocketRepository.findOne(example);
        if (websocket.isPresent()) {
            return websocket.get();
        }
        return null;
    }

    /**
     * Save.
     *
     * @param extension the extension
     * @return the websocket
     */
    @Override
    public Websocket save(Websocket extension) {
        return websocketRepository.saveAndFlush(extension);
    }

    /**
     * Delete.
     *
     * @param extension the extension
     */
    @Override
    public void delete(Websocket extension) {
        websocketRepository.delete(extension);
    }
}
