package org.eclipse.dirigible.database.squle.test.sybase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.sybase.SybaseSquleDialect;
import org.junit.Test;

public class DropTableTest {
	
	@Test
	public void dropTable() {
		String sql = Squle.getNative(new SybaseSquleDialect())
			.drop()
			.table("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DROP TABLE CUSTOMERS", sql);
	}
	
	
}
