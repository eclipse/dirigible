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
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinitionFactoryTest;
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
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OData2ODataXTransformerTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DBMetadataUtil dbMetadataUtil;

    @InjectMocks
    OData2ODataXTransformer odata2ODataXTransformer;

    @Test(expected = IllegalArgumentException.class)
    public void testTransformWithIncorrectMultiplicity() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithWrongMultiplicity.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithWrongMultiplicity.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        odata2ODataXTransformer.transform(definition);
    }

    @Test
    public void testTransformWithCompositePrimaryKeyWithLessDbPropsExposed() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithProp.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithProp.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("PHONES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"companyId\" />\n" +
                "\t\t\t<PropertyRef Name=\"employeeNumber\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"companyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"employeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"orderId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "\t<EntityType Name=\"phoneType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"Number\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"Number\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"FkCompanyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"FkEmployeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\" />\n" +
                "\t\t<EntitySet Name=\"Phones\" EntityType=\"np.phoneType\" />\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test(expected = OData2TransformerException.class)
    public void testTransformWithCompositePrimaryKeyAndLessNumberOfDBProps() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithProp.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithProp.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        odata2ODataXTransformer.transform(definition);
    }
}
