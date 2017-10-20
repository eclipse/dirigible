package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.junit.Test;

public class DataStructureTableConstraintsTest {

	@Test
	public void parseTable() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableConstraintsTest.class.getResourceAsStream("/persons.table"),
					StandardCharsets.UTF_8);
			DataStructureTableModel table = DataStructureModelFactory.parseTable(tableFile);
			assertEquals("PERSONS", table.getName());
			assertNotNull(table.getConstraints());
			assertNotNull(table.getConstraints().getPrimaryKey());
			assertNotNull(table.getConstraints().getForeignKeys());
			assertNotNull(table.getConstraints().getUniqueIndices());
			assertNotNull(table.getConstraints().getChecks());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
