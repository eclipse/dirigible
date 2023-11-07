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
package org.eclipse.dirigible.components.security.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.security.domain.Access;
import org.eclipse.dirigible.components.security.repository.AccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class SecurityAccessService.
 */
@Service
@Transactional
public class AccessService implements ArtefactService<Access> {

	/**
	 * The security access repository.
	 */
	@Autowired
	private AccessRepository accessRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Access> getAll() {
		return accessRepository.findAll();
	}

	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Access> getPages(Pageable pageable) {
		return accessRepository.findAll(pageable);
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the security access
	 */
	@Override
	@Transactional(readOnly = true)
	public Access findById(Long id) {
		Optional<Access> securityAccess = accessRepository.findById(id);
		if (securityAccess.isPresent()) {
			return securityAccess.get();
		} else {
			throw new IllegalArgumentException("Access with id does not exist: " + id);
		}
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the security access
	 */
	@Override
	@Transactional(readOnly = true)
	public Access findByName(String name) {
		Access filter = new Access();
		filter.setName(name);
		Example<Access> example = Example.of(filter);
		Optional<Access> access = accessRepository.findOne(example);
		if (access.isPresent()) {
			return access.get();
		} else {
			throw new IllegalArgumentException("Access with name does not exist: " + name);
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
	public List<Access> findByLocation(String location) {
		Access filter = new Access();
		filter.setLocation(location);
		Example<Access> example = Example.of(filter);
		List<Access> list = accessRepository.findAll(example);
		return list;
	}

	/**
	 * Find by key.
	 *
	 * @param key the key
	 * @return the security access
	 */
	@Override
	@Transactional(readOnly = true)
	public Access findByKey(String key) {
		Access filter = new Access();
		filter.setKey(key);
		Example<Access> example = Example.of(filter);
		Optional<Access> access = accessRepository.findOne(example);
		if (access.isPresent()) {
			return access.get();
		}
		return null;
	}

	/**
	 * Save.
	 *
	 * @param securityAccess the security access
	 * @return the security access
	 */
	@Override
	public Access save(Access securityAccess) {
		return accessRepository.saveAndFlush(securityAccess);
	}

	/**
	 * Delete.
	 *
	 * @param securityAccess the security access
	 */
	@Override
	public void delete(Access securityAccess) {
		accessRepository.delete(securityAccess);
	}

}
