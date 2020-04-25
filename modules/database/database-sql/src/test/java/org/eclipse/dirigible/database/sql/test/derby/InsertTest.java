/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.test.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.derby.DerbySqlDialect;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class InsertTest.
 */
public class InsertTest {
	
	/**
	 * Insert simple.
	 */
	@Test
	public void insertSimple() {
		String sql = SqlFactory.getNative(new DerbySqlDialect())
			.insert()
			.into("CUSTOMERS")
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.build();
		
		assertNotNull(sql);
		assertEquals("INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME) VALUES (?, ?)", sql);
	}
	
	/**
	 * Insert values.
	 */
	@Test
	public void insertValues() {
		String sql = SqlFactory.getNative(new DerbySqlDialect())
			.insert()
			.into("CUSTOMERS")
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.value("?")
			.value("'Smith'")
			.build();

		assertNotNull(sql);
		assertEquals("INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME) VALUES (?, 'Smith')", sql);
	}
	
	/**
	 * Insert select.
	 */
	@Test
	public void insertSelect() {
		String sql = SqlFactory.getNative(new DerbySqlDialect())
			.insert()
			.into("CUSTOMERS")
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.select(SqlFactory.getNative(new DerbySqlDialect()).select().column("*").from("SUPPLIERS").build())
			.toString();

		assertNotNull(sql);
		assertEquals("INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME) SELECT * FROM SUPPLIERS", sql);
	}

}
