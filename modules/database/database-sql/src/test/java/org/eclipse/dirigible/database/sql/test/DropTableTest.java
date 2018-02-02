/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class DropTableTest.
 */
public class DropTableTest {
	
	/**
	 * Drop table.
	 */
	@Test
	public void dropTable() {
		String sql = SqlFactory.getDefault()
			.drop()
			.table("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("DROP TABLE CUSTOMERS", sql);
	}
	
	
}
