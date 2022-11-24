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
package org.eclipse.dirigible.components.data.metadata.endpoint;

import java.sql.SQLException;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.data.metadata.service.DataSourceMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

/**
 * The Class DataSourceMetadataEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_DATA + "metadata")
public class DataSourceMetadataEndpoint extends BaseEndpoint {
	
	/** The data source metadata service. */
	private final DataSourceMetadataService datasourceMetadataService;

	/**
	 * Instantiates a new data source metadata endpoint.
	 *
	 * @param datasourceMetadataService the datasource metadata service
	 */
	@Autowired
	public DataSourceMetadataEndpoint(DataSourceMetadataService datasourceMetadataService) {
		this.datasourceMetadataService = datasourceMetadataService;
	}
	
	/**
	 * Gets the metadata of a schema.
	 *
	 * @param name the name
	 * @param schema the schema
	 * @return the response entity
	 * @throws SQLException the SQL exception
	 */
	@GetMapping("/{name}/{schema}")
	public ResponseEntity<String> getSchemaMetadata(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("name") String name,
			@ApiParam(value = "Schema of the DataSource", required = true) @PathVariable("schema") String schema) throws SQLException {
		
		return ResponseEntity.ok(datasourceMetadataService.getSchemaMetadata(name, schema));
	}
	
	/**
	 * Gets the metadata of a structure.
	 *
	 * @param name the name
	 * @param schema the schema
	 * @param structure the structure
	 * @return the response entity
	 * @throws SQLException the SQL exception
	 */
	@GetMapping("/{name}/{schema}/{structure}")
	public ResponseEntity<String> getStructureMetadata(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("name") String name,
			@ApiParam(value = "Schema of the DataSource", required = true) @PathVariable("schema") String schema,
			@ApiParam(value = "Structure of the DataSource", required = true) @PathVariable("structure") String structure) throws SQLException {
		
		return ResponseEntity.ok(datasourceMetadataService.getStructureMetadata(name, schema, structure));
	}

}
