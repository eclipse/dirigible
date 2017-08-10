/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.squle.Squle;
import org.junit.Test;

public class PersistenceManagerTest extends AbstractPersistenceManagerTest {

	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Customer> persistenceManager = new PersistenceManager<Customer>();
		Connection connection = getDataSrouce().getConnection();
		try {
			// create table
			createTableForPojo(connection, persistenceManager);
			// check whether it is created successfully
			assertTrue(existsTable(connection, persistenceManager));
			// insert a record in the table for a pojo
			insertPojo(connection, persistenceManager);
			// retreive the record by the primary key
			findPojo(connection, persistenceManager);
			// get the list of all the records
			findAllPojo(connection, persistenceManager);
			// make a simple custom query
			queryAll(connection, persistenceManager);
			// make a bit more complicated query
			queryByName(connection, persistenceManager);
			// update one record
			updatePojo(connection, persistenceManager);
			// delete one record
			deletePojo(connection, persistenceManager);
			// drop the table
			dropTableForPojo(connection, persistenceManager);
		} finally {
			connection.close();
		}
	}

	public void createTableForPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Customer.class);
	}

	public boolean existsTable(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Customer.class);
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
		Customer customer = persistenceManager.find(connection, Customer.class, 1);
		assertEquals("John", customer.getFirstName());
	}

	public void findAllPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		List<Customer> list = persistenceManager.findAll(connection, Customer.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Customer customer = list.get(0);
		assertEquals("John", customer.getFirstName());

		insertSecondPojo(connection, persistenceManager);

		list = persistenceManager.findAll(connection, Customer.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		customer = list.get(1);
		assertEquals("Jane", customer.getFirstName());

	}

	public void queryAll(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {

		String sql = Squle.getNative(connection).select().column("*").from("CUSTOMERS").toString();

		List<Customer> list = persistenceManager.query(connection, Customer.class, sql);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Customer customer = list.get(0);
		assertEquals("John", customer.getFirstName());

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		customer = list.get(1);
		assertEquals("Jane", customer.getFirstName());

	}

	public void queryByName(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {

		String sql = Squle.getNative(connection).select().column("*").from("CUSTOMERS").where("CUSTOMER_FIRST_NAME = ?").toString();

		List<Object> values = new ArrayList<Object>();
		values.add("Jane");

		List<Customer> list = persistenceManager.query(connection, Customer.class, sql, values);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Customer customer = list.get(0);
		assertEquals("Jane", customer.getFirstName());

	}

	public void updatePojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		Customer customer = persistenceManager.find(connection, Customer.class, 1);

		assertEquals("John", customer.getFirstName());
		assertEquals("Smith", customer.getLastName());

		customer.setLastName("Wayne");

		int result = persistenceManager.update(connection, customer, 1);

		assertEquals(1, result);

		customer = persistenceManager.find(connection, Customer.class, 1);

		assertEquals("John", customer.getFirstName());
		assertEquals("Wayne", customer.getLastName());
	}

	public void deletePojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		Customer customer = persistenceManager.find(connection, Customer.class, 1);

		assertEquals("John", customer.getFirstName());

		int result = persistenceManager.delete(connection, Customer.class, 1);

		assertEquals(1, result);
	}

	public void dropTableForPojo(Connection connection, PersistenceManager<Customer> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Customer.class);
	}

}
