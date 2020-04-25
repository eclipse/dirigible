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
 * The Class SequenceTest.
 */
public class SequenceTest {

	/**
	 * Creates the sequence.
	 */
	@Test
	public void createSequence() {
		String sql = SqlFactory.getNative(new DerbySqlDialect()).create().sequence("CUSTOMERS_SEQUENCE").build();

		assertNotNull(sql);
		assertEquals("CREATE SEQUENCE CUSTOMERS_SEQUENCE AS BIGINT START WITH 0", sql);
	}

	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		String sql = SqlFactory.getNative(new DerbySqlDialect()).drop().sequence("CUSTOMERS_SEQUENCE").build();

		assertNotNull(sql);
		assertEquals("DROP SEQUENCE CUSTOMERS_SEQUENCE RESTRICT", sql);
	}

	/**
	 * Nextval sequnce.
	 */
	@Test
	public void nextvalSequnce() {
		String sql = SqlFactory.getNative(new DerbySqlDialect()).nextval("CUSTOMERS_SEQUENCE").build();

		assertNotNull(sql);
		assertEquals("( VALUES NEXT VALUE FOR CUSTOMERS_SEQUENCE )", sql);
	}

}
