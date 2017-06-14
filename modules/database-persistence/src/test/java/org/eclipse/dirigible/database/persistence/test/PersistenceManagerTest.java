package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
		Connection connection = this.dataSrouce.getConnection();
		try {
			createTableForPojo(connection, persistenceManager);
			insertPojo(connection, persistenceManager);
			findPojo(connection, persistenceManager);
			findAllPojo(connection, persistenceManager);
			queryAll(connection, persistenceManager);
			queryByName(connection, persistenceManager);
			
//			deletePojo();
			dropTableForPojo(connection, persistenceManager);
		} finally {
			connection.close();
		}
	}
	
	public void createTableForPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		persistenceManager.createTable(connection, Customer.class);
	}
	
	public void insertPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		Customer customer = new Customer();
		customer.setId(1);
		customer.setFirstName("John");
		customer.setLastName("Smith");
		customer.setAge(33);
		persistenceManager.insert(connection, customer);
	}
	
	public void insertSecondPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		Customer customer = new Customer();
		customer.setId(2);
		customer.setFirstName("Jane");
		customer.setLastName("Smith");
		customer.setAge(32);
		persistenceManager.insert(connection, customer);
	}
	
	public void findPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		Object pojo = persistenceManager.find(connection, Customer.class, 1);
		
		assertTrue(pojo instanceof Customer);
		Customer customer = (Customer) pojo;
		assertEquals("John", customer.getFirstName());
		
	}
	
	public void findAllPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		List<Customer> list = persistenceManager.findAll(connection, Customer.class);
		
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Customer pojo = list.get(0);
		assertTrue(pojo instanceof Customer);
		Customer customer = (Customer) pojo;
		assertEquals("John", customer.getFirstName());
		
		insertSecondPojo(connection, persistenceManager);
		
		list = persistenceManager.findAll(connection, Customer.class);
		
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		pojo = list.get(1);
		assertTrue(pojo instanceof Customer);
		customer = (Customer) pojo;
		assertEquals("Jane", customer.getFirstName());
		
	}
	
	public void queryAll(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		
		String sql = Squle.getNative(connection)
				.select()
				.column("*")
				.from("CUSTOMERS")
				.toString();
		
		List<Customer> list = persistenceManager.query(connection, Customer.class, sql, null);
		
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Customer pojo = list.get(0);
		assertTrue(pojo instanceof Customer);
		Customer customer = (Customer) pojo;
		assertEquals("John", customer.getFirstName());
		
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		pojo = list.get(1);
		assertTrue(pojo instanceof Customer);
		customer = (Customer) pojo;
		assertEquals("Jane", customer.getFirstName());
		
	}
	
	public void queryByName(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		
		String sql = Squle.getNative(connection)
				.select()
				.column("*")
				.from("CUSTOMERS")
				.where("CUSTOMER_FIRST_NAME = ?")
				.toString();
		
		
		List<Object> values = new ArrayList<Object>();
		values.add("Jane");
		
		List<Customer> list = persistenceManager.query(connection, Customer.class, sql, values);
		
		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Object pojo = list.get(0);
		assertTrue(pojo instanceof Customer);
		Customer customer = (Customer) pojo;
		assertEquals("Jane", customer.getFirstName());
		
	}
	
	public void dropTableForPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		persistenceManager.dropTable(connection, Customer.class);
	}

}
