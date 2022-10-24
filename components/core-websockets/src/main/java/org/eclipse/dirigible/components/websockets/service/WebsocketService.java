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

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.websockets.domain.Websocket;
import org.eclipse.dirigible.components.websockets.repository.WebsocketRepository;
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
public class WebsocketService implements ArtefactService<Websocket> {

    @Autowired
    private WebsocketRepository websocketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Websocket> getAll() {
        return websocketRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Websocket> findAll(Pageable pageable) {
        return websocketRepository.findAll(pageable);
    }

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

    @Override
    public Websocket save(Websocket extension) {
        return websocketRepository.saveAndFlush(extension);
    }

    @Override
    public void delete(Websocket extension) {
        websocketRepository.delete(extension);
    }
}
