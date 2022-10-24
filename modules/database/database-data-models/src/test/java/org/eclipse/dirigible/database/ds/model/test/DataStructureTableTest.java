/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.ds.model.processors.TableAlterProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableForeignKeysCreateProcessor;
import org.eclipse.dirigible.database.h2.H2Database;
import org.eclipse.dirigible.databases.helpers.DatabaseMetadataHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureTableTest.
 */
public class DataStructureTableTest {
	
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
	public void parseTable() {
		try {
			InputStream in = DataStructureTableTest.class.getResourceAsStream("/customers.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("CUSTOMERS", table.getName());
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

	/**
	 * Parses the precision scale.
	 */
	@Test
	public void parsePrecisionScale() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableTest.class.getResourceAsStream("/orders.table"),
					StandardCharsets.UTF_8);
			DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
			assertEquals("ORDERS", table.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Parses the table.
	 */
	@Test
	public void createTable() {
		try {
			InputStream in = DataStructureTableTest.class.getResourceAsStream("/orders.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("ORDERS", table.getName());
				
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
						
						
					} finally {
						connection.createStatement().executeUpdate("DROP TABLE " + table.getName());
					}
				} finally {
					connection.close();
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
	
	/**
	 * Parses the table.
	 */
	@Test
	public void alterTable() {
		try {
			InputStream in = DataStructureTableTest.class.getResourceAsStream("/orders.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("ORDERS", table.getName());
				
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
						InputStream alter = DataStructureTableTest.class.getResourceAsStream("/orders_alter.table");
						try {
							tableFile = IOUtils.toString(alter, StandardCharsets.UTF_8);
							table = DataStructureModelFactory.parseTable(tableFile);
							assertEquals("ORDERS", table.getName());
							
							rs = connection.createStatement().executeQuery("SELECT * FROM ORDERS");
							try {
								assertEquals(2, rs.getMetaData().getColumnCount());
							} finally {
								rs.close();
							}
							
							TableAlterProcessor.execute(connection, table);
							
							rs = connection.createStatement().executeQuery("SELECT * FROM ORDERS");
							try {
								assertEquals(3, rs.getMetaData().getColumnCount());
							} finally {
								rs.close();
							}
							
						} finally {
							if (alter != null) {
								alter.close();
							}
						}
						
					} finally {
						connection.createStatement().executeUpdate("DROP TABLE " + table.getName());
					}
				} finally {
					connection.close();
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
	
	/**
	 * Parses the table.
	 */
	@Test
	public void createCaseSensitiveTable() {
		try {
			InputStream in = DataStructureTableTest.class.getResourceAsStream("/ProjectDetails.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("ProjectDetails", table.getName());
				
				Connection connection = getDataSource().getConnection();
				try {
					ResultSet rs = connection.getMetaData().getTables(null, null, table.getName(), null);
					if (rs.next()) {
						rs.close();
						fail("Table already exists!");
					}
					Configuration.set(IDatabase.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "true");
					try {
						TableCreateProcessor.execute(connection, table);
						rs = connection.getMetaData().getTables(null, null, table.getName(), null);
						if (!rs.next()) {
							rs.close();
							fail("Table has not been materialized!");
						}
						
						
					} finally {
						connection.createStatement().executeUpdate("DROP TABLE \"" + table.getName() + "\"");
						Configuration.set(IDatabase.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "false");
					}
				} finally {
					connection.close();
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
	
	/**
	 * Create the tables with constraints.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void createTableWithConstraints() throws IOException {
		DataStructureTableModel addresses = parseTableDefinition("/addresses.table", "ADDRESSES");
		DataStructureTableModel persons = parseTableDefinition("/persons.table", "PERSONS");
			
		try {
			Connection connection = getDataSource().getConnection();
			try {
				ResultSet rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(addresses.getName()), null);
				if (rs.next()) {
					rs.close();
					fail("Table 'ADDRESSES' already exists!");
				}
				rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(persons.getName()), null);
				if (rs.next()) {
					rs.close();
					fail("Table 'PERSONS' already exists!");
				}
				try {
					TableCreateProcessor.execute(connection, addresses);
					TableCreateProcessor.execute(connection, persons);
					
					rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(addresses.getName()), null);
					if (!rs.next()) {
						rs.close();
						fail("Table addresses has not been materialized!");
					}
					
					rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(persons.getName()), null);
					if (!rs.next()) {
						rs.close();
						fail("Table persons has not been materialized!");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				} finally {
					connection.createStatement().executeUpdate("DROP TABLE " + persons.getName());
					connection.createStatement().executeUpdate("DROP TABLE " + addresses.getName());
				}
			} finally {
				connection.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Parses the table definition.
	 *
	 * @param definition the definition
	 * @param name the name
	 * @return the data structure table model
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private DataStructureTableModel parseTableDefinition(String definition, String name) throws IOException {
		InputStream in = null;
		try {
			in = DataStructureTableTest.class.getResourceAsStream(definition);
			String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
			DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
			assertEquals(name, table.getName());
			return table;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * Create the tables with constraints.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void createTableWithConstraintsReverseOrder() throws IOException {
		DataStructureTableModel addresses = parseTableDefinition("/addresses.table", "ADDRESSES");
		DataStructureTableModel persons = parseTableDefinition("/persons.table", "PERSONS");
			
		try {
			Connection connection = getDataSource().getConnection();
			try {
				ResultSet rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(addresses.getName()), null);
				if (rs.next()) {
					rs.close();
					fail("Table 'ADDRESSES' already exists!");
				}
				rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(persons.getName()), null);
				if (rs.next()) {
					rs.close();
					fail("Table 'PERSONS' already exists!");
				}
				try {
					TableCreateProcessor.execute(connection, persons, true);
					TableCreateProcessor.execute(connection, addresses);
					TableForeignKeysCreateProcessor.execute(connection, persons);
					
					rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(addresses.getName()), null);
					if (!rs.next()) {
						rs.close();
						fail("Table addresses has not been materialized!");
					}
					
					rs = connection.getMetaData().getTables(null, null, DatabaseMetadataHelper.normalizeTableName(persons.getName()), null);
					if (!rs.next()) {
						rs.close();
						fail("Table persons has not been materialized!");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					fail(e.getMessage());
				} finally {
					connection.createStatement().executeUpdate("DROP TABLE " + persons.getName());
					connection.createStatement().executeUpdate("DROP TABLE " + addresses.getName());
				}
			} finally {
				connection.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
