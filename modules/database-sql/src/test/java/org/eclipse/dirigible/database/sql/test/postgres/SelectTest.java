/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.test.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.postgres.PostgresSqlDialect;
import org.junit.Test;

public class SelectTest {
	
	@Test
	public void selectStar() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTable() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTableAliases() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("c.FIRST_NAME")
			.column("c.LAST_NAME")
			.from("CUSTOMERS", "c")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT c.FIRST_NAME, c.LAST_NAME FROM CUSTOMERS AS c", sql);
	}
	
	@Test
	public void selectColumnsFromTableJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.join("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS INNER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableInnerJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.innerJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS INNER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableOuterJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.outerJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS OUTER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableLeftJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.leftJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS LEFT JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableRightJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.rightJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS RIGHT JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableFullJoin() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.fullJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS FULL JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectDistinctColumnsFromTable() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.distinct()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT DISTINCT FIRST_NAME, LAST_NAME FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTableOrderByAndDesc() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.order("FIRST_NAME")
			.order("LAST_NAME", false)
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS ORDER BY FIRST_NAME ASC, LAST_NAME DESC", sql);
	}
	
	@Test
	public void selectColumnsFromTableGroupBy() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.group("FIRST_NAME")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS GROUP BY FIRST_NAME", sql);
	}
	
	@Test
	public void selectWhereSimple() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ?")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ?)", sql);
	}
	
	@Test
	public void selectWhereAnd() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ?")
			.where("AMOUNT < ?")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ?) AND (AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectWhereOr() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ? OR AMOUNT < ?")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectWhereExpr() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.where(SqlFactory.getNative(new PostgresSqlDialect()).expression().and("PRICE > ?").or("AMOUNT < ?").build())
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectLimit() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.limit(10)
			.build();

		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS LIMIT 10", sql);
	}
	
	@Test
	public void selectLimitOffset() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("*")
			.from("CUSTOMERS")
			.limit(10)
			.offset(20)
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS LIMIT 10 OFFSET 20", sql);
	}
	
	@Test
	public void selectHaving() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("COUNT(FIRST_NAME)")
			.column("COUNTRY")
			.from("CUSTOMERS")
			.group("COUNTRY")
			.having("COUNT(FIRST_NAME) > 5")
			.build();

		assertNotNull(sql);
		assertEquals("SELECT COUNT(FIRST_NAME), COUNTRY FROM CUSTOMERS GROUP BY COUNTRY HAVING COUNT(FIRST_NAME) > 5", sql);
	}
	
	@Test
	public void selectUnion() {
		String sql = SqlFactory.getNative(new PostgresSqlDialect())
			.select()
			.column("COUNTRY")
			.from("CUSTOMERS")
			.union(SqlFactory.getNative(new PostgresSqlDialect()).select().column("COUNTRY").from("SUPPLIERS").build())
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT COUNTRY FROM CUSTOMERS UNION SELECT COUNTRY FROM SUPPLIERS", sql);
	}

}
