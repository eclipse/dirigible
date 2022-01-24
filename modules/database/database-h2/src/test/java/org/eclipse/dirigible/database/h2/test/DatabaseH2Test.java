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
package org.eclipse.dirigible.database.h2.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.api.metadata.SchemaMetadata;
import org.eclipse.dirigible.database.api.metadata.TableMetadata;
import org.eclipse.dirigible.database.h2.H2Database;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.ColumnsIteratorCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.IndicesIteratorCallback;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DatabaseMetadataHelperTest.
 */
public class DatabaseH2Test {

	/** The data srouce. */
	private DataSource dataSource = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			H2Database h2Database = new H2Database();
			this.dataSource = h2Database.getDataSource("DefaultDB");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Gets the data srouce.
	 *
	 * @return the data srouce
	 */
	public DataSource getDataSrouce() {
		return dataSource;
	}

	/**
	 * List schema names test.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void listSchemaNamesTest() throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			List<SchemaMetadata> schemas = DatabaseMetadataHelper.listSchemas(connection, null, null, null);
			for (SchemaMetadata schema : schemas) {
				if ("INFORMATION_SCHEMA".equals(schema.getName())) {
					return;
				}
			}
			fail("No APP schema present");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * List table names test.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void listTableNamesTest() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			List<TableMetadata> tables = DatabaseMetadataHelper.listTables(connection, null, "INFORMATION_SCHEMA", null);
			for (TableMetadata table : tables) {
				if ("USERS".equals(table.getName())) {
					return;
				}
			}
			fail("No USERS table present");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Iterate table definition test.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void iterateTableDefinitionTest() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetadataHelper.iterateTableDefinition(connection, null, "SYS", "SYSKEYS", new ColumnsIteratorCallback() {
				@Override
				public void onColumn(String columnName, String columnType, String columnSize, boolean isNullable, boolean isKey) {
					assertNotNull(columnName);
				}
			}, new IndicesIteratorCallback() {
				@Override
				public void onIndex(String indexName, String indexType, String columnName, boolean isNonUnique, String indexQualifier,
						String ordinalPosition, String sortOrder, String cardinality, String pagesIndex, String filterCondition) {
					assertNotNull(indexName);
				}
			});
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
