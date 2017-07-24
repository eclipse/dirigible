package org.eclipse.dirigible.database.api.metadata.test;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.junit.Before;
import org.junit.Test;

public class DatabaseMetadataSerializerTest {
	
	private DataSource dataSource = null;
	
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
	
	@Test
	public void serializeToJson() throws SQLException {
		Connection connection = dataSource.getConnection();
		try {
			DatabaseMetadata database = new DatabaseMetadata(connection, null, null, null);
			String json = GsonHelper.GSON.toJson(database);
			System.out.println(json);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
