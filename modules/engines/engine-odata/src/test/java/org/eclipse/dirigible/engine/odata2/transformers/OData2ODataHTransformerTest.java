/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.transformers;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.engine.odata2.definition.*;
import org.eclipse.dirigible.engine.odata2.definition.factory.ODataDefinitionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class OData2ODataHTransformerTest {

    @InjectMocks
    OData2ODataHTransformer odata2ODataHTransformer;

    @Test
    public void testTransform() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithHandlers.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithHandlers.odata", employee);

        List<ODataHandlerDefinition> actualResult = odata2ODataHTransformer.transform(definition);

        assertEquals(actualResult.size(),4);
        assertEquals("employeeType", actualResult.get(0).getName());
        assertEquals("np", actualResult.get(0).getNamespace());
        assertEquals(ODataHandlerMethods.create.name(), actualResult.get(0).getMethod());
        assertEquals(ODataHandlerTypes.before.name(), actualResult.get(0).getType());
        assertEquals("/test1/myhandler", actualResult.get(0).getHandler());

        assertEquals("employeeType", actualResult.get(1).getName());
        assertEquals("np", actualResult.get(1).getNamespace());
        assertEquals(ODataHandlerMethods.update.name(), actualResult.get(1).getMethod());
        assertEquals(ODataHandlerTypes.after.name(), actualResult.get(1).getType());
        assertEquals("/test2/myhandler", actualResult.get(1).getHandler());

        assertEquals("employeeType", actualResult.get(2).getName());
        assertEquals("np", actualResult.get(2).getNamespace());
        assertEquals(ODataHandlerMethods.delete.name(), actualResult.get(2).getMethod());
        assertEquals(ODataHandlerTypes.on.name(), actualResult.get(2).getType());
        assertEquals("/test3/myhandler", actualResult.get(2).getHandler());

        assertEquals("employeeType", actualResult.get(3).getName());
        assertEquals("np", actualResult.get(3).getNamespace());
        assertEquals(ODataHandlerMethods.delete.name(), actualResult.get(3).getMethod());
        assertEquals(ODataHandlerTypes.forbid.name(), actualResult.get(3).getType());
    }

    @Test(expected = OData2TransformerException.class)
    public void testTransformWithIncorrectODataHandlerType() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithWrongHandler.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithWrongHandler.odata", employee);
        odata2ODataHTransformer.transform(definition);
    }
}
