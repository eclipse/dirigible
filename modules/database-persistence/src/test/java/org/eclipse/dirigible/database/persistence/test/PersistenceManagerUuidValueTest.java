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
import java.util.UUID;

import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Test;

public class PersistenceManagerUuidValueTest extends AbstractPersistenceManagerTest {

	@Test
	public void orderedCrudTests() throws SQLException {
		PersistenceManager<Task> persistenceManager = new PersistenceManager<Task>();
		Connection connection = getDataSrouce().getConnection();
		try {
			// create table
			createTableForPojo(connection, persistenceManager);
			// check whether it is created successfully
			assertTrue(existsTable(connection, persistenceManager));
			// insert a record in the table for a pojo
			insertPojo(connection, persistenceManager);
			// get the list of all the records
			findAllPojo(connection, persistenceManager);
			// drop the table
			dropTableForPojo(connection, persistenceManager);
		} finally {
			connection.close();
		}
	}

	public void createTableForPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Task.class);
	}

	public boolean existsTable(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Task.class);
	}

	public void insertPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		Task task = new Task();
		String uuid = UUID.randomUUID().toString();
		task.setId(uuid);
		task.setSubject("Subject 1");
		String uuidInserted = persistenceManager.insert(connection, task).toString();
		assertEquals(uuid, uuidInserted);
	}

	public void findAllPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		List<Task> list = persistenceManager.findAll(connection, Task.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		Task task = list.get(0);

		assertEquals("Subject 1", task.getSubject());

		System.out.println(task.getId());

	}

	public void dropTableForPojo(Connection connection, PersistenceManager<Task> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Task.class);
	}

}
