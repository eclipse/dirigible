package org.eclipse.dirigible.database.sqlb.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.eclipse.dirigible.database.sqlb.SelectBuilder;
import org.junit.Test;

public class SelectTest {
	
	@Test
	public void selectStar() {
		SelectBuilder select = new SelectBuilder()
			.addColumn("*")
			.addTable("CUSTOMERS");
		
		String sql = select.generate();
		assertNotNull(sql);
		assertEquals("SELECT * FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectColumnsFromTable() {
		SelectBuilder select = new SelectBuilder()
			.addColumn("FIRST_NAME")
			.addColumn("LAST_NAME")
			.addTable("CUSTOMERS");
		
		String sql = select.generate();
		assertNotNull(sql);
		assertEquals("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMERS", sql);
	}
	
	@Test
	public void selectWhereSimple() {
		SelectBuilder select = new SelectBuilder()
			.addColumn("*")
			.addTable("CUSTOMERS")
			.addWhere("PRICE > ?");
		
		String sql = select.generate();
		System.out.println(sql);
		assertEquals("SELECT * FROM CUSTOMERS WHERE PRICE > ?", sql);
	}

}
