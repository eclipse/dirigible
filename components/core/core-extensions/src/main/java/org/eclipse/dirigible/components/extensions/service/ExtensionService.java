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
package org.eclipse.dirigible.components.extensions.service;

import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.extensions.domain.Extension;
import org.eclipse.dirigible.components.extensions.repository.ExtensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processing the Extensions Service incoming requests.
 */
@Service
@Transactional
public class ExtensionService implements ArtefactService<Extension> {

	/** The extension repository. */
	@Autowired
	private ExtensionRepository extensionRepository;

	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Extension> getAll() {
		return extensionRepository.findAll();
	}

	/**
	 * Find all.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Extension> getPages(Pageable pageable) {
		return extensionRepository.findAll(pageable);
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the extension
	 */
	@Override
	@Transactional(readOnly = true)
	public Extension findById(Long id) {
		Optional<Extension> extension = extensionRepository.findById(id);
		if (extension.isPresent()) {
			return extension.get();
		} else {
			throw new IllegalArgumentException("Extension with id does not exist: " + id);
		}
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the extension
	 */
	@Override
	@Transactional(readOnly = true)
	public Extension findByName(String name) {
		Extension filter = new Extension();
		filter.setName(name);
		Example<Extension> example = Example.of(filter);
		Optional<Extension> extension = extensionRepository.findOne(example);
		if (extension.isPresent()) {
			return extension.get();
		} else {
			throw new IllegalArgumentException("Extension with name does not exist: " + name);
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
	public List<Extension> findByLocation(String location) {
		Extension filter = new Extension();
		filter.setLocation(location);
		Example<Extension> example = Example.of(filter);
		List<Extension> list = extensionRepository.findAll(example);
		return list;
	}

	/**
	 * Find by key.
	 *
	 * @param key the key
	 * @return the extension point
	 */
	@Override
	@Transactional(readOnly = true)
	public Extension findByKey(String key) {
		Extension filter = new Extension();
		filter.setKey(key);
		Example<Extension> example = Example.of(filter);
		Optional<Extension> extension = extensionRepository.findOne(example);
		if (extension.isPresent()) {
			return extension.get();
		}
		return null;
	}

	/**
	 * Find by extension point.
	 *
	 * @param extensionPoint the extension point
	 * @return the extension
	 */
	@Transactional(readOnly = true)
	public List<Extension> findByExtensionPoint(String extensionPoint) {
		Extension filter = new Extension();
		filter.setExtensionPoint(extensionPoint);
		Example<Extension> example = Example.of(filter);
		return extensionRepository.findAll(example);
	}

	/**
	 * Save.
	 *
	 * @param extension the extension
	 * @return the extension
	 */
	@Override
	public Extension save(Extension extension) {
		return extensionRepository.saveAndFlush(extension);
	}

	/**
	 * Delete.
	 *
	 * @param extension the extension
	 */
	@Override
	public void delete(Extension extension) {
		extensionRepository.delete(extension);
	}

}
