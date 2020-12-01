/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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
			.synonym("CUSTOMERS_SEQUENCE")
			.forSource("CUSTOMERS")
			.build();
		
		assertNotNull(sql);
		assertEquals("CREATE SYNONYM CUSTOMERS_SEQUENCE FOR CUSTOMERS", sql);
	}
	
	/**
	 * Drop sequnce.
	 */
	@Test
	public void dropSequnce() {
		String sql = SqlFactory.getNative(new HanaSqlDialect())
			.drop()
			.synonym("CUSTOMERS_SEQUENCE")
			.build();
		
		assertNotNull(sql);
		assertEquals("DROP SYNONYM CUSTOMERS_SEQUENCE", sql);
	}

}
