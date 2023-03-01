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
package org.eclipse.dirigible.database.changelog.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.database.changelog.synchronizer.ChangelogSynchronizer;
import org.eclipse.dirigible.database.ds.model.DataStructureChangelogModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.persistence.processors.identity.Identity;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class DataStructureChangelogTest.
 */
public class DataStructureChangelogTest extends AbstractDirigibleTest {

	/** The changelog synchronizer. */
	private ChangelogSynchronizer changelogSynchronizer;

	/**  The datasource. */
	private DataSource dataSource;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.changelogSynchronizer = new ChangelogSynchronizer();
		this.dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
	}

	/**
	 * Changelog data.
	 */
	@Test
	public void updateChangelog() {
		try {
			InputStream in = DataStructureChangelogTest.class.getResourceAsStream("/person.changelog");
			try {
				String dataFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureChangelogModel data = DataStructureModelFactory.parseChangelog("/person.changelog", dataFile);
				Connection connection = null;
				try {
					connection = dataSource.getConnection();

					changelogSynchronizer.executeChangelogUpdate(connection, "/person.changelog", data);
					
					Statement statement = connection.createStatement();
					statement.executeUpdate("INSERT INTO PERSON (firstname, lastname, country, username) VALUES ('JOHN', 'SMITH', 'US', 'JONNY')");
					ResultSet rs = statement.executeQuery("SELECT * FROM PERSON");
					if (rs.next()) {
						assertEquals("JOHN", rs.getString(2));
					} else {
						fail("Post-processing - insert and query failed for the changelog");
					}

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
