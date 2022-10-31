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
package org.eclipse.dirigible.components.listeners.service;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.listeners.domain.Listener;
import org.eclipse.dirigible.components.listeners.repository.ListenerRepository;
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
public class ListenerService implements ArtefactService<Listener> {

    @Autowired
    private ListenerRepository listenerRepository;

    @Override
    public List getAll() {
        return listenerRepository.findAll();
    }

    @Override
    public Page findAll(Pageable pageable) {
        return listenerRepository.findAll(pageable);
    }

    @Override
    public Listener findById(Long id) {
        Optional<Listener> listener = listenerRepository.findById(id);
        if (listener.isPresent()) {
            return listener.get();
        } else {
            throw new IllegalArgumentException("Listener with id does not exist: " + id);
        }
    }

    @Override
    public Listener findByName(String name) {
        Listener filter = new Listener();
        filter.setName(name);
        Example<Listener> example = Example.of(filter);
        Optional<Listener> listener = listenerRepository.findOne(example);
        if (listener.isPresent()) {
            return listener.get();
        } else {
            throw new IllegalArgumentException("Listener with name does not exist: " + name);
        }
    }

    @Override
    public Listener save(Listener listener) {
        return listenerRepository.saveAndFlush(listener);
    }

    @Override
    public void delete(Listener listener) {
        listenerRepository.delete(listener);
    }
}
