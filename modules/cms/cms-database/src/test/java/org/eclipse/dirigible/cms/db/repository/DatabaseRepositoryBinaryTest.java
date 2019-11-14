/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.db.repository;

import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.eclipse.dirigible.cms.db.CmsDatabaseRepository;
import org.eclipse.dirigible.repository.generic.RepositoryGenericBinaryTest;
import org.junit.Before;

/**
 * The Class DatabaseRepositoryBinaryTest.
 */
public class DatabaseRepositoryBinaryTest extends RepositoryGenericBinaryTest {

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		try {
			DataSource dataSource = DatabaseTestHelper.createDataSource("target/tests/derby");
			repository = new CmsDatabaseRepository(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
