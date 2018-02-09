/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.test.mysql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.mysql.MySQLSqlDialect;
import org.junit.Test;

/**
 * The Class SequenceTest.
 */
public class SequenceTest {

	/**
	 * Creates the sequence.
	 */
	@Test
	public void createSequence() {
		try {
			String sql = SqlFactory.getNative(new MySQLSqlDialect()).create().sequence("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does MySQL support Sequences?");
	}

	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		try {
			String sql = SqlFactory.getNative(new MySQLSqlDialect()).drop().sequence("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does MySQL support Sequences?");
	}

	/**
	 * Nextval sequnce.
	 */
	@Test
	public void nextvalSequnce() {
		try {
			String sql = SqlFactory.getNative(new MySQLSqlDialect()).nextval("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does MySQL support Sequences?");
	}

}
