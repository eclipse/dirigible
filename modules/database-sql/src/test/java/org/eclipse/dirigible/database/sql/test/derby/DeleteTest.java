/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.test.derby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.derby.DerbySqlDialect;
import org.junit.Test;

public class DeleteTest {
	
	@Test
	public void deleteSimple() {
		String sql = SqlFactory.getNative(new DerbySqlDialect())
			.delete()
			.from("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("DELETE FROM CUSTOMERS", sql);
	}

	@Test
	public void deleteWhere() {
		String sql = SqlFactory.getNative(new DerbySqlDialect())
				.delete()
				.from("CUSTOMERS")
				.where("AGE > ?")
				.where("COMPANY = 'SAP'")
				.build();
			
			assertNotNull(sql);
			assertEquals("DELETE FROM CUSTOMERS WHERE (AGE > ?) AND (COMPANY = 'SAP')", sql);
	}

}
