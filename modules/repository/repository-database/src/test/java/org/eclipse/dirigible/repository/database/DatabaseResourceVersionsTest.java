/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.repository.database;

import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.db.DatabaseRepository;
import org.eclipse.dirigible.repository.generic.RepositoryGenericResourceVersionsTest;
import org.junit.Before;

/**
 * The Class DatabaseResourceVersionsTest.
 */
public class DatabaseResourceVersionsTest extends RepositoryGenericResourceVersionsTest {

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			DataSource dataSource = DatabaseTestHelper.createDataSource("target/tests/derby");
			repository = new DatabaseRepository(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
