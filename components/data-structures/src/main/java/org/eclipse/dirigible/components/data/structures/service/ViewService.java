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
package org.eclipse.dirigible.components.data.structures.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.data.structures.domain.View;
import org.eclipse.dirigible.components.data.structures.repository.ViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Views Service incoming requests.
 */
@Service
@Transactional
public class ViewService implements ArtefactService<View> {
	
	/** The view repository. */
	@Autowired 
	private ViewRepository viewRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<View> getAll() {
		return viewRepository.findAll();
	}
	
	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<View> getPages(Pageable pageable) {
		return viewRepository.findAll(pageable);
	}
	
	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the view
	 */
	@Override
	@Transactional(readOnly = true)
	public View findById(Long id) {
		Optional<View> view = viewRepository.findById(id);
		if (view.isPresent()) {
			return view.get();
		} else {
			throw new IllegalArgumentException("View with id does not exist: " + id);
		}
	}
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the view
	 */
	@Override
	@Transactional(readOnly = true)
	public View findByName(String name) {
		View filter = new View();
		filter.setName(name);
		Example<View> example = Example.of(filter);
		Optional<View> view = viewRepository.findOne(example);
		if (view.isPresent()) {
			return view.get();
		} else {
			throw new IllegalArgumentException("View with name does not exist: " + name);
		}
	}
	
	/**
     * Find by key.
     *
     * @param key the key
     * @return the view
     */
    @Override
    @Transactional(readOnly = true)
    public View findByKey(String key) {
    	View filter = new View();
        filter.setKey(key);
        Example<View> example = Example.of(filter);
        Optional<View> view = viewRepository.findOne(example);
        if (view.isPresent()) {
            return view.get();
        }
        return null;
    }
	
	/**
	 * Save.
	 *
	 * @param view the view
	 * @return the view
	 */
	@Override
	public View save(View view) {
		return viewRepository.saveAndFlush(view);
	}
	
	/**
	 * Delete.
	 *
	 * @param view the view
	 */
	@Override
	public void delete(View view) {
		viewRepository.delete(view);
	}

}
