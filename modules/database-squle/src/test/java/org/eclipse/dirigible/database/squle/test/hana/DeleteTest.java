package org.eclipse.dirigible.database.squle.test.hana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.hana.HanaSquleDialect;
import org.junit.Test;

public class DeleteTest {
	
	@Test
	public void deleteSimple() {
		String sql = Squle.getNative(new HanaSquleDialect())
			.delete()
			.from("CUSTOMERS")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DELETE FROM CUSTOMERS", sql);
	}

	@Test
	public void deleteWhere() {
		String sql = Squle.getNative(new HanaSquleDialect())
				.delete()
				.from("CUSTOMERS")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.toString();
			
			assertNotNull(sql);
			assertEquals("DELETE FROM CUSTOMERS WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

}
