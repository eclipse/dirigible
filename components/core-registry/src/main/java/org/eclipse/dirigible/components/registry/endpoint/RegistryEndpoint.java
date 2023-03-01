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
package org.eclipse.dirigible.components.registry.endpoint;

import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.registry.accessor.RegistryAccessor;
import org.eclipse.dirigible.components.registry.service.RegistryService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "registry")
public class RegistryEndpoint extends BaseEndpoint {
	
	private final RegistryService registryService;
	
	private final RegistryAccessor registryAccessor;
	
	@Autowired
	public RegistryEndpoint(RegistryService registryService, RegistryAccessor registryAccessor) {
		this.registryService = registryService;
		this.registryAccessor = registryAccessor;
	}
	
	@GetMapping("/{*path}")
	public ResponseEntity<?> get(@PathVariable("path") String path) {
		
		final HttpHeaders httpHeaders = new HttpHeaders();
		
		IResource resource = registryService.getResource(path);
		if (!resource.exists()) {
			ICollection collection = registryService.getCollection(path);
			if (!collection.exists()) {
				byte[] content;
				try {
					content = registryAccessor.getRegistryContent(path);
				} catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, path, e);
				}
				if (content != null) {
					httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					return new ResponseEntity(content, httpHeaders, HttpStatus.OK);
				}
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, path);
			}
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(registryService.renderRegistry(collection), httpHeaders, HttpStatus.OK);
		}
		if (resource.isBinary()) {
			httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity(resource.getContent(), httpHeaders, HttpStatus.OK);
		}
		
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity(new String(resource.getContent(), StandardCharsets.UTF_8), httpHeaders, HttpStatus.OK);
	}

}
