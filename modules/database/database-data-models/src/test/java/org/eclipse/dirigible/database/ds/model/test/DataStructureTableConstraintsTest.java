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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.junit.Test;

/**
 * The Class DataStructureTableConstraintsTest.
 */
public class DataStructureTableConstraintsTest {

	/**
	 * Parses the table.
	 */
	@Test
	public void parseTable() {
		try {
			InputStream in = DataStructureTableConstraintsTest.class.getResourceAsStream("/persons.table");
			try {
				String tableFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
				assertEquals("PERSONS", table.getName());
				assertNotNull(table.getConstraints());
				assertNotNull(table.getConstraints().getPrimaryKey());
				assertNotNull(table.getConstraints().getForeignKeys());
				assertNotNull(table.getConstraints().getUniqueIndices());
				assertNotNull(table.getConstraints().getChecks());
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
