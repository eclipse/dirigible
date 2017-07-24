package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.junit.Before;

public class AbstractPersistenceManagerTest {
	
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
	
	public DataSource getDataSrouce() {
		return dataSource;
	}

}
