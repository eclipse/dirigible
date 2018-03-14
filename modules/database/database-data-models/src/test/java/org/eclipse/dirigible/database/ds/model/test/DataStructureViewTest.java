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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.database.ds.model.DataStructureModelFactory;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.junit.Test;

/**
 * The Class DataStructureViewTest.
 */
public class DataStructureViewTest {
	
	/**
	 * Parses the view.
	 */
	@Test
	public void parseView() {
		try {
			InputStream in = DataStructureViewTest.class.getResourceAsStream("/customer_orders.view");
			try {
				String viewFile = IOUtils.toString(in, StandardCharsets.UTF_8);
				DataStructureViewModel view = DataStructureModelFactory.parseView(viewFile);
				assertEquals("CUSTOMER_ORDERS", view.getName());
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
