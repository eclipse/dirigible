package org.eclipse.dirigible.runtime.ide.databases.helpers.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.eclipse.dirigible.runtime.ide.databases.helpers.DatabaseMetadataHelper;
import org.eclipse.dirigible.runtime.ide.databases.helpers.DatabaseMetadataHelper.ColumnsIteratorCallback;
import org.eclipse.dirigible.runtime.ide.databases.helpers.DatabaseMetadataHelper.IndicesIteratorCallback;
import org.junit.Before;
import org.junit.Test;

public class DatabaseMetadataHelperTest {
	
	private DataSource dataSrouce = null;
	
	@Before
	public void setUp() {
		try {
			DerbyDatabase derbyDatabase = new DerbyDatabase();
			this.dataSrouce = derbyDatabase.getDataSource("target/tests/derby");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public DataSource getDataSrouce() {
		return dataSrouce;
	}
	
	@Test
	public void listSchemaNamesTest() throws SQLException {
		Connection connection = dataSrouce.getConnection();
		try {
			List<String> shemaNames = DatabaseMetadataHelper.listSchemaNames(connection, null, null);
			assertTrue(shemaNames.contains("APP"));
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	@Test
	public void listTableNamesTest() throws SQLException {
		Connection connection = dataSrouce.getConnection();
		try {
			List<String> tableNames = DatabaseMetadataHelper.listTableNames(connection, null, "SYS", null);
			assertTrue(tableNames.contains("SYSKEYS"));
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}
	
	@Test
	public void iterateTableDefinitionTest() throws SQLException {
		Connection connection = dataSrouce.getConnection();
		try {
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
	
	@Test
	public void getAsJsonTest() throws SQLException {
		Connection connection = dataSrouce.getConnection();
		try {
			String metadata = DatabaseMetadataHelper.getAsJson(connection, null, null, null);
			assertNotNull(metadata);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
