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

public class PersistenceManagerGeneratedValueSequenceTest extends AbstractPersistenceManagerTest {

	@Test
	public void inquiryedCrudTests() throws SQLException {
		PersistenceManager<Inquiry> persistenceManager = new PersistenceManager<Inquiry>();
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

	public void createTableForPojo(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		persistenceManager.tableCreate(connection, Inquiry.class);
	}

	public boolean existsTable(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		return persistenceManager.tableExists(connection, Inquiry.class);
	}

	public void insertPojo(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		Inquiry inquiry = new Inquiry();
		inquiry.setSubject("Subject 1");
		persistenceManager.insert(connection, inquiry);
	}

	public void insertSecondPojo(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		Inquiry inquiry = new Inquiry();
		inquiry.setSubject("Subject 2");
		persistenceManager.insert(connection, inquiry);
	}

	public void findAllPojo(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		List<Inquiry> list = persistenceManager.findAll(connection, Inquiry.class);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(2, list.size());
		Inquiry inquiry = list.get(0);
		assertEquals("Subject 1", inquiry.getSubject());

		System.out.println(inquiry.getId());

	}

	public void dropTableForPojo(Connection connection, PersistenceManager<Inquiry> persistenceManager) throws SQLException {
		persistenceManager.tableDrop(connection, Inquiry.class);
	}

}
