/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.test;

import static java.text.MessageFormat.format;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPersistenceManagerTest.
 */
public class AbstractPersistenceManagerTest {

	/** The data source. */
	private DataSource dataSource = null;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {

			this.dataSource = createDataSource("target/tests/derby");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Gets the data srouce.
	 *
	 * @return the data srouce
	 */
	public DataSource getDataSrouce() {
		return dataSource;
	}

	/**
	 * Creates the data source.
	 *
	 * @param name the name
	 * @return the data source
	 * @throws Exception the exception
	 */
	protected DataSource createDataSource(String name) throws Exception {
		try {
			DataSource dataSource = new EmbeddedDataSource();
			String derbyRoot = prepareRootFolder(name);
			((EmbeddedDataSource) dataSource).setDatabaseName(derbyRoot);
			((EmbeddedDataSource) dataSource).setCreateDatabase("create");
			return dataSource;
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	/**
	 * Prepare root folder.
	 *
	 * @param name the name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String prepareRootFolder(String name) throws IOException {
		File rootFile = new File(name);
		File parentFile = rootFile.getCanonicalFile().getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				throw new IOException(
						format("Creation of the root folder [{0}] of the embedded Derby database failed.", name));
			}
		}
		return name;
	}

}
