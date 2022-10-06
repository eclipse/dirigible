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

import java.util.Optional;

import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.eclipse.dirigible.components.extensions.repository.ExtensionPointRepository;
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
public class ExtensionPointService {
	
	@Autowired 
	private ExtensionPointRepository extensionPointRepository;

	@Transactional(readOnly = true)
	public Page<ExtensionPoint> findAll(Pageable pageable) {
		return extensionPointRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public ExtensionPoint findByName(String name) {
		Optional<ExtensionPoint> extensionPoint = extensionPointRepository.findById(name);
		if (extensionPoint.isPresent()) {
			return extensionPoint.get();
		} else {
			throw new IllegalArgumentException("ExtensionPoint does not exist: " + name);
		}
	}
	
	public ExtensionPoint save(ExtensionPoint extensionPoint) {
		return extensionPointRepository.saveAndFlush(extensionPoint);
	}
	
	public void delete(ExtensionPoint extensionPoint) {
		extensionPointRepository.delete(extensionPoint);
	}

}
