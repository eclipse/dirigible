/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Test;

/**
 * The Persistence Manager Generated Value Table Test.
 */
public class PersistenceManagerGeneratedValueTableTest extends AbstractPersistenceManagerTest {

	/**
	 * Ordered CRUD tests.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Order> persistenceManager = new PersistenceManager<Order>();
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			// create table
			createTableForPojo(connection, persistenceManager);
			// check whether it is created successfully
			assertTrue(existsTable(connection, persistenceManager));
			// insert a record in the table for a pojo
			insertPojo(connection, persistenceManager);
			// insert a record in the table for a pojo
			insertSecondPojo(connection, persistenceManager);
			// get the list of all the records
			findAllPojo(connection, persistenceManager);
			// drop the table
			dropTableForPojo(connection, persistenceManager);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Creates the table for pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void createTableForPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Order.class);
	}

	/**
	 * Exists table.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @return true, if successful
	 * @throws SQLException
	 *             the SQL exception
	 */
	public boolean existsTable(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Order.class);
	}

	/**
	 * Insert pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void insertPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		Order order = new Order();
		order.setSubject("Subject 1");
		persistenceManager.insert(connection, order);
	}

	/**
	 * Insert second pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void insertSecondPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		Order order = new Order();
		order.setSubject("Subject 2");
		persistenceManager.insert(connection, order);
	}

	/**
	 * Find all pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void findAllPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		List<Order> list = persistenceManager.findAll(connection, Order.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Order order = list.get(0);
		assertEquals("Subject 1", order.getSubject());

		System.out.println(order.getId());

	}

	/**
	 * Drop table for pojo.
	 *
	 * @param connection
	 *            the connection
	 * @param persistenceManager
	 *            the persistence manager
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void dropTableForPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Order.class);
	}

}
