package org.eclipse.dirigible.database.squle.test.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.derby.DerbySquleDialect;
import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void createSequence() {
		String sql = Squle.getNative(new DerbySquleDialect())
			.create()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("CREATE SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	@Test
	public void dropSequnce() {
		String sql = Squle.getNative(new DerbySquleDialect())
			.drop()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DROP SEQUENCE CUSTOMERS_SEQUENCE RESTRICT", sql);
	}
	
	@Test
	public void nextvalSequnce() {
		String sql = Squle.getNative(new DerbySquleDialect())
			.nextval("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("( VALUES NEXT VALUE FOR CUSTOMERS_SEQUENCE )", sql);
	}

}
