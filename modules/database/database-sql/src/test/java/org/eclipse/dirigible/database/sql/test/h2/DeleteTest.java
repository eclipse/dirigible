/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.database.sql.test.h2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.h2.H2SqlDialect;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class DeleteTest.
 */
public class DeleteTest {
	
	/**
	 * Delete simple.
	 */
	@Test
	public void deleteSimple() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
			.delete()
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("DELETE FROM CUSTOMERS", sql);
	}

	/**
	 * Delete where.
	 */
	@Test
	public void deleteWhere() {
		String sql = SqlFactory.getNative(new H2SqlDialect())
				.delete()
				.from("CUSTOMERS")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
			
			assertNotNull(sql);
			assertEquals("DELETE FROM CUSTOMERS WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

}
