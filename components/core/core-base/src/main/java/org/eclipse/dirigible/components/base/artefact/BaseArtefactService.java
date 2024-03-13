/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.artefact;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public abstract class BaseArtefactService<A extends Artefact, ID> implements ArtefactService<A, ID> {

    private final ArtefactRepository<A, ID> repository;

    protected BaseArtefactService(ArtefactRepository<A, ID> repository) {
        this.repository = repository;
    }

    @Override
    public final List<A> getAll() {
        return getRepo().findAll();
    }

    @Override
    public final Page<A> getPages(Pageable pageable) {
        return getRepo().findAll(pageable);
    }

    @Override
    public final A findById(ID id) {
        return getRepo().findById(id)
                        .orElseThrow(() -> new IllegalArgumentException(this.getClass() + ": missing artefact with [" + id + "]"));
    }

    @Override
    public A findByName(String name) {
        return getRepo().findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException(this.getClass() + ": missing artefact with name: [" + name + "]"));
    }

    @Override
    public final List<A> findByLocation(String location) {
        return getRepo().findByLocation(location);
    }

    @Override
    public final A findByKey(String key) {
        return getRepo().findByKey(key)
                        .orElse(null);
    }

    @Override
    public A save(A a) {
        return getRepo().saveAndFlush(a);
    }

    @Override
    public void delete(A a) {
        getRepo().delete(a);
    }

    @Override
    public void setRunningToAll(boolean running) {
        getRepo().setRunningToAll(running);
    }

    protected ArtefactRepository<A, ID> getRepo() {
        return repository;
    }

}
