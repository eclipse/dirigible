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
package org.eclipse.dirigible.database.sql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class DropViewTest.
 */
public class DropViewTest {

	/**
	 * Drop table.
	 */
	@Test
	public void dropTable() {
		String sql = SqlFactory.getDefault().drop().view("CUSTOMERS_VIEW").build();

		assertNotNull(sql);
		assertEquals("DROP VIEW CUSTOMERS_VIEW", sql);
	}

}
