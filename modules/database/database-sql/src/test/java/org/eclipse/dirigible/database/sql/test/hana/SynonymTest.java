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
package org.eclipse.dirigible.database.sql.test.hana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.dialects.hana.HanaSqlDialect;
import org.junit.Test;

/**
 * The Class SequenceTest.
 */
public class SynonymTest {
	
	/**
	 * Creates the sequence.
	 */
	@Test
	public void createSequence() {
		String sql = SqlFactory.getNative(new HanaSqlDialect())
			.create()
			.synonym("my.namespace::Customers")
			.forSource("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("CREATE SYNONYM \"my.namespace::Customers\" FOR CUSTOMERS", sql);
	}
	
	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		String sql = SqlFactory.getNative(new HanaSqlDialect())
			.drop()
			.synonym("my.namespace::Customers")
			.build();
		
		assertNotNull(sql);
		assertEquals("DROP SYNONYM \"my.namespace::Customers\"", sql);
	}

}
