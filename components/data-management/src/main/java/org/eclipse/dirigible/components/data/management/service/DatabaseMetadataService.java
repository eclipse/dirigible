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
package org.eclipse.dirigible.components.data.management.service;


import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.dirigible.components.data.management.domain.DatabaseStructureTypes;
import org.eclipse.dirigible.components.data.management.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.components.data.sources.domain.DataSource;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class DataSourceMetadataService.
 */
@Service
public class DatabaseMetadataService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseMetadataService.class);
	
	/** The data sources manager. */
	private final DataSourcesManager datasourceManager;
	
	/** The data sources service. */
	private final DataSourceService datasourceService;

	/**
	 * Instantiates a new data source endpoint.
	 *
	 * @param datasourceManager the datasource manager
	 * @param datasourceService the datasource service
	 */
	@Autowired
	public DatabaseMetadataService(DataSourcesManager datasourceManager, DataSourceService datasourceService) {
		this.datasourceManager = datasourceManager;
		this.datasourceService = datasourceService;
	}
	
	/**
	 * Gets the data sources.
	 *
	 * @return the data sources
	 */
	public Set<String> getDataSourcesNames() {
		return datasourceService.getAll().stream().map(DataSource::getName).collect(Collectors.toSet());
	}
	
	/**
	 * Gets the structures.
	 *
	 * @param datasource the datasource
	 * @return the structures
	 * @throws SQLException the SQL exception
	 */
	public String getDataSourceMetadata(String datasource) throws SQLException {
		javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
		String metadata = DatabaseMetadataHelper.getMetadataAsJson(dataSource);
		return metadata;
	}
	
	/**
	 * Exists the structures.
	 *
	 * @param datasource the datasource
	 * @return the structures
	 * @throws SQLException the SQL exception
	 */
	public boolean existsDataSourceMetadata(String datasource) throws SQLException {
		javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
		return dataSource != null;
	}
	
	/**
	 * Gets the schema metadata.
	 *
	 * @param datasource the datasource
	 * @param schema the schema
	 * @return the schema metadata
	 * @throws SQLException the SQL exception
	 */
	public String getSchemaMetadata(String datasource, String schema) throws SQLException {
		javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
		String metadata = DatabaseMetadataHelper.getSchemaMetadataAsJson(dataSource, schema);
		return metadata;
	}
	
	/**
	 * Describe the requested artifact in JSON.
	 *
	 * @param datasource the requested datasource
	 * @param schema the requested schema
	 * @param artifact the requested artifact
	 * @param kind the type of the artifact
	 * @return the JSON representation
	 * @throws SQLException in case of an error
	 */
	public String getStructureMetadata(String datasource, String schema, String artifact, String kind) throws SQLException {
		javax.sql.DataSource dataSource = datasourceManager.getDataSource(datasource);
		String metadata = null;
		if (kind == null) {
			kind = DatabaseStructureTypes.TABLE.name();
		}
		if (artifact != null && !artifact.trim().isEmpty()) {
			DatabaseStructureTypes type = DatabaseStructureTypes.TABLE;
			try {
				type = DatabaseStructureTypes.valueOf(kind);
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {logger.warn("Kind is unknown", e);}
			}
			switch (type) {
				case PROCEDURE: metadata = DatabaseMetadataHelper.getProcedureMetadataAsJson(dataSource, schema, artifact); break;
				case FUNCTION: metadata = DatabaseMetadataHelper.getFunctionMetadataAsJson(dataSource, schema, artifact); break;
				default: metadata = DatabaseMetadataHelper.getTableMetadataAsJson(dataSource, schema, artifact); // TABLE, VIEW
			}
		}
		return metadata;
	}

}
