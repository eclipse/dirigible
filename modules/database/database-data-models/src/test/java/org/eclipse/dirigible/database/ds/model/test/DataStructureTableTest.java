/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.junit.Test;

/**
 * The Class DataStructureTableTest.
 */
public class DataStructureTableTest {

	/**
	 * Parses the table.
	 */
	@Test
	public void parseTable() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableTest.class.getResourceAsStream("/customers.table"),
					StandardCharsets.UTF_8);
			DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
			assertEquals("CUSTOMERS", table.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Parses the precision scale.
	 */
	@Test
	public void parsePrecisionScale() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableTest.class.getResourceAsStream("/orders.table"),
					StandardCharsets.UTF_8);
			DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
			assertEquals("ORDERS", table.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
