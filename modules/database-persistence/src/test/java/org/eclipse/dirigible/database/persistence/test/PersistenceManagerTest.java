package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.squle.Squle;
import org.junit.Before;
import org.junit.Test;

public class PersistenceManagerTest {
	
	DataSource dataSrouce = null;
	
	@Before
	public void setUp() {
		try {
			DerbyDatabase derbyDatabase = new DerbyDatabase();
			this.dataSrouce = derbyDatabase.getDataSource("target/persitence_tests");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void crud() throws SQLException {
		PersistenceManager persistenceManager = new PersistenceManager();
		Connection connection = this.dataSrouce.getConnection();
		try {
			createTableForPojo(connection, persistenceManager);
			insertPojo(connection, persistenceManager);
			selectPojo(connection, persistenceManager);
//			deletePojo();
			dropTableForPojo(connection, persistenceManager);
		} finally {
			connection.close();
		}
	}
	
	public void createTableForPojo(Connection connection, PersistenceManager persistenceManager) throws SQLException {
		persistenceManager.createTable(connection, Customer.class);
	}
	
	public void insertPojo(Connection connection, PersistenceManager persistenceManager) throws SQLException {
		Customer customer = new Customer();
		customer.setFirstName("John");
		customer.setLastName("Smith");
		customer.setAge(33);
		persistenceManager.insert(connection, customer);
	}
	
	public void selectPojo(Connection connection, PersistenceManager persistenceManager) throws SQLException {
		
		// IN PROGRESS...
		
		String sql = Squle.getNative(Squle.deriveDialect(connection))
				.select()
				.column("*")
				.from("CUSTOMERS")
				.toString();
		
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		try {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String firstName = resultSet.getString("CUSTOMER_FIRST_NAME");
				assertEquals("John", firstName);
			}
		} finally {
			preparedStatement.close();
		}
		
	}
	
	public void dropTableForPojo(Connection connection, PersistenceManager persistenceManager) throws SQLException {
		persistenceManager.dropTable(connection, Customer.class);
	}

}
