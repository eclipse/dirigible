/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.api.metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.Filter;

/**
 * The Schema Metadata transport object.
 */
public class SchemaMetadata {

	private String name;

	private String kind = "schema";

	private List<TableMetadata> tables;

	/**
	 * Instantiates a new schema metadata.
	 *
	 * @param name
	 *            the name
	 * @param connection
	 *            the connection
	 * @param catalogName
	 *            the catalog name
	 * @param tableNameFilter
	 *            the table name filter
	 * @throws SQLException
	 *             the SQL exception
	 */
	public SchemaMetadata(String name, Connection connection, String catalogName, Filter<String> tableNameFilter) throws SQLException {
		super();
		this.name = name;

		this.tables = DatabaseMetadataHelper.listTables(connection, catalogName, name, tableNameFilter);
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
