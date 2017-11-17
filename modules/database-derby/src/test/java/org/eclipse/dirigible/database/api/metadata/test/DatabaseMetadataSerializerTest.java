/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.api.metadata.test;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.database.api.metadata.DatabaseMetadata;
import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class DatabaseMetadataSerializerTest.
 */
public class DatabaseMetadataSerializerTest {

	/** The data source. */
	private DataSource dataSource = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			DerbyDatabase derbyDatabase = new DerbyDatabase();
			this.dataSource = derbyDatabase.getDataSource("target/tests/derby");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Serialize to json.
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
	@Test
	public void serializeToJson() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetadata database = new DatabaseMetadata(connection, null, null, null);
			String json = GsonHelper.GSON.toJson(database);
			System.out.println(json);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

}
