package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.junit.Test;

public class DataStructureTableTest {
	
	@Test
	public void parseTable() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableTest.class.getResourceAsStream("/customers.table"), Configuration.UTF8);
			DataStructureTableModel table = DataStructureModelFactory.createTableModel(tableFile);
			assertEquals("CUSTOMERS", table.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void parsePrecisionScale() {
		try {
			String tableFile = IOUtils.toString(DataStructureTableTest.class.getResourceAsStream("/orders.table"), Configuration.UTF8);
			DataStructureTableModel table = DataStructureModelFactory.createTableModel(tableFile);
			assertEquals("ORDERS", table.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


}
