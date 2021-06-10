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
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinitionFactoryTest;
import org.eclipse.dirigible.engine.odata2.definition.ODataProperty;
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
        model.setTableType(ISqlKeywords.METADATA_TABLE);
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
        model.setTableType(ISqlKeywords.METADATA_TABLE);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_TABLE);
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
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n" +
                "\t\t<EntitySet Name=\"Phones\" EntityType=\"np.phoneType\"/>\n";

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
        model.setTableType(ISqlKeywords.METADATA_TABLE);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        odata2ODataXTransformer.transform(definition);
    }

    @Test
    public void testTransformWithAnnotations() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/Employee.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/Employee.odata", employee);

        definition.getEntities().get(0).getAnnotationsEntitySet().put("sap:creatable", "true");
        definition.getEntities().get(0).getAnnotationsEntitySet().put("sap:updatable-path", "Updatable");
        definition.getEntities().get(0).getAnnotationsEntityType().put("sap:semantics", "aggregate");

        definition.getEntities().get(0).getProperties().get(0).getAnnotationsProperty().put("sap:label", "someLabel");
        definition.getEntities().get(0).getProperties().get(0).getAnnotationsProperty().put("sap:aggregation-role", "dimension");
        definition.getEntities().get(0).getNavigations().get(0).getAnnotationsNavigationProperty().put("sap:filterable", "false");

        definition.getAssociations().get(0).getAnnotationsAssociationSet().put("sap:creatable", "true");

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_TABLE);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_TABLE);
        when(dbMetadataUtil.getTableMetadata("PHONES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\" sap:semantics=\"aggregate\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"companyId\" />\n" +
                "\t\t\t<PropertyRef Name=\"employeeNumber\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"companyId\" Nullable=\"false\" Type=\"Edm.Int32\" sap:label=\"someLabel\" sap:aggregation-role=\"dimension\"/>\n" +
                "\t\t<Property Name=\"employeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<NavigationProperty Name=\"HisPhones\" Relationship=\"np.Employees_PhonesType\" FromRole=\"employeePrincipal\" ToRole=\"phoneDependent\" sap:filterable=\"false\"/>\n" +
                "\t</EntityType>\n" +
                "\t<EntityType Name=\"phoneType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"Number\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"Number\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"FkCompanyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"FkEmployeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "\t<Association Name=\"Employees_PhonesType\">\n" +
                "\t\t<End Type=\"np.employeeType\" Role=\"employeePrincipal\" Multiplicity=\"1\"/>\n" +
                "\t\t<End Type=\"np.phoneType\" Role=\"phoneDependent\" Multiplicity=\"*\"/>\n" +
                " \t</Association>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\" sap:creatable=\"true\" sap:updatable-path=\"Updatable\"/>\n" +
                "\t\t<EntitySet Name=\"Phones\" EntityType=\"np.phoneType\"/>\n" +
                "\t<AssociationSet Name=\"Employees_Phones\" Association=\"np.Employees_PhonesType\" sap:creatable=\"true\">\n" +
                "\t\t\t<End Role=\"employeePrincipal\" EntitySet=\"Employees\"/>\n" +
                " \t\t\t<End Role=\"phoneDependent\" EntitySet=\"Phones\"/>\n" +
                "\t\t\t</AssociationSet>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test
    public void testTransformOnViewWithGenId() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("GEN_ID");

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"GEN_ID\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"GEN_ID\" Type=\"Edm.String\" Nullable=\"false\" MaxLength=\"2147483647\" sap:filterable=\"false\"/>\n" +
                "\t\t<Property Name=\"CompanyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"EmployeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"OrderId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"AddressId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test
    public void testTransformOnViewWithOriginalKeys() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("COMPANY_ID");
        definition.getEntities().get(0).getKeys().add("EMPLOYEE_NUMBER");

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"CompanyId\" />\n" +
                "\t\t\t<PropertyRef Name=\"EmployeeNumber\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"CompanyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"EmployeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"OrderId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"AddressId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test
    public void testTransformOnViewWithPropsAndOriginalKeys() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("COMPANY_ID");
        definition.getEntities().get(0).getKeys().add("EMPLOYEE_NUMBER");

        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myORDER_ID").setType("Edm.Int32").setNullable(false).setColumn("ORDER_ID"));
        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myCOMPANY_ID").setType("Edm.Int32").setNullable(false).setColumn("COMPANY_ID"));
        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myEMPLOYEE_NUMBER").setType("Edm.Int32").setNullable(false).setColumn("EMPLOYEE_NUMBER"));

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"myCOMPANY_ID\" />\n" +
                "\t\t\t<PropertyRef Name=\"myEMPLOYEE_NUMBER\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"myORDER_ID\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"myCOMPANY_ID\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"myEMPLOYEE_NUMBER\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test
    public void testTransformOnViewWithNotOriginalKeys() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("ADDRESS_ID");

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4, column5), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"AddressId\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"CompanyId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"EmployeeNumber\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"OrderId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"AddressId\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"Id\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test
    public void testTransformOnViewWithPropsAndNotOriginalKeys() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("ADDRESS_ID");

        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myORDER_ID").setType("Edm.Int32").setNullable(false).setColumn("ORDER_ID"));
        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myADDRESS_ID").setType("Edm.Int32").setNullable(false).setColumn("ADDRESS_ID"));

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "\t<EntityType Name=\"employeeType\">\n" +
                "\t\t<Key>\n" +
                "\t\t\t<PropertyRef Name=\"myADDRESS_ID\" />\n" +
                "\t\t</Key>\n" +
                "\t\t<Property Name=\"myORDER_ID\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t\t<Property Name=\"myADDRESS_ID\" Nullable=\"false\" Type=\"Edm.Int32\"/>\n" +
                "\t</EntityType>\n" +
                "</Schema>\n";
        String entitySet = "\t\t<EntitySet Name=\"Employees\" EntityType=\"np.employeeType\"/>\n";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }

    @Test(expected = OData2TransformerException.class)
    public void testTransformOnViewWithNonExistingPropsAndNotOriginalKeys() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);
        definition.getEntities().get(0).getKeys().add("ADDRESS_ID");

        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myERROR_ID").setType("Edm.Int32").setNullable(false).setColumn("ERROR_ID"));
        definition.getEntities().get(0).getProperties().add(new ODataProperty().setName("myADDRESS_ID").setType("Edm.Int32").setNullable(false).setColumn("ADDRESS_ID"));

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);

        odata2ODataXTransformer.transform(definition);
    }

    @Test
    public void testTransformWithUnsupportedObjectType() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeView.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeView.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_SYNONYM);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES")).thenReturn(model);
        String entitySchema = "<Schema Namespace=\"np\"\n" +
                "\txmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\">\n" +
                "</Schema>\n";
        String entitySet = "";

        String[] actualResult = odata2ODataXTransformer.transform(definition);
        assertArrayEquals(new String[]{entitySchema, entitySet}, actualResult);
    }
}
