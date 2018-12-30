/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceTest.
 */
public class SequenceTest {
	
	/**
	 * Creates the sequence.
	 */
	@Test
	public void createSequence() {
		String sql = SqlFactory.getDefault()
			.create()
			.sequence("CUSTOMERS_SEQUENCE")
			.build();
		
		assertNotNull(sql);
		assertEquals("CREATE SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		String sql = SqlFactory.getDefault()
			.drop()
			.sequence("CUSTOMERS_SEQUENCE")
			.build();
		
		assertNotNull(sql);
		assertEquals("DROP SEQUENCE CUSTOMERS_SEQUENCE", sql);
	}
	
	/**
	 * Nextval sequnce.
	 */
	@Test
	public void nextvalSequnce() {
		String sql = SqlFactory.getDefault()
			.nextval("CUSTOMERS_SEQUENCE")
			.build();
		
		assertNotNull(sql);
		assertEquals("SELECT NEXT VALUE FOR CUSTOMERS_SEQUENCE", sql);
	}

}
