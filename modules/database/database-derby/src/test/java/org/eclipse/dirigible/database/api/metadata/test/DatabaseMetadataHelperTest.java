/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.api.metadata.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.api.metadata.SchemaMetadata;
import org.eclipse.dirigible.database.api.metadata.TableMetadata;
import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.ColumnsIteratorCallback;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper.IndicesIteratorCallback;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseMetadataHelperTest.
 */
public class DatabaseMetadataHelperTest {

	/** The data srouce. */
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
				public void onColumn(String columnName, String columnType, String columnSize, String isNullable, String isKey) {
					assertNotNull(columnName);
				}
			}, new IndicesIteratorCallback() {
				@Override
				public void onIndex(String indexName, String indexType, String columnName, String isNonUnique, String indexQualifier,
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
