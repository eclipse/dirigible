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
	
	@Autowired 
	private ExtensionRepository extensionRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Extension> getAll() {
		return extensionRepository.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Extension> findAll(Pageable pageable) {
		return extensionRepository.findAll(pageable);
	}
	
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
	
	@Override
	public Extension save(Extension extension) {
		return extensionRepository.saveAndFlush(extension);
	}
	
	@Override
	public void delete(Extension extension) {
		extensionRepository.delete(extension);
	}

}
