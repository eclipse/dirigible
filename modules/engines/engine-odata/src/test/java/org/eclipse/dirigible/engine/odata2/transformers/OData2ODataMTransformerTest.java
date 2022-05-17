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
package org.eclipse.dirigible.engine.odata2.transformers;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
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
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OData2ODataMTransformerTest extends AbstractDirigibleTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DBMetadataUtil dbMetadataUtil;

    @InjectMocks
    OData2ODataMTransformer odata2ODataMTransformer = new OData2ODataMTransformer(new DefaultPropertyNameEscaper());

    @Test
    public void testTransformOrders() throws IOException, SQLException {
        String orders = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/orders/Orders.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/orders/Orders.odata", orders);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("Id", "Edm.Int32", true, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("Customer", "Edm.String", true, false);
        PersistenceTableModel model = new PersistenceTableModel("ORDERS", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("ORDERS", null)).thenReturn(model);

        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("Id", "Edm.Int32", true, true);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("OrderId", "Edm.Int32", true, false);
        PersistenceTableRelationModel rel = new PersistenceTableRelationModel("ITEMS", "ORDERS", "OrderId", "Id", "fkName", "PRIMARY_KEY_8B");
        model = new PersistenceTableModel("ITEMS", Arrays.asList(column3, column4), Collections.singletonList(rel));
        when(dbMetadataUtil.getTableMetadata("ITEMS", null)).thenReturn(model);

        String entityOrder = "{\n" +
                "\t\"edmType\": \"OrderType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataOrders.OrderType\",\n" +
                "\t\"sqlTable\": \"ORDERS\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Id\": \"Id\",\n" +
                "\t\"Customer\": \"Customer\",\n" +
                "\t\"_ref_ItemType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"Id\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"Id\"\n" +
                "}";
        String entityItem = "{\n" +
                "\t\"edmType\": \"ItemType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataOrders.ItemType\",\n" +
                "\t\"sqlTable\": \"ITEMS\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Id\": \"Id\",\n" +
                "\t\"Orderid\": \"OrderId\",\n" +
                "\t\"_ref_OrderType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"OrderId\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"Id\"\n" +
                "}";
        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityOrder, entityItem}, transformed);
    }

    @Test
    public void testTransformOrdersCaseSensitive() throws IOException, SQLException {
        String orders = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/orderscs/Orders.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/orderscs/Orders.odata", orders);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("CUSTOMER", "Edm.String", true, false);
        PersistenceTableModel model = new PersistenceTableModel("ORDERS", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("ORDERS", null)).thenReturn(model);

        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ITEM_ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", true, false);
        PersistenceTableRelationModel rel = new PersistenceTableRelationModel("ITEMS", "ORDERS", "ORDER_ID", "ID", "fkName", "PRIMARY_KEY_8B");
        model = new PersistenceTableModel("ITEMS", Arrays.asList(column3, column4), Collections.singletonList(rel));
        when(dbMetadataUtil.getTableMetadata("ITEMS", null)).thenReturn(model);

        String entityOrder = "{\n" +
                "\t\"edmType\": \"OrderType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataOrders.OrderType\",\n" +
                "\t\"sqlTable\": \"ORDERS\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Id\": \"ID\",\n" +
                "\t\"Customer\": \"CUSTOMER\",\n" +
                "\t\"_ref_ItemType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ID\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ID\"\n" +
                "}";
        String entityItem = "{\n" +
                "\t\"edmType\": \"ItemType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataOrders.ItemType\",\n" +
                "\t\"sqlTable\": \"ITEMS\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"ItemId\": \"ITEM_ID\",\n" +
                "\t\"OrderId\": \"ORDER_ID\",\n" +
                "\t\"_ref_OrderType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ORDER_ID\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ITEM_ID\"\n" +
                "}";
        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityOrder, entityItem}, transformed);
    }

    @Test
    public void testTransformEntityProperty() throws IOException, SQLException {
        ODataDefinition definition = OData2ODataTransformerTestUtil.loadData_testTransformEntityProperty(dbMetadataUtil);

        String entity1 = "{\n" +
                "\t\"edmType\": \"Entity1Type\",\n" +
                "\t\"edmTypeFqn\": \"mytest.Entity1Type\",\n" +
                "\t\"sqlTable\": \"ENTITY1\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"entity1Id\": \"ENTITY1ID\",\n" +
                "\t\"property2\": \"PROPERTY2\",\n" +
                "\t\"property3\": \"PROPERTY3\",\n" +
                "\t\"Country_Id\": \"Country.Id\",\n" +
                "\t\"_ref_Entity2Type\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ENTITY1ID\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ENTITY1ID\"\n" +
                "}";
        String entity2 = "{\n" +
                "\t\"edmType\": \"Entity2Type\",\n" +
                "\t\"edmTypeFqn\": \"mytest.Entity2Type\",\n" +
                "\t\"sqlTable\": \"ENTITY2\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"entity2Id\": \"ENTITY2ID\",\n" +
                "\t\"property2\": \"PROPERTY2\",\n" +
                "\t\"property3\": \"PROPERTY3\",\n" +
                "\t\"property4_5\": \"PROPERTY4_5\",\n" +
                "\t\"Entity1entity1Id\": \"ENTITY1ENTITY1ID\",\n" +
                "\t\"_ref_Entity1Type\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ENTITY1ENTITY1ID\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ENTITY2ID\"\n" +
                "}";
        String entity3 = "{\n" +
                "\t\"edmType\": \"Entity3Type\",\n" +
                "\t\"edmTypeFqn\": \"mytest.Entity3Type\",\n" +
                "\t\"sqlTable\": \"ENTITY3\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Entity3_id\": \"ENTITY3.ID\",\n" +
                "\t\"Name_id\": \"NAME.ID\",\n" +
                "\t\"_pk_\" : \"ENTITY3.ID\"\n" +
                "}";

        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entity1, entity2, entity3}, transformed);
    }

    @Test
    public void testTransformEntityWithParametersWhenHanaCalculationView() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithParameters.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithParameters.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true, true);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
        model.setTableType(ISqlKeywords.METADATA_CALC_VIEW);
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);

        String entityEmployee = "{\n" +
                "\t\"edmType\": \"employeeType\",\n" +
                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
                "\t\"dataStructureType\": \"CALC VIEW\",\n" +
                "\t\"companyId\": \"COMPANY_ID\",\n" +
                "\t\"employeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
                "\t\"EmployeeId\": \"EmployeeId\",\n" +
                "\t\"_parameters_\" : [\"EmployeeId\"],\n" +
                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
                "}";

        String[] actualResult = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityEmployee}, actualResult);
    }

    @Test
    public void testTransformWithCompositePrimaryKeyAndProperties() throws IOException, SQLException {
        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/Employee.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/Employee.odata", employee);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true, true);
        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);

        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true, true);
        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", true, false);
        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", true, false);
        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);

        String entityEmployee = "{\n" +
                "\t\"edmType\": \"employeeType\",\n" +
                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"companyId\": \"COMPANY_ID\",\n" +
                "\t\"employeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
                "\t\"_ref_phoneType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"COMPANY_ID\",\"EMPLOYEE_NUMBER\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
                "}";
        String phoneEntity = "{\n" +
                "\t\"edmType\": \"phoneType\",\n" +
                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
                "\t\"sqlTable\": \"PHONES\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"number\": \"NUMBER\",\n" +
                "\t\"fkCompanyId\": \"FK_COMPANY_ID\",\n" +
                "\t\"fkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
                "\t\"_ref_employeeType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"FK_COMPANY_ID\",\"FK_EMPLOYEE_NUMBER\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"NUMBER\"\n" +
                "}";
        String[] actualResult = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityEmployee, phoneEntity}, actualResult);
    }

    @Test
    public void testManyToManyMappingTableTransformation() throws IOException, SQLException {
        String users = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/users/Users.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/users/Users.odata", users);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("FIRSTNAME", "Edm.String", true, false);
        PersistenceTableModel model = new PersistenceTableModel("CVUSER", Arrays.asList(column1, column2), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("CVUSER", null)).thenReturn(model);

        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("FIRSTNAME", "Edm.String", true, false);
        model = new PersistenceTableModel("CVGROUP", Arrays.asList(column3, column4), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("CVGROUP", null)).thenReturn(model);

        String entityUser = "{\n" +
                "\t\"edmType\": \"UsersType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataUsers.UsersType\",\n" +
                "\t\"sqlTable\": \"CVUSER\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Id\": \"ID\",\n" +
                "\t\"Firstname\": \"FIRSTNAME\",\n" +
                "\t\"_ref_GroupsType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ID\"\n" +
                "\t\t],\n" +
                "\t\t\"manyToManyMappingTable\" : {\n" +
                "\t\t\t\"mappingTableName\" : \"USERSTOGROUP\",\n" +
                "\t\t\t\"mappingTableJoinColumn\" : \"UserId\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ID\"\n" +
                "}";
        String entityGroup = "{\n" +
                "\t\"edmType\": \"GroupsType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataUsers.GroupsType\",\n" +
                "\t\"sqlTable\": \"CVGROUP\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"Id\": \"ID\",\n" +
                "\t\"Firstname\": \"FIRSTNAME\",\n" +
                "\t\"_ref_UsersType\": {\n" +
                "\t\t\"joinColumn\" : [\n" +
                "\t\t\t\"ID\"\n" +
                "\t\t],\n" +
                "\t\t\"manyToManyMappingTable\" : {\n" +
                "\t\t\t\"mappingTableName\" : \"USERSTOGROUP\",\n" +
                "\t\t\t\"mappingTableJoinColumn\" : \"GroupId\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ID\"\n" +
                "}";
        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityUser, entityGroup}, transformed);
    }

    @Test
    public void testViewTransformation() throws IOException, SQLException {
        String view = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/view/View.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/view/View.odata", view);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("ZUSR_ROLE", "Edm.String", true, false);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("ZROLE_NAME", "Edm.String", true, false);
        PersistenceTableModel model = new PersistenceTableModel("UserRole", Arrays.asList(column1, column2), new ArrayList<>());
        model.setTableType(ISqlKeywords.KEYWORD_VIEW);
        when(dbMetadataUtil.getTableMetadata("UserRole", null)).thenReturn(model);

        String entityView = "{\n" +
                "\t\"edmType\": \"UserRoleType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataUserRole.UserRoleType\",\n" +
                "\t\"sqlTable\": \"UserRole\",\n" +
                "\t\"dataStructureType\": \"VIEW\",\n" +
                "\t\"ZUSR_ROLE\": \"ZUSR_ROLE\",\n" +
                "\t\"ZROLE_NAME\": \"ZROLE_NAME\",\n" +
                "\t\"keyGenerated\": \"ID\",\n" +
                "\t\"_pk_\" : \"\"\n" +
                "}";

        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{entityView}, transformed);
    }

    @Test
    public void testAggregationsTransformation() throws IOException, SQLException {
        String customer = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/customer/Customer.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/customer/Customer.odata", customer);

        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("ID", "Edm.Int32", false, true);
        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true, false);
        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("PAYMENT", "Edm.Int32", true, false);
        PersistenceTableModel model = new PersistenceTableModel("CUSTOMER", Arrays.asList(column1, column2, column3), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("CUSTOMER", null)).thenReturn(model);

        String aggregationEntity = "{\n" +
                "\t\"edmType\": \"CustomerType\",\n" +
                "\t\"edmTypeFqn\": \"org.apache.olingo.odata2.ODataCustomer.CustomerType\",\n" +
                "\t\"sqlTable\": \"CUSTOMER\",\n" +
                "\t\"dataStructureType\": \"TABLE\",\n" +
                "\t\"ID\": \"ID\",\n" +
                "\t\"NUMBER\": \"NUMBER\",\n" +
                "\t\"PAYMENT\": \"PAYMENT\",\n" +
                "\t\"aggregationType\" : \"derived\",\n" +
                "\t\"aggregationProps\" : {\n" +
                "\t\t\"NUMBER\": \"SUM\",\n" +
                "\t\t\"PAYMENT\": \"AVERAGE\"\n" +
                "\t},\n" +
                "\t\"_pk_\" : \"ID\"\n" +
                "}";

        String[] transformed = odata2ODataMTransformer.transform(definition);
        assertArrayEquals(new String[]{aggregationEntity}, transformed);
    }

