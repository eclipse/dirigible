/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureViewTest.
 */
public class DataStructureViewTest extends AbstractDirigibleTest {

	/** The data structure core service. */
	private DataStructuresSynchronizer dataStructuresSynchronizer;

	/** The datasource */
	private DataSource dataSource;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.dataStructuresSynchronizer = new DataStructuresSynchronizer();
		this.dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
	}

	/**
	 * Parses the precision scale.
	 */
	@Test
	public void updateTable() {
		try {
			InputStream in = DataStructureViewTest.class.getResourceAsStream("/orders.view");
			try {
				String viewFile = IOUtils.toString(in, StandardCharsets.UTF_8);
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
