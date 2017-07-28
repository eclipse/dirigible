/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.test;

import static org.junit.Assert.fail;

import javax.sql.DataSource;

import org.eclipse.dirigible.database.derby.DerbyDatabase;
import org.junit.Before;

public class AbstractPersistenceManagerTest {
	
	private DataSource dataSource = null;
	
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
	
	public DataSource getDataSrouce() {
		return dataSource;
	}

}
