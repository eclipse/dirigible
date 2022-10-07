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
package org.eclipse.dirigible.components.extensions.endpoint;

import org.eclipse.dirigible.components.base.BaseEndpoint;
import org.eclipse.dirigible.components.extensions.domain.ExtensionPoint;
import org.eclipse.dirigible.components.extensions.service.ExtensionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_CORE + "extensionpoints")
public class ExtensionsEndpoint extends BaseEndpoint {

	private final ExtensionPointService extensionPointService;

	@Autowired
	public ExtensionsEndpoint(ExtensionPointService extensionPointService) {
		this.extensionPointService = extensionPointService;
	}

	@ApiOperation(value = "Returns all the Extension Points", nickname = "findAll", notes = "", response = ExtensionPoint.class, tags = {
			"Extensions Points", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The Extension Points", response = ExtensionPoint.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Extension Points do not exist") })
	@GetMapping("")
	public Page<ExtensionPoint> findAll(
			@Parameter(description = "The size of the page to be returned") @RequestParam(required = false) Integer size,
			@Parameter(description = "Zero-based page index") @RequestParam(required = false) Integer page) {
		
		if (size == null) {
			size = DEFAULT_PAGE_SIZE;
		}
		if (page == null) {
			page = 0;
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<ExtensionPoint> extensionPoints = extensionPointService.findAll(pageable);
		return extensionPoints;

	}

	@ApiOperation(value = "Returns the Extension Point with their Extensions requested by its id", nickname = "get", notes = "", response = ExtensionPoint.class, tags = {
			"Extensions Points", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The Extension Point", response = ExtensionPoint.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Extension Point with the requested id does not exist") })
	@GetMapping("/{id}")
	public ResponseEntity<ExtensionPoint> get(
			@ApiParam(value = "Id of the ExtensionPoint", required = true) @PathVariable("id") Long id) {

		return ResponseEntity.ok(extensionPointService.findById(id));

	}
	
	@ApiOperation(value = "Returns the Extension Point with their Extensions requested by its name", nickname = "findByName", notes = "", response = ExtensionPoint.class, tags = {
			"Extensions Points", })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The Extension Point", response = ExtensionPoint.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Extension Point with the requested id does not exist") })
	@GetMapping("/search")
	public ResponseEntity<ExtensionPoint> findByName(
			@ApiParam(value = "Name of the ExtensionPoint", required = true) @RequestParam("name") String name) {

		return ResponseEntity.ok(extensionPointService.findByName(name));

	}

}
