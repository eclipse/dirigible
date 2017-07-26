package org.eclipse.dirigible.database.squle.test.sybase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.dialects.sybase.SybaseSquleDialect;
import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void createSequence() {
		String sql = Squle.getNative(new SybaseSquleDialect())
			.create()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("CREATE SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	@Test
	public void dropSequnce() {
		String sql = Squle.getNative(new SybaseSquleDialect())
			.drop()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DROP SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	@Test
	public void nextvalSequnce() {
		String sql = Squle.getNative(new SybaseSquleDialect())
			.nextval("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT CUSTOMERS_SEQUENCE.NEXTVAL", sql);
	}

}
