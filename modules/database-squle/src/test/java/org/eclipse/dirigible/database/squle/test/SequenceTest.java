package org.eclipse.dirigible.database.squle.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.squle.Squle;
import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void createSequence() {
		String sql = Squle.getDefault()
			.create()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("CREATE SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	@Test
	public void dropSequnce() {
		String sql = Squle.getDefault()
			.drop()
			.sequence("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("DROP SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	@Test
	public void nextvalSequnce() {
		String sql = Squle.getDefault()
			.nextval("CUSTOMERS_SEQUENCE")
			.toString();
		
		assertNotNull(sql);
		assertEquals("SELECT NEXT VALUE FOR CUSTOMERS_SEQUENCE", sql);
	}

}
