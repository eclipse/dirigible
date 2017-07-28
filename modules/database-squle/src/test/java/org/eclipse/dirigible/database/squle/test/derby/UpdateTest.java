/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.test.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.derby.DerbySquleDialect;
import org.junit.Test;

public class UpdateTest {
	
	@Test
	public void updateSimple() {
		String sql = Squle.getNative(new DerbySquleDialect())
			.update()
			.table("CUSTOMERS")
			.set("FIRST_NAME", "'John'")
			.toString();
		
		assertNotNull(sql);
		assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John'", sql);
	}

	@Test
	public void updateValues() {
		String sql = Squle.getNative(new DerbySquleDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.toString();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith'", sql);
	}
	
	@Test
	public void updateWhere() {
		String sql = Squle.getNative(new DerbySquleDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.toString();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

	@Test
	public void updateWhereSelect() {
		String sql = Squle.getNative(new DerbySquleDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("SALARY", Squle.getNative(new DerbySquleDialect()).select().column("MAX(SALARY)").from("BENEFITS").toString())
				.where("COMPANY = 'SAP'")
				.toString();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', SALARY = SELECT MAX(SALARY) FROM BENEFITS WHERE (COMPANY = 'SAP')", sql);
	}

	@Test
	public void updateWhereExpr() {
		String sql = Squle.getNative(new DerbySquleDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where(Squle.getNative(new DerbySquleDialect()).expr().and("PRICE > ?").or("AMOUNT < ?").and("COMPANY = 'SAP'").toString())
				.toString();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (PRICE > ? OR AMOUNT < ? AND COMPANY = 'SAP')", sql);
	}
	
	@Test
	public void updateWhereOrderLimit() {
		String sql = Squle.getNative(new DerbySquleDialect())
				.update()
				.table("CUSTOMERS")
				.set("FIRST_NAME", "'John'")
				.set("LAST_NAME", "'Smith'")
				.where("COMPANY = 'SAP'")
				.order("FIRST_NAME", false)
				.limit(5)
				.toString();
			
			assertNotNull(sql);
			assertEquals("UPDATE CUSTOMERS SET FIRST_NAME = 'John', LAST_NAME = 'Smith' WHERE (COMPANY = 'SAP') ORDER BY FIRST_NAME DESC LIMIT 5", sql);
	}
}
