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
package org.eclipse.dirigible.database.transfer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.h2.H2Database;
import org.junit.Before;
import org.junit.Test;

public class DataTransferTest {
	
	/** The source data source. */
	private DataSource sourceDS = null;
	
	/** The target data source. */
	private DataSource targetDS = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			File file = new File("./target/dirigible/h2");
			file.delete();
			H2Database h2Database = new H2Database();
			this.sourceDS = h2Database.getDataSource("SourceDB");
			this.targetDS = h2Database.getDataSource("TargetDB");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Transfer data
	 *
	 * @throws SQLException
	 *             the SQL exception
	 * @throws DataTransferException
	 *             the DataTransferException exception
	 */
	@Test
	public void transferData() throws SQLException, DataTransferException {
		
		prepareSourceDatabase();
		
		DataTransferConfiguration dataTransferConfiguration = new DataTransferConfiguration();
		dataTransferConfiguration.setSourceSchema("PUBLIC");
		dataTransferConfiguration.setTargetSchema("PUBLIC");
		
		DataTransferManager.transfer(sourceDS, targetDS, dataTransferConfiguration, null);
		
		assertTrue(checkResults());
	}

	private void prepareSourceDatabase() throws SQLException {
		try (Connection connection = sourceDS.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				try {
					stmt.executeUpdate("DROP TABLE CAR");
					stmt.executeUpdate("DROP TABLE DRIVER");
				} catch (Exception e) {
					// cleanup
				}
				stmt.executeUpdate("CREATE TABLE DRIVER (ID INT PRIMARY KEY, NAME VARCHAR(20))");
				stmt.executeUpdate("INSERT INTO DRIVER VALUES (1, 'John')");
				stmt.executeUpdate("INSERT INTO DRIVER VALUES (2, 'Rudolf')");
				stmt.executeUpdate("INSERT INTO DRIVER VALUES (3, 'Tom')");
				stmt.executeUpdate("CREATE TABLE CAR (ID INT PRIMARY KEY, NAME VARCHAR(20), DRIVER INT, FOREIGN KEY (DRIVER) REFERENCES DRIVER(ID))");
				stmt.executeUpdate("INSERT INTO CAR VALUES (1, 'Toyota', 1)");
				stmt.executeUpdate("INSERT INTO CAR VALUES (2, 'Subaru', 2)");
				stmt.executeUpdate("INSERT INTO CAR VALUES (3, 'Honda', 3)");
			}
		}
		try (Connection connection = targetDS.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				try {
					stmt.executeUpdate("DROP TABLE CAR");
					stmt.executeUpdate("DROP TABLE DRIVER");
				} catch (Exception e) {
					// cleanup
				}
			}
		}
	}
	
	private boolean checkResults() throws SQLException {
		try (Connection connection = targetDS.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM DRIVER");
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count != 3) {
						return false;
					}
				}
				rs = stmt.executeQuery("SELECT COUNT(*) FROM CAR");
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count != 3) {
						return false;
					}
				}
				
			}
		}
		return true;
	}


}
