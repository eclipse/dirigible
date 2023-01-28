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
package org.eclipse.dirigible.components.data.sources.endpoint;

import java.util.List;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
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

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * The Class DataSourceEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_DATA + "sources")
public class DataSourceEndpoint extends BaseEndpoint {

	/** The data source service. */
	private final DataSourceService datasourceService;

	/**
	 * Instantiates a new data source endpoint.
	 *
	 * @param datasourceService the datasource service
	 */
	@Autowired
	public DataSourceEndpoint(DataSourceService datasourceService) {
		this.datasourceService = datasourceService;
	}

	/**
	 * Find all.
	 *
	 * @param size the size
	 * @param page the page
	 * @return the page
	 */
	@GetMapping("/pages")
	public Page<DataSource> findAll(
			@Parameter(description = "The size of the page to be returned") @RequestParam(required = false) Integer size,
			@Parameter(description = "Zero-based page index") @RequestParam(required = false) Integer page) {
		if (size == null) {
			size = DEFAULT_PAGE_SIZE;
		}
		if (page == null) {
			page = 0;
		}
		Pageable pageable = PageRequest.of(page, size);
		Page<DataSource> tables = datasourceService.getPages(pageable);
		return tables;
	}

	/**
	 * Gets the.
	 *
	 * @param id the id
	 * @return the response entity
	 */
	@GetMapping("/{id}")
	public ResponseEntity<DataSource> get(
			@ApiParam(value = "Id of the DataSource", required = true) @PathVariable("id") Long id) {
		return ResponseEntity.ok(datasourceService.findById(id));
	}
	
	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the response entity
	 */
	@GetMapping("/search")
	public ResponseEntity<DataSource> findByName(
			@ApiParam(value = "Name of the DataSource", required = true) @RequestParam("name") String name) {
		return ResponseEntity.ok(datasourceService.findByName(name));
	}
	
	/**
	 * Gets the all.
	 *
	 * @return the all
	 */
	@GetMapping
	public ResponseEntity<List<DataSource>> getAll() {
		return ResponseEntity.ok(datasourceService.getAll());
	}

}
