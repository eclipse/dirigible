/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.engine.odata2.definition.factory.ODataDefinitionFactory;
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
	
	@Test
	public void testHandlers() throws IOException {
		String def = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/def/OData1.odata"));
		ODataDefinition definition = ODataDefinitionFactory.parseOData("/def/OData1.odata", def);
		assertEquals(1, definition.getEntities().size());
		assertNotNull(definition.getEntities().get(0));
		assertEquals("ENTITY1", definition.getEntities().get(0).getTable());
		assertEquals(4, definition.getEntities().get(0).getHandlers().size());
		assertEquals("create", definition.getEntities().get(0).getHandlers().get(0).getMethod());
		assertEquals("before", definition.getEntities().get(0).getHandlers().get(0).getType());
		assertEquals("/test1/myhandler", definition.getEntities().get(0).getHandlers().get(0).getHandler());
	}

}
