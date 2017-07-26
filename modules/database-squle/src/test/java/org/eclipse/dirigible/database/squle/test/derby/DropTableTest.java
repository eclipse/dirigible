package org.eclipse.dirigible.database.squle.test.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.derby.DerbySquleDialect;
import org.junit.Test;

public class DropTableTest {
	
	@Test
	public void dropTable() {
		String sql = Squle.getNative(new DerbySquleDialect())
			.drop()
			.table("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DROP TABLE CUSTOMERS", sql);
	}
	
	
}
