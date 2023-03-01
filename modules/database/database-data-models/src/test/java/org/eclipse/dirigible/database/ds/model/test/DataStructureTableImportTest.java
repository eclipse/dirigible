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
package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.eclipse.dirigible.database.ds.model.transfer.TableImporter;
import org.eclipse.dirigible.database.h2.H2Database;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureTableImportTest.
 */
public class DataStructureTableImportTest {
	
	/** The data source. */
	private DataSource dataSource = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			H2Database h2Database = new H2Database();
			this.dataSource = h2Database.getDataSource("target/h2");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	public DataSource getDataSource() {
		return dataSource;
	}
	
	/**
	 * Parses the table.
	 */
	@Test
	public void createTable() {
		try {
			InputStream in = DataStructureTableImportTest.class.getResourceAsStream("/offers.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("OFFERS", table.getName());
				
				Connection connection = getDataSource().getConnection();
				try {
					ResultSet rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(table.getName()), null);
					if (rs.next()) {
						rs.close();
						fail("Table already exists!");
					}
					try {
						TableCreateProcessor.execute(connection, table);
						rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(table.getName()), null);
						if (!rs.next()) {
							rs.close();
							fail("Table has not been materialized!");
						}
						
						InputStream data = DataStructureTableImportTest.class.getResourceAsStream("/offers.append");
						try {
							String dataFile = IOUtils.toString(data, StandardCharsets.UTF_8);
							TableImporter tableImporter = new TableImporter(dataSource, dataFile.getBytes(), table.getName());
							tableImporter.insert();
							
							rs = connection.prepareStatement("SELECT * FROM OFFERS").executeQuery();
							if (rs.next()) {
								assertEquals(rs.getString(2), "John");
							}
						} catch (Exception e) {
							e.printStackTrace();
							fail(e.getMessage());
						} finally {
							if (data != null) {
								data.close();
							}
							if (rs != null) {
								rs.close();
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						fail(e.getMessage());
					} finally {
						connection.createStatement().executeUpdate("DROP TABLE " + table.getName());
					}
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				} finally {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
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
