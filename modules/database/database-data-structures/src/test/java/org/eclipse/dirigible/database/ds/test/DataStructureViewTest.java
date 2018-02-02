/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureViewTest.
 */
public class DataStructureViewTest extends AbstractGuiceTest {

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
			String viewFile = IOUtils.toString(DataStructureViewTest.class.getResourceAsStream("/orders.view"), StandardCharsets.UTF_8);
			DataStructureViewModel view = DataStructureModelFactory.parseView(viewFile);
			assertEquals("ORDERS_VIEW", view.getName());
			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				PersistenceManager<Order> tablePersistenceManager = new PersistenceManager<Order>();
				tablePersistenceManager.tableCreate(connection, Order.class);

				dataStructuresSynchronizer.executeViewCreate(connection, view);

				PersistenceManager<OrderRO> persistenceManager = new PersistenceManager<OrderRO>();
				boolean exists = persistenceManager.tableExists(connection, OrderRO.class);
				assertTrue(exists);

				dataStructuresSynchronizer.executeViewDrop(connection, view);

				exists = persistenceManager.tableExists(connection, OrderRO.class);
				assertFalse(exists);

				tablePersistenceManager.tableDrop(connection, Order.class);

			} finally {
				if (connection != null) {
					connection.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
