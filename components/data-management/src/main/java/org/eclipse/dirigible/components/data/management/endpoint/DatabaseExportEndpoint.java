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
package org.eclipse.dirigible.components.data.management.endpoint;

import static java.text.MessageFormat.format;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.data.management.service.DatabaseExportService;
import org.eclipse.dirigible.components.data.management.service.DatabaseMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Front facing REST service serving the raw data.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_DATA + "export")
public class DatabaseExportEndpoint {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseExportEndpoint.class);

	/** The database export service. */
	private DatabaseExportService databaseExportService;
	
	/** The database metadata service. */
	private DatabaseMetadataService databaseMetadataService;
	
	@Autowired
	public DatabaseExportEndpoint(DatabaseExportService databaseExportService, DatabaseMetadataService databaseMetadataService) {
		this.databaseExportService = databaseExportService;
		this.databaseMetadataService = databaseMetadataService;
	}
	
	public DatabaseExportService getDatabaseExportService() {
		return databaseExportService;
	}
	
	public DatabaseMetadataService getDatabaseMetadataService() {
		return databaseMetadataService;
	}

	
	/**
	 * Execute artifact export.
	 *
	 * @param datasource the datasource
	 * @param schema the schema name
	 * @param structure the structure name
	 * @return the response
	 * @throws SQLException 
	 */
	@GetMapping(value = "/{datasource}/{schema}/{structure}", produces = "application/octet-stream")
	public ResponseEntity<byte[]> exportArtifact(
			@PathVariable("datasource") String datasource,
			@PathVariable("schema") String schema,
			@PathVariable("structure") String structure) throws SQLException {

		if (!databaseMetadataService.existsDataSourceMetadata(datasource)) {
			String error = format("Datasource {0} does not exist.", datasource);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}
		
		String result = databaseExportService.exportStructure(datasource, schema, structure);
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.add("Content-Disposition", "attachment; filename=\"" + schema + "." + structure + "-" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".csv\"");
		return new ResponseEntity<byte[]>(result.getBytes(), httpHeaders, HttpStatus.OK);
	}
	
	/**
	 * Execute schema export.
	 *
	 * @param datasource the datasource
	 * @param schema the schema name
	 * @return the response
	 * @throws SQLException 
	 */
	@GetMapping(value = "/{datasource}/{schema}", produces = "application/octet-stream")
	public ResponseEntity<byte[]> exportSchema(
			@PathVariable("datasource") String datasource,
			@PathVariable("schema") String schema) throws SQLException {

		if (!databaseMetadataService.existsDataSourceMetadata(datasource)) {
			String error = format("Datasource {0} does not exist.", datasource);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, error);
		}
		
		byte[] result = databaseExportService.exportSchema(datasource, schema);
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.add("Content-Disposition", "attachment; filename=\"" + schema + "-" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + ".zip\"");
		return new ResponseEntity<byte[]>(result, httpHeaders, HttpStatus.OK);
	}

}
