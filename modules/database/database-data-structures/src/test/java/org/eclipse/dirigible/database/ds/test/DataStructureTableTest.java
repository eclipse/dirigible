/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.ds.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureTableTest.
 */
public class DataStructureTableTest extends AbstractGuiceTest {

	/** The data structure core service. */
	@Inject
	private DataStructuresSynchronizer dataStructuresSynchronizer;

	/** The datasource */
	@Inject
	private DataSource dataSource;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.dataStructuresSynchronizer = getInjector().getInstance(DataStructuresSynchronizer.class);
		this.dataSource = getInjector().getInstance(DataSource.class);
	}

	/**
	 * Parses the precision scale.
	 */
	@Test
	public void updateTable() {
		try {
			InputStream in = DataStructureTableTest.class.getResourceAsStream("/orders.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("ORDERS", table.getName());
				Connection connection = null;
				try {
					connection = dataSource.getConnection();

					PersistenceManager<Order> persistenceManager = new PersistenceManager<Order>();

					if (persistenceManager.tableExists(connection, Order.class)) {
						persistenceManager.tableDrop(connection, Order.class);
					}

					dataStructuresSynchronizer.executeTableUpdate(connection, table);

					boolean exists = persistenceManager.tableExists(connection, Order.class);
					assertTrue(exists);

					dataStructuresSynchronizer.executeTableDrop(connection, table);

					exists = persistenceManager.tableExists(connection, Order.class);
					assertFalse(exists);

				} finally {
					if (connection != null) {
						connection.close();
					}
				} 
			} finally {
				if (in != null) {
					in.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
