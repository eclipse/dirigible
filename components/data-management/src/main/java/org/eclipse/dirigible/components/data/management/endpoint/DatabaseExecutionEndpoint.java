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

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.components.base.endpoint.BaseEndpoint;
import org.eclipse.dirigible.components.data.management.service.DatabaseExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

/**
 * The Class DatabaseExecutionEndpoint.
 */
@RestController
@RequestMapping(BaseEndpoint.PREFIX_ENDPOINT_DATA)
public class DatabaseExecutionEndpoint {
	
	/** The database execution service. */
	private final DatabaseExecutionService databaseExecutionService;

	/**
	 * Instantiates a new data source metadata endpoint.
	 *
	 * @param databaseExecutionService the database execution service
	 */
	@Autowired
	public DatabaseExecutionEndpoint(DatabaseExecutionService databaseExecutionService) {
		this.databaseExecutionService = databaseExecutionService;
	}

	/**
	 * Gets the supported message.
	 *
	 * @return the message
	 */
	@GetMapping
	public ResponseEntity<String> getSupported() {
		return ResponseEntity.ok("Only POST requests are supported");
	}
	
	/**
	 * Query statement.
	 *
	 * @param datasource the datasource
	 * @param sql the sql
	 * @param accept the accept
	 * @return the data sources
	 * @throws SQLException the SQL exception
	 */
	@PostMapping("/query/{datasource}")
	public ResponseEntity<String> executeQuery(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("datasource") String datasource,
			@RequestBody String sql, @RequestHeader(HttpHeaders.ACCEPT) String accept) throws SQLException {
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = databaseExecutionService.executeQuery(datasource, sql, false, false);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = databaseExecutionService.executeQuery(datasource, sql, false, true);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}
		String result = databaseExecutionService.executeQuery(datasource, sql, true, false);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
	}
	
	/**
	 * Update statement.
	 *
	 * @param datasource the datasource
	 * @param sql the sql
	 * @param accept the accept
	 * @return the data sources
	 * @throws SQLException the SQL exception
	 */
	@PostMapping("/update/{datasource}")
	public ResponseEntity<String> executeUpdate(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("datasource") String datasource,
			@RequestBody String sql, @RequestHeader(HttpHeaders.ACCEPT) String accept) throws SQLException {
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = databaseExecutionService.executeUpdate(datasource, sql, false, false);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = databaseExecutionService.executeUpdate(datasource, sql, false, true);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}
		String result = databaseExecutionService.executeUpdate(datasource, sql, true, false);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
	}
	
	/**
	 * Update statement.
	 *
	 * @param datasource the datasource
	 * @param sql the sql
	 * @param accept the accept
	 * @return the data sources
	 * @throws SQLException the SQL exception
	 */
	@PostMapping("/procedure/{datasource}")
	public ResponseEntity<String> executeProcedure(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("datasource") String datasource,
			@RequestBody String sql, @RequestHeader(HttpHeaders.ACCEPT) String accept) throws SQLException {
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = databaseExecutionService.executeProcedure(datasource, sql, false, false);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = databaseExecutionService.executeProcedure(datasource, sql, false, true);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}
		String result = databaseExecutionService.executeProcedure(datasource, sql, true, false);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
	}
	
	/**
	 * Execute statement.
	 *
	 * @param datasource the datasource
	 * @param sql the sql
	 * @param accept the accept
	 * @return the data sources
	 * @throws SQLException the SQL exception
	 */
	@PostMapping("/execute/{datasource}")
	public ResponseEntity<String> execute(
			@ApiParam(value = "Name of the DataSource", required = true) @PathVariable("datasource") String datasource,
			@RequestBody String sql, @RequestHeader(HttpHeaders.ACCEPT) String accept) throws SQLException {
		if (ContentTypeHelper.TEXT_PLAIN.equals(accept)) {
			String result = databaseExecutionService.execute(datasource, sql, false, false);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}  else if (ContentTypeHelper.TEXT_CSV.equals(accept)) {
			String result = databaseExecutionService.execute(datasource, sql, false, true);
			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
		}
		String result = databaseExecutionService.execute(datasource, sql, true, false);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
	}
	
}
