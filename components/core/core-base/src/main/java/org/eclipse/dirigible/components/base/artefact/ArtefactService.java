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

/**
 * The Interface ArtefactService.
 *
 * @param <A> the generic type
 */
public interface ArtefactService<A extends Artefact> {

    /**
     * Find all.
     *
     * @return the page
     */
    public List<A> getAll();

    /**
     * Find all.
     *
     * @param pageable the pageable
     * @return the page
     */
    public Page<A> getPages(Pageable pageable);

    /**
     * Find by id.
     *
     * @param id the id
     * @return the a
     */
    public A findById(Long id);

    /**
     * Find by name.
     *
     * @param name the name
     * @return the a
     */
    public A findByName(String name);

    /**
     * Find by location.
     *
     * @param location the location
     * @return the a
     */
    public List<A> findByLocation(String location);

    /**
     * Find by key.
     *
     * @param key the key
     * @return the a
     */
    public A findByKey(String key);

    /**
     * Save.
     *
     * @param a the a
     * @return the a
     */
    public A save(A a);

    /**
     * Delete.
     *
     * @param a the a
     */
    public void delete(A a);

}
