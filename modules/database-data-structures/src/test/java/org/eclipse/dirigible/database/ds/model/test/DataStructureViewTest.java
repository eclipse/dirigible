package org.eclipse.dirigible.database.ds.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.junit.Test;

public class DataStructureViewTest {
	
	@Test
	public void parseView() {
		try {
			String viewFile = IOUtils.toString(DataStructureViewTest.class.getResourceAsStream("/customer_orders.view"), StandardCharsets.UTF_8);
			DataStructureViewModel view = DataStructureModelFactory.createViewModel(viewFile);
			assertEquals("CUSTOMER_ORDERS", view.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
