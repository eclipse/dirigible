package org.eclipse.dirigible.database.squle.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.junit.Test;

public class SelectTest {
	
	@Test
	public void selectStar() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTable() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTableAliases() {
		String sql = Squle.select()
			.column("c.FIRST_NAME")
			.column("c.LAST_NAME")
			.from("CUSTOMERS", "c")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT c.FIRST_NAME, c.LAST_NAME FROM CUSTOMERS AS c", sql);
	}
	
	@Test
	public void selectColumnsFromTableJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.join("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS INNER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableInnerJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.innerJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS INNER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableOuterJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.outerJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS OUTER JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableLeftJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.leftJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS LEFT JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableRightJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.rightJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS RIGHT JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectColumnsFromTableFullJoin() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.fullJoin("ADDRESSES", "CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS FULL JOIN ADDRESSES ON CUSTOMERS.ADDRESS_ID=ADDRESSES.ADDRESS_ID", sql);
	}
	
	@Test
	public void selectDistinctColumnsFromTable() {
		String sql = Squle.select()
			.distinct()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT DISTINCT FIRST_NAME, LAST_NAME FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTableOrderByAndDesc() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.order("FIRST_NAME")
			.order("LAST_NAME", false)
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS ORDER BY FIRST_NAME ASC, LAST_NAME DESC", sql);
	}
	
	@Test
	public void selectColumnsFromTableGroupBy() {
		String sql = Squle.select()
			.column("FIRST_NAME")
			.column("LAST_NAME")
			.from("CUSTOMERS")
			.group("FIRST_NAME")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS GROUP BY FIRST_NAME", sql);
	}
	
	@Test
	public void selectWhereSimple() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ?")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ?)", sql);
	}
	
	@Test
	public void selectWhereAnd() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ?")
			.where("AMOUNT < ?")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ?) AND (AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectWhereOr() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.where("PRICE > ? OR AMOUNT < ?")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectWhereExpr() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.where(Squle.expr().and("PRICE > ?").or("AMOUNT < ?").toString())
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE (PRICE > ? OR AMOUNT < ?)", sql);
	}
	
	@Test
	public void selectLimit() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.limit(10)
			.toString();

		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS LIMIT 10", sql);
	}
	
	@Test
	public void selectLimitOffset() {
		String sql = Squle.select()
			.column("*")
			.from("CUSTOMERS")
			.limit(10)
			.offset(20)
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS LIMIT 10 OFFSET 20", sql);
	}
	
	@Test
	public void selectHaving() {
		String sql = Squle.select()
			.column("COUNT(FIRST_NAME)")
			.column("COUNTRY")
			.from("CUSTOMERS")
			.group("COUNTRY")
			.having("COUNT(FIRST_NAME) > 5")
			.toString();

		assertNotNull(sql);
		assertEquals("SELECT COUNT(FIRST_NAME), COUNTRY FROM CUSTOMERS GROUP BY COUNTRY HAVING COUNT(FIRST_NAME) > 5", sql);
	}
	
	@Test
	public void selectUnion() {
		String sql = Squle.select()
			.column("COUNTRY")
			.from("CUSTOMERS")
			.union(Squle.select().column("COUNTRY").from("SUPPLIERS").toString())
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT COUNTRY FROM CUSTOMERS UNION SELECT COUNTRY FROM SUPPLIERS", sql);
	}

}
