/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.test.h2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.h2.H2SqlDialect;
import org.junit.Test;

public class UpdateTest {
	
	@Test
	public void updateSimple() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
			.update()
			.table("CUSTOMERS")
			.set("FIRST_NAME", "'John'")
			.build();
		
		assertNotNull(sql);
		assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John'", sql);
	}

	@Test
	public void updateValues() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.build();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith'", sql);
	}
	
	@Test
	public void updateWhere() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

	@Test
	public void updateWhereSelect() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("SALARY", SqlFactory.getNative(new H2SqlDialect()).select().column("MAX(SALARY)").from("BENEFITS").build())
				.where("COMPANY = 'SAP'")
				.build();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', SALARY = SELECT MAX(SALARY) FROM BENEFITS WHERE (COMPANY = 'SAP')", sql);
	}

	@Test
	public void updateWhereExpr() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where(SqlFactory.getNative(new H2SqlDialect()).expression().and("PRICE > ?").or("AMOUNT < ?").and("COMPANY = 'SAP'").build())
				.build();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (PRICE > ? OR AMOUNT < ? AND COMPANY = 'SAP')", sql);
	}

}
