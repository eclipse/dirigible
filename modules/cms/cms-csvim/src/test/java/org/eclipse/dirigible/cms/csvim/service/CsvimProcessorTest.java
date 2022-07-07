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
package org.eclipse.dirigible.cms.csvim.service;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.eclipse.dirigible.cms.csvim.api.CsvimException;
import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.junit.Test;

public class CsvimProcessorTest extends AbstractDirigibleTest {

	@Test
	public void testInsert() throws SQLException, CsvimException {
		CsvimProcessor csvimProcessor = new CsvimProcessor();
		DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		
		String path = IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/csvim/test.csv";
		String content = "1,John,Doe\n2,Jane,Doe\n";
		repository.createResource(path, content.getBytes());
		CsvFileDefinition csvFileDefinition = new CsvFileDefinition();
		csvFileDefinition.setFile("/csvim/test.csv");
		csvFileDefinition.setTable("TEST_CSV");
		
		try (Connection connection = dataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("CREATE TABLE TEST_CSV (ID INT PRIMARY KEY, FIRST_NAME VARCHAR(20), LAST_NAME VARCHAR(20))");
			}
			
			csvimProcessor.process(csvFileDefinition, content, connection);
			
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TEST_CSV");
				if (rs.next()) {
					assertEquals(2, rs.getInt(1));
				}
			}
			
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("DROP TABLE TEST_CSV");
			}
		}
	}
	
	@Test
	public void testUpdate() throws SQLException, CsvimException {
		CsvimProcessor csvimProcessor = new CsvimProcessor();
		DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
		
		String path = IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/csvim/test.csv";
		String content = "1,John,Doe\n2,Jane,Doe\n";
		repository.createResource(path, content.getBytes());
		CsvFileDefinition csvFileDefinition = new CsvFileDefinition();
		csvFileDefinition.setFile("/csvim/test.csv");
		csvFileDefinition.setTable("TEST_CSV");
		
		try (Connection connection = dataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("CREATE TABLE TEST_CSV (ID INT PRIMARY KEY, FIRST_NAME VARCHAR(20), LAST_NAME VARCHAR(20))");
			}
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("INSERT INTO TEST_CSV VALUES (2,'Jennifer','Doe')");
			}
			
			csvimProcessor.process(csvFileDefinition, content, connection);
			
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TEST_CSV");
				if (rs.next()) {
					assertEquals(2, rs.getInt(1));
				}
			}
			
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM TEST_CSV WHERE ID=2");
				if (rs.next()) {
					assertEquals("Jane", rs.getString("FIRST_NAME"));
				}
			}
			
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("DROP TABLE TEST_CSV");
			}
		}
	}

}
