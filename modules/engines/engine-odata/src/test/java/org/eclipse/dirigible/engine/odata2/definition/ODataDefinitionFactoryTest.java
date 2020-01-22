/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.odata2.definition;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ODataDefinitionFactoryTest {

	@Test
	public void testSimple() throws IOException {
		String cars = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/cars/Cars.odata"));
		ODataDefinition definition = ODataDefinitionFactory.parseOData("/cars/Cars.odata", cars);
		assertEquals(2, definition.getEntities().size());
		assertNotNull(definition.getEntities().get(0));
		assertEquals("CAR", definition.getEntities().get(0).getTable());
	}
	
	@Test
	public void testNavigation() throws IOException {
		String cars = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/orders/Orders.odata"));
		ODataDefinition definition = ODataDefinitionFactory.parseOData("/orders/Orders.odata", cars);
		assertEquals(2, definition.getEntities().size());
		assertNotNull(definition.getEntities().get(0));
		assertEquals("ORDERS", definition.getEntities().get(0).getTable());
		assertEquals(1, definition.getEntities().get(0).getNavigations().size());
		assertEquals("OrderItems", definition.getEntities().get(0).getNavigations().get(0).getAssociation());
		assertEquals(1, definition.getAssociations().size());
		assertEquals("OrderItems", definition.getAssociations().get(0).getName());
	}

}
