package org.eclipse.dirigible.database.api.metadata.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
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
	
	@Test
	public void listTableNamesTest() throws SQLException {
		Connection connection = dataSrouce.getConnection();
		try {
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
	
}
