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
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.eclipse.dirigible.database.ds.model.DataStructureDataReplaceModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureDataReplaceTest.
 */
public class DataStructureDataReplaceTest extends AbstractGuiceTest {

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
	 * Replace data
	 */
	@Test
	public void replaceData() {
		try {
			String dataFile = IOUtils.toString(DataStructureDataReplaceTest.class.getResourceAsStream("/orders.replace"), StandardCharsets.UTF_8);
			DataStructureDataReplaceModel data = DataStructureModelFactory.parseReplace("/orders.replace", dataFile);
			assertEquals("1|Order 1|11.11", data.getContent());
			Connection connection = null;
			try {
				connection = dataSource.getConnection();

				PersistenceManager<Order> persistenceManager = new PersistenceManager<Order>();
				if (!persistenceManager.tableExists(connection, Order.class)) {
					persistenceManager.tableCreate(connection, Order.class);
				}

				dataStructuresSynchronizer.executeReplaceUpdate(data);

				Order order = persistenceManager.find(connection, Order.class, 1);

				assertEquals("Order 1", order.getSubject());

				persistenceManager.tableDrop(connection, Order.class);
				boolean exists = persistenceManager.tableExists(connection, Order.class);
				assertFalse(exists);

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
