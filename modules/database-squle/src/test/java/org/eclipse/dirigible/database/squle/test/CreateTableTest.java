package org.eclipse.dirigible.database.squle.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.Squle;
import org.junit.Test;

public class CreateTableTest {
	
	@Test
	public void createTableGeneric() {
		String sql = Squle.getDefault()
			.create()
			.table("CUSTOMERS")
			.column("ID", DataType.INTEGER, true)
			.column("FIRST_NAME", DataType.VARCHAR, false, "(20)")
			.column("LAST_NAME", DataType.VARCHAR, false, "(30)")
			.toString();
		
		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER PRIMARY KEY , FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) )", sql);
	}
	
	@Test
	public void createTableTypeSafe() {
		String sql = Squle.getDefault()
			.create()
			.table("CUSTOMERS")
			.columnInteger("ID", true)
			.columnVarchar("FIRST_NAME", 20, false)
			.columnVarchar("LAST_NAME", 30, false)
			.toString();
		
		assertNotNull(sql);
		assertEquals("CREATE TABLE CUSTOMERS ( ID INTEGER PRIMARY KEY , FIRST_NAME VARCHAR (20) , LAST_NAME VARCHAR (30) )", sql);
	}

}