//    @Test
//    public void testTransformWithCompositePrimaryKey() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeCompositePrimaryKey.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeCompositePrimaryKey.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column8 = new PersistenceTableColumnModel("FK_PHONE", "Edm.Int32", false);
//        PersistenceTableRelationModel relPhone = new PersistenceTableRelationModel("ADDRESS", "PHONES", "FK_PHONE", "ID", "CONSTRAINT_8C9F7", "CONSTRAINT_INDEX_E67");
//        model = new PersistenceTableModel("ADDRESS", Arrays.asList(column7, column8), Collections.singletonList(relPhone));
//        when(dbMetadataUtil.getTableMetadata("ADDRESS", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
//        PersistenceTableRelationModel rel = new PersistenceTableRelationModel("PHONES", "EMPLOYEES", "FK_COMPANY_ID", "COMPANY_ID", "CONSTRAINT_8C", "CONSTRAINT_INDEX_4");
//        PersistenceTableRelationModel rel2 = new PersistenceTableRelationModel("PHONES", "EMPLOYEES", "FK_EMPLOYEE_NUMBER", "EMPLOYEE_NUMBER", "CONSTRAINT_8C9", "CONSTRAINT_INDEX_43");
//        model = new PersistenceTableModel("PHONES", Arrays.asList(column3, column4, column5), Arrays.asList(rel, rel2));
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);
//
//        String entityEmployee = "{\n" +
//                "\t\"edmType\": \"employeeType\",\n" +
//                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
//                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
//                "\t\"CompanyId\": \"COMPANY_ID\",\n" +
//                "\t\"EmployeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_phoneType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"COMPANY_ID\",\"EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
//                "}";
//        String entityPhone = "{\n" +
//                "\t\"edmType\": \"phoneType\",\n" +
//                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
//                "\t\"sqlTable\": \"PHONES\",\n" +
//                "\t\"Number\": \"NUMBER\",\n" +
//                "\t\"FkCompanyId\": \"FK_COMPANY_ID\",\n" +
//                "\t\"FkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_employeeType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"FK_COMPANY_ID\",\"FK_EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_ref_addressType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"NUMBER\"\n" +
//                "}";
//
//        String entityAddress = "{\n" +
//                "\t\"edmType\": \"addressType\",\n" +
//                "\t\"edmTypeFqn\": \"np.addressType\",\n" +
//                "\t\"sqlTable\": \"ADDRESS\",\n" +
//                "\t\"Id\": \"ID\",\n" +
//                "\t\"FkPhone\": \"FK_PHONE\",\n" +
//                "\t\"_ref_phoneType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"FK_PHONE\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"ID\"\n" +
//                "}";
//        String[] actualResult = odata2ODataMTransformer.transform(definition);
//        assertArrayEquals(new String[]{entityEmployee, entityPhone, entityAddress}, actualResult);
//    }
//
//    @Test
//    public void testTransformWithCompositePrimaryKeyWhenThereIsNoFK() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeCompositePrimaryKey.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeCompositePrimaryKey.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column8 = new PersistenceTableColumnModel("FK_PHONE", "Edm.Int32", false);
//        model = new PersistenceTableModel("ADDRESS", Arrays.asList(column7, column8), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("ADDRESS", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
//        model = new PersistenceTableModel("PHONES", Arrays.asList(column3, column4, column5), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);
//
//        String entityEmployee = "{\n" +
//                "\t\"edmType\": \"employeeType\",\n" +
//                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
//                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
//                "\t\"CompanyId\": \"COMPANY_ID\",\n" +
//                "\t\"EmployeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_phoneType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"COMPANY_ID\",\"EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
//                "}";
//        String entityPhone = "{\n" +
//                "\t\"edmType\": \"phoneType\",\n" +
//                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
//                "\t\"sqlTable\": \"PHONES\",\n" +
//                "\t\"Number\": \"NUMBER\",\n" +
//                "\t\"FkCompanyId\": \"FK_COMPANY_ID\",\n" +
//                "\t\"FkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_addressType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_ref_employeeType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"FK_COMPANY_ID\",\"FK_EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"NUMBER\"\n" +
//                "}";
//
//        String entityAddress = "{\n" +
//                "\t\"edmType\": \"addressType\",\n" +
//                "\t\"edmTypeFqn\": \"np.addressType\",\n" +
//                "\t\"sqlTable\": \"ADDRESS\",\n" +
//                "\t\"Id\": \"ID\",\n" +
//                "\t\"FkPhone\": \"FK_PHONE\",\n" +
//                "\t\"_ref_phoneType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"FK_PHONE\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"ID\"\n" +
//                "}";
//        String[] actualResult = odata2ODataMTransformer.transform(definition);
//        assertArrayEquals(new String[]{entityEmployee, entityPhone, entityAddress}, actualResult);
//    }
//
//    @Test
//    public void testTransformWithCompositePrimaryKeyWithoutDefinedAllEntitiesInsideOdataFile() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeCompositePrimaryKeyWithoutEntity.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeCompositePrimaryKeyWithoutEntity.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column8 = new PersistenceTableColumnModel("FK_PHONE", "Edm.Int32", false);
//        PersistenceTableRelationModel relPhone = new PersistenceTableRelationModel("ADDRESS", "PHONES", "FK_PHONE", "ID", "CONSTRAINT_8C9F7", "CONSTRAINT_INDEX_E67");
//        model = new PersistenceTableModel("ADDRESS", Arrays.asList(column7, column8), Collections.singletonList(relPhone));
//        when(dbMetadataUtil.getTableMetadata("ADDRESS", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
//        PersistenceTableRelationModel rel = new PersistenceTableRelationModel("PHONES", "EMPLOYEES", "FK_COMPANY_ID", "COMPANY_ID", "CONSTRAINT_8C", "CONSTRAINT_INDEX_4");
//        PersistenceTableRelationModel rel2 = new PersistenceTableRelationModel("PHONES", "EMPLOYEES", "FK_EMPLOYEE_NUMBER", "EMPLOYEE_NUMBER", "CONSTRAINT_8C9", "CONSTRAINT_INDEX_43");
//        model = new PersistenceTableModel("PHONES", Arrays.asList(column3, column4, column5), Arrays.asList(rel, rel2));
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);
//
//        String entityEmployee = "{\n" +
//                "\t\"edmType\": \"employeeType\",\n" +
//                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
//                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
//                "\t\"CompanyId\": \"COMPANY_ID\",\n" +
//                "\t\"EmployeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_phoneType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"COMPANY_ID\",\"EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
//                "}";
//        String entityPhone = "{\n" +
//                "\t\"edmType\": \"phoneType\",\n" +
//                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
//                "\t\"sqlTable\": \"PHONES\",\n" +
//                "\t\"Number\": \"NUMBER\",\n" +
//                "\t\"FkCompanyId\": \"FK_COMPANY_ID\",\n" +
//                "\t\"FkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
//                "\t\"_ref_employeeType\": {\n" +
//                "\t\t\"joinColumn\" : [\n" +
//                "\t\t\t\"FK_COMPANY_ID\",\"FK_EMPLOYEE_NUMBER\"\n" +
//                "\t\t]\n" +
//                "\t},\n" +
//                "\t\"_pk_\" : \"NUMBER\"\n" +
//                "}";
//
//        String[] actualResult = odata2ODataMTransformer.transform(definition);
//        assertArrayEquals(new String[]{entityEmployee, entityPhone}, actualResult);
//    }
//
//    @Test(expected = OData2TransformerException.class)
//    public void testTransformWithCompositePrimaryKeyWhenDBIsInconsistentWithOdataFile() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeCompositePrimaryKey.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeCompositePrimaryKey.odata", employee);
//
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(new PersistenceTableModel("PHONES", new ArrayList<>(), new ArrayList<>()));
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(new PersistenceTableModel("EMPLOYEES", new ArrayList<>(), new ArrayList<>()));
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column8 = new PersistenceTableColumnModel("FK_PHONE", "Edm.Int32", false);
//        PersistenceTableRelationModel relPhone = new PersistenceTableRelationModel("ADDRESS", "PHONES", "FK_PHONE_WRONG", "ID", "CONSTRAINT_8C9F7", "CONSTRAINT_INDEX_E67");
//        PersistenceTableModel model = new PersistenceTableModel("ADDRESS", Arrays.asList(column7, column8), Collections.singletonList(relPhone));
//        when(dbMetadataUtil.getTableMetadata("ADDRESS", null)).thenReturn(model);
//
//        odata2ODataMTransformer.transform(definition);
//    }
//
//    @Test
//    public void testTransformWithCompositePrimaryKeyAndAllValidProperties() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithProp.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithProp.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
//        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);
//
//        String entityEmployee = "{\n" +
//                "\t\"edmType\": \"employeeType\",\n" +
//                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
//                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
//                "\t\"companyId\": \"COMPANY_ID\",\n" +
//                "\t\"employeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
//                "\t\"orderId\": \"ORDER_ID\",\n" +
//                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
//                "}";
//        String phoneEntity = "{\n" +
//                "\t\"edmType\": \"phoneType\",\n" +
//                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
//                "\t\"sqlTable\": \"PHONES\",\n" +
//                "\t\"Number\": \"NUMBER\",\n" +
//                "\t\"FkCompanyId\": \"FK_COMPANY_ID\",\n" +
//                "\t\"FkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
//                "\t\"_pk_\" : \"NUMBER\"\n" +
//                "}";
//        String[] actualResult = odata2ODataMTransformer.transform(definition);
//        assertArrayEquals(new String[]{entityEmployee, phoneEntity}, actualResult);
//    }
//
//    @Test
//    public void testTransformWithCompositePrimaryKeyWithLessDbPropsExposed() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithProp.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithProp.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column3 = new PersistenceTableColumnModel("ORDER_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ADDRESS_ID", "Edm.Int32", false);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2, column3, column4), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES",null)).thenReturn(model);
//
//        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("NUMBER", "Edm.Int32", true);
//        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("FK_COMPANY_ID", "Edm.Int32", false);
//        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("FK_EMPLOYEE_NUMBER", "Edm.Int32", false);
//        model = new PersistenceTableModel("PHONES", Arrays.asList(column5, column6, column7), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("PHONES", null)).thenReturn(model);
//
//        String entityEmployee = "{\n" +
//                "\t\"edmType\": \"employeeType\",\n" +
//                "\t\"edmTypeFqn\": \"np.employeeType\",\n" +
//                "\t\"sqlTable\": \"EMPLOYEES\",\n" +
//                "\t\"companyId\": \"COMPANY_ID\",\n" +
//                "\t\"employeeNumber\": \"EMPLOYEE_NUMBER\",\n" +
//                "\t\"orderId\": \"ORDER_ID\",\n" +
//                "\t\"_pk_\" : \"COMPANY_ID,EMPLOYEE_NUMBER\"\n" +
//                "}";
//        String phoneEntity = "{\n" +
//                "\t\"edmType\": \"phoneType\",\n" +
//                "\t\"edmTypeFqn\": \"np.phoneType\",\n" +
//                "\t\"sqlTable\": \"PHONES\",\n" +
//                "\t\"Number\": \"NUMBER\",\n" +
//                "\t\"FkCompanyId\": \"FK_COMPANY_ID\",\n" +
//                "\t\"FkEmployeeNumber\": \"FK_EMPLOYEE_NUMBER\",\n" +
//                "\t\"_pk_\" : \"NUMBER\"\n" +
//                "}";
//
//        String[] actualResult = odata2ODataMTransformer.transform(definition);
//        assertArrayEquals(new String[]{entityEmployee, phoneEntity}, actualResult);
//    }
//
//    @Test(expected = OData2TransformerException.class)
//    public void testTransformWithCompositePrimaryKeyAndLessNumberOfDBProps() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithProp.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithProp.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        odata2ODataMTransformer.transform(definition);
//    }
//
//    @Test(expected = OData2TransformerException.class)
//    public void testTransformWithCompositePrimaryKeyAndWrongAssProps() throws IOException, SQLException {
//        String employee = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/transformers/EmployeeWithWrongAssProps.odata"), Charset.defaultCharset());
//        ODataDefinition definition = ODataDefinitionFactory.parseOData("/transformers/EmployeeWithWrongAssProps.odata", employee);
//
//        PersistenceTableColumnModel column1 = new PersistenceTableColumnModel("COMPANY_ID", "Edm.Int32", true);
//        PersistenceTableColumnModel column2 = new PersistenceTableColumnModel("EMPLOYEE_NUMBER", "Edm.Int32", true);
//        PersistenceTableModel model = new PersistenceTableModel("EMPLOYEES", Arrays.asList(column1, column2), new ArrayList<>());
//        when(dbMetadataUtil.getTableMetadata("EMPLOYEES", null)).thenReturn(model);
//
//        odata2ODataMTransformer.transform(definition);
//    }
}