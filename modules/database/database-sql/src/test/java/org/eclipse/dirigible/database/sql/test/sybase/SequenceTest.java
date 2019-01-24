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
package org.eclipse.dirigible.database.sql.test.sybase;

import static org.junit.Assert.fail;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.sybase.SybaseSqlDialect;
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
			String sql = SqlFactory.getNative(new SybaseSqlDialect()).create().sequence("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does Sybase support Sequences?");
	}

	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		try {
			String sql = SqlFactory.getNative(new SybaseSqlDialect()).drop().sequence("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does Sybase support Sequences?");
	}

	/**
	 * Nextval sequnce.
	 */
	@Test
	public void nextvalSequnce() {
		try {
			String sql = SqlFactory.getNative(new SybaseSqlDialect()).nextval("CUSTOMERS_SEQUENCE").build();
		} catch (Exception e) {
			return;
		}

		fail("Does Sybase support Sequences?");
	}

}
