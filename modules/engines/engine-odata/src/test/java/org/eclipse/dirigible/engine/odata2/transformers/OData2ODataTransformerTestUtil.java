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
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableRelationModel;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinition;
import org.eclipse.dirigible.engine.odata2.definition.ODataDefinitionFactoryTest;
import org.eclipse.dirigible.engine.odata2.definition.factory.ODataDefinitionFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;

public class OData2ODataTransformerTestUtil {

    public static ODataDefinition loadData_testTransformEntityProperty(DBMetadataUtil dbMetadataUtil) throws SQLException, IOException {
        String orders = IOUtils.toString(ODataDefinitionFactoryTest.class.getResourceAsStream("/entityproperty/Entities.odata"), Charset.defaultCharset());
        ODataDefinition definition = ODataDefinitionFactory.parseOData("/entityproperty/Entities.odata", orders);

        PersistenceTableColumnModel column11 = new PersistenceTableColumnModel("ENTITY1ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column12 = new PersistenceTableColumnModel("PROPERTY2", "Edm.String", true, false);
        PersistenceTableColumnModel column13 = new PersistenceTableColumnModel("PROPERTY3", "Edm.String", true, false);
        PersistenceTableColumnModel column14 = new PersistenceTableColumnModel("Country.Id", "Edm.String", true, false);
        PersistenceTableModel model = new PersistenceTableModel("ENTITY1", Arrays.asList(column11, column12, column13, column14), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("ENTITY1", null)).thenReturn(model);

        PersistenceTableColumnModel column4 = new PersistenceTableColumnModel("ENTITY2ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column5 = new PersistenceTableColumnModel("PROPERTY2", "Edm.Int32", true, false);
        PersistenceTableColumnModel column6 = new PersistenceTableColumnModel("PROPERTY3", "Edm.Int32", true, false);
        PersistenceTableColumnModel column7 = new PersistenceTableColumnModel("PROPERTY4_5", "Edm.Int32", true, false);
        PersistenceTableColumnModel column8 = new PersistenceTableColumnModel("ENTITY1ENTITY1ID", "Edm.Int32", true, false);
        PersistenceTableRelationModel rel = new PersistenceTableRelationModel("ENTITY2ID", "ENTITY1ID", "ENTITY1ENTITY1ID", "ENTITY1ID", "fkName", "PRIMARY_KEY_8B");
        model = new PersistenceTableModel("ENTITY2", Arrays.asList(column13, column4, column5, column6, column7, column8), Collections.singletonList(rel));
        when(dbMetadataUtil.getTableMetadata("ENTITY2", null)).thenReturn(model);

        PersistenceTableColumnModel column24 = new PersistenceTableColumnModel("ENTITY3.ID", "Edm.Int32", true, true);
        PersistenceTableColumnModel column25 = new PersistenceTableColumnModel("NAME.ID", "Edm.String", true, false);
        model = new PersistenceTableModel("ENTITY3", Arrays.asList(column24, column25), new ArrayList<>());
        when(dbMetadataUtil.getTableMetadata("ENTITY3", null)).thenReturn(model);

        return definition;
    }
}