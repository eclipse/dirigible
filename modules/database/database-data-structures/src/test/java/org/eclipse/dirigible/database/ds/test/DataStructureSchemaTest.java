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
import org.eclipse.dirigible.database.ds.model.DataStructureSchemaModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureSchemaTest.
 */
public class DataStructureSchemaTest extends AbstractDirigibleTest {

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
	public void updateSchema() {
		try {
			InputStream in = DataStructureSchemaTest.class.getResourceAsStream("/test.schema");
			try {
				String schemaFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureSchemaModel schema = DataStructureModelFactory.parseSchema("test.schema", schemaFile);
				assertEquals("test", schema.getName());
				Connection connection = null;
				try {
					connection = dataSource.getConnection();

					PersistenceManager<Table1> persistenceManager = new PersistenceManager<Table1>();

					if (persistenceManager.tableExists(connection, Table1.class)) {
						persistenceManager.tableDrop(connection, Table1.class);
					}

					for (DataStructureTableModel table : schema.getTables()) {
//						if ("TABLE2".equals(table.getName())) {
//							dataStructuresSynchronizer.executeTableUpdate(connection, table);
//							break;
//						}
						dataStructuresSynchronizer.executeTableUpdate(connection, table);
					}

					for (DataStructureTableModel table : schema.getTables()) {
//						if ("TABLE1".equals(table.getName())) {
//							dataStructuresSynchronizer.executeTableUpdate(connection, table);
//							break;
//						}
						dataStructuresSynchronizer.executeTableForeignKeysCreate(connection, table);
					}

					boolean exists = persistenceManager.tableExists(connection, Table1.class);
					assertTrue(exists);

					for (DataStructureTableModel table : schema.getTables()) {
						if ("TABLE1".equals(table.getName())) {
							dataStructuresSynchronizer.executeTableDrop(connection, table);
							break;
						}
					}

					for (DataStructureTableModel table : schema.getTables()) {
						dataStructuresSynchronizer.executeTableDrop(connection, table);
					}

					exists = persistenceManager.tableExists(connection, Table1.class);
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
