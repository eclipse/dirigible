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
package org.eclipse.dirigible.database.api.metadata.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.api.metadata.FunctionMetadata;
import org.eclipse.dirigible.database.api.metadata.ProcedureMetadata;
import org.eclipse.dirigible.database.api.metadata.SchemaMetadata;
import org.eclipse.dirigible.database.api.metadata.TableMetadata;
import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.ColumnsIteratorCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.IndicesIteratorCallback;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DatabaseMetadataHelperTest.
 */
public class DatabaseMetadataHelperTest {

	/** The data source. */
	private DataSource dataSource = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			DerbyDatabase derbyDatabase = new DerbyDatabase();
			this.dataSource = derbyDatabase.getDataSource("target/tests/derby");
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
				if ("APP".equals(schema.getName())) {
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
			List<TableMetadata> tables = DatabaseMetadataHelper.listTables(connection, null, "SYS", null);
			for (TableMetadata table : tables) {
				if ("SYSKEYS".equals(table.getName())) {
					return;
				}
			}
			fail("No SYSKEYS table present");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	/**
	 * List procedures names test.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void listProceduresNamesTest() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			
			String sql = "CREATE PROCEDURE TOTAL_REVENUE(IN S_MONTH INTEGER,\n"
					+ "IN S_YEAR INTEGER, OUT TOTAL DECIMAL(10,2))\n"
					+ "PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME \n"
					+ "'com.acme.sales.calculateRevenueByMonth'";
			
			try (PreparedStatement pstms = connection.prepareStatement(sql)) {
				pstms.execute();
			}
			
			List<ProcedureMetadata> procedures = DatabaseMetadataHelper.listProcedures(connection, null, null, null);
			for (ProcedureMetadata procedure : procedures) {
				if ("TOTAL_REVENUE".equals(procedure.getName())) {
					
					try {
						assertEquals(procedure.getColumns().size(), 0);
						ProcedureMetadata procedureMetadata = DatabaseMetadataHelper.describeProcedure(connection, null,
								null, "TOTAL_REVENUE");
						assertEquals(procedureMetadata.getColumns().size(), 3);
					} finally {
						sql = "DROP PROCEDURE TOTAL_REVENUE";
						try (PreparedStatement pstms = connection.prepareStatement(sql)) {
							pstms.execute();
						}
					}
					
					return;
				}
			}
			fail("No TOTAL_REVENUE procedure present");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	/**
	 * List functions names test.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void listFunctionsNamesTest() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			
			String sql = "CREATE FUNCTION PROPERTY_FILE_READER\n"
					+ "( FILENAME VARCHAR( 32672 ) )\n"
					+ "RETURNS TABLE\n"
					+ "  (\n"
					+ "     KEY_COL     VARCHAR( 10 ),\n"
					+ "     VALUE_COL VARCHAR( 1000 )\n"
					+ "  )\n"
					+ "LANGUAGE JAVA\n"
					+ "PARAMETER STYLE DERBY_JDBC_RESULT_SET\n"
					+ "NO SQL\n"
					+ "EXTERNAL NAME 'vtis.example.PropertyFileVTI.propertyFileVTI'";
			
			try (PreparedStatement pstms = connection.prepareStatement(sql)) {
				pstms.execute();
			}
			
			List<FunctionMetadata> functions = DatabaseMetadataHelper.listFunctions(connection, null, null, null);
			for (FunctionMetadata function : functions) {
				if ("PROPERTY_FILE_READER".equals(function.getName())) {
					
					try {
						assertEquals(function.getColumns().size(), 0);
						FunctionMetadata functionMetadata = DatabaseMetadataHelper.describeFunction(connection, null,
								null, "PROPERTY_FILE_READER");
						assertEquals(functionMetadata.getColumns().size(), 3);
					} finally {
						sql = "DROP FUNCTION PROPERTY_FILE_READER";
						try (PreparedStatement pstms = connection.prepareStatement(sql)) {
							pstms.execute();
						}
					}
					
					return;
				}
			}
			fail("No TOTAL_REVENUE procedure present");
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
				public void onColumn(String columnName, String columnType, String columnSize, boolean isNullable, boolean isKey, int scale) {
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
