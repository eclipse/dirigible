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
package org.eclipse.dirigible.components.engine.bpm.flowable.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.engine.bpm.flowable.domain.Bpmn;
import org.eclipse.dirigible.components.engine.bpm.flowable.repository.BpmnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class BpmnService.
 */
@Service
@Transactional
public class BpmnService implements ArtefactService<Bpmn> {
	
	/** The bpmn repository. */
	@Autowired 
	private BpmnRepository bpmnRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Bpmn> getAll() {
		return bpmnRepository.findAll();
	}
	
	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Bpmn> getPages(Pageable pageable) {
		return bpmnRepository.findAll(pageable);
	}
	
	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the bpmn
	 */
	@Override
	@Transactional(readOnly = true)
	public Bpmn findById(Long id) {
		Optional<Bpmn> bpmn = bpmnRepository.findById(id);
		if (bpmn.isPresent()) {
			return bpmn.get();
		} else {
			throw new IllegalArgumentException("Bpmn with id does not exist: " + id);
		}
	}
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the bpmn
	 */
	@Override
	@Transactional(readOnly = true)
	public Bpmn findByName(String name) {
		Bpmn filter = new Bpmn();
		filter.setName(name);
		Example<Bpmn> example = Example.of(filter);
		Optional<Bpmn> bpmn = bpmnRepository.findOne(example);
		if (bpmn.isPresent()) {
			return bpmn.get();
		} else {
			throw new IllegalArgumentException("Bpmn with name does not exist: " + name);
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
    public List<Bpmn> findByLocation(String location) {
    	Bpmn filter = new Bpmn();
        filter.setLocation(location);
        Example<Bpmn> example = Example.of(filter);
        List<Bpmn> list = bpmnRepository.findAll(example);
        return list;
    }
	
	/**
     * Find by key.
     *
     * @param key the key
     * @return the bpmn point
     */
    @Override
    @Transactional(readOnly = true)
    public Bpmn findByKey(String key) {
    	Bpmn filter = new Bpmn();
        filter.setKey(key);
        Example<Bpmn> example = Example.of(filter);
        Optional<Bpmn> bpmn = bpmnRepository.findOne(example);
        if (bpmn.isPresent()) {
            return bpmn.get();
        }
        return null;
    }
    
	/**
	 * Save.
	 *
	 * @param bpmn the bpmn
	 * @return the bpmn
	 */
	@Override
	public Bpmn save(Bpmn bpmn) {
		return bpmnRepository.saveAndFlush(bpmn);
	}
	
	/**
	 * Delete.
	 *
	 * @param bpmn the bpmn
	 */
	@Override
	public void delete(Bpmn bpmn) {
		bpmnRepository.delete(bpmn);
	}

}
