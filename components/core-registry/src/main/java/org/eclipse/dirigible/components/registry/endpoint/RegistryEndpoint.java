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
package org.eclipse.dirigible.components.registry.endpoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.eclipse.dirigible.api.v3.platform.RegistryFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.components.base.BaseEndpoint;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "registry")
public class RegistryEndpoint extends BaseEndpoint {
	
	
	private final RegistryService registryService;
	
	@Autowired
	public RegistryEndpoint(RegistryService registryService) {
		this.registryService = registryService;
	}
	
	@ApiOperation(value = "Returns the Registry Resource requested by its path", nickname = "get", notes = "", response = Object.class, tags = {
			"Extensions Points", })
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "The Registry Resource", response = Object.class),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Resource with the requested path does not exist"),
			@ApiResponse(code = 500, message = "Internal Server Error")})
	@GetMapping("/{*path}")
	public ResponseEntity<?> get(
			@ApiParam(value = "Location of the Resource", required = true) @PathVariable("path") String path) {
		
		final HttpHeaders httpHeaders= new HttpHeaders();
		
		IResource resource = registryService.getResource(path);
		if (!resource.exists()) {
			ICollection collection = registryService.getCollection(path);
			if (!collection.exists()) {
				byte[] content;
				try {
					content = RegistryFacade.getContent(path);
				} catch (ScriptingException e) {
					throw new InternalServerErrorException(path, e);
				} catch (IOException e) {
					throw new NotFoundException(path, e);
				}
				if (content != null) {
					httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					return new ResponseEntity(content, httpHeaders, HttpStatus.OK);
				}
				throw new NotFoundException(path);
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
