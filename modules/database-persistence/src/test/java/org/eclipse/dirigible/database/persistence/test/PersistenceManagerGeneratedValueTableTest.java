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
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Test;

public class PersistenceManagerGeneratedValueTableTest extends AbstractPersistenceManagerTest {

	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Order> persistenceManager = new PersistenceManager<Order>();
		Connection connection = getDataSrouce().getConnection();
		try {
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
			connection.close();
		}
	}

	public void createTableForPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Order.class);
	}

	public boolean existsTable(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Order.class);
	}

	public void insertPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		Order order = new Order();
		order.setSubject("Subject 1");
		persistenceManager.insert(connection, order);
	}

	public void insertSecondPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		Order order = new Order();
		order.setSubject("Subject 2");
		persistenceManager.insert(connection, order);
	}

	public void findAllPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		List<Order> list = persistenceManager.findAll(connection, Order.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Order order = list.get(0);
		assertEquals("Subject 1", order.getSubject());

		System.out.println(order.getId());

	}

	public void dropTableForPojo(Connection connection, PersistenceManager<Order> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Order.class);
	}

}
