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
package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Schema Metadata transport object.
 */
public class SchemaMetadata {
	
	private static final Logger logger = LoggerFactory.getLogger(SchemaMetadata.class);

	private String name;

	private String kind = "schema";

	private List<TableMetadata> tables;
	
	private List<ProcedureMetadata> procedures;
	
	private List<FunctionMetadata> functions;

	/**
	 * Instantiates a new schema metadata.
	 *
	 * @param name
	 *            the name
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param nameFilter
	 *            the name filter
	 * @throws SQLException
	 *             the SQL exception
	 */
	public SchemaMetadata(String name, Connection connection, String catalogName, Filter<String> nameFilter) throws SQLException {
		super();
		
		this.name = name;

		this.tables = DatabaseMetadataHelper.listTables(connection, catalogName, name, nameFilter);
		
		try {
			this.procedures = DatabaseMetadataHelper.listProcedures(connection, catalogName, name, nameFilter);
		} catch (SQLException e) {
			this.procedures = new ArrayList<ProcedureMetadata>();
			logger.error(e.getMessage());
		}
		
		try {
			this.functions = DatabaseMetadataHelper.listFunctions(connection, catalogName, name, nameFilter);
		} catch (SQLException e) {
			this.functions = new ArrayList<FunctionMetadata>();
			logger.error(e.getMessage());
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the tables.
	 *
	 * @return the tables
	 */
	public List<TableMetadata> getTables() {
		return tables;
	}
	
	

	/**
	 * @return the procedures
	 */
	public List<ProcedureMetadata> getProcedures() {
		return procedures;
	}
	
	/**
	 * @return the functions
	 */
	public List<FunctionMetadata> getFunctions() {
		return functions;
	}

	/**
	 * Gets the kind.
	 *
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * Sets the kind.
	 *
	 * @param kind
	 *            the new kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

}
