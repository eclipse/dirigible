/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql.builders.records;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;

/**
 * The Class DeleteTest.
 */
public class DeleteRecordTest {
	
	/**
	 * Delete simple.
	 */
	@Test
	public void deleteSimple() {
		String sql = SqlFactory.getDefault()
			.delete()
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("DELETE FROM CUSTOMERS", sql);
	}

	/**
	 * Delete where.
	 */
	@Test
	public void deleteWhere() {
		String sql = SqlFactory.getDefault()
				.delete()
				.from("CUSTOMERS")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
			
			assertNotNull(sql);
			assertEquals("DELETE FROM CUSTOMERS WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

	/**
	 * Select column and where clause in case sensitive mode
	 */
	@Test
	public void deleteWehereWithSpecialCharsCaseSensitive() {
		Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "true");
		try {
			String sql = SqlFactory.getDefault()
				.delete()
				.from("CUSTOMERS")
				.where("PRICE_BASIC1$ LIKE ?")
				.build();
			
			assertNotNull(sql);
			assertEquals("DELETE FROM \"CUSTOMERS\" WHERE (\"PRICE_BASIC1$\" LIKE ?)", sql);
		} finally {
			Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
		}
	}

	@Test
	public void deleteWehereWithSpecialCharsCaseSensitiveWIthEqualCondition() {
		Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "true");
		try {
			String sql = SqlFactory.getDefault()
					.delete()
					.from("CUSTOMERS")
					.where("id= 'asas.as.as:asas`,_!@#$%^&*()+-::/\\'")
					.build();

			assertNotNull(sql);
			assertEquals("DELETE FROM \"CUSTOMERS\" WHERE (\"id\"= 'asas.as.as:asas`,_!@#$%^&*()+-::/\\')", sql);
		} finally {
			Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
		}
	}
	@Test
	public void deleteWehereWithSpecialCharsCaseSensitiveWithInCondition() {
		Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "true");
		try {
			String sql = SqlFactory.getDefault()
					.delete()
					.from("CUSTOMERS")
					.where("id in('as', 'bd')")
					.build();

			assertNotNull(sql);
			assertEquals("DELETE FROM \"CUSTOMERS\" WHERE (\"id\" in('as', 'bd'))", sql);
		} finally {
			Configuration.set("DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE", "false");
		}
	}
}
